package com.thewizard91.thejournal.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.sing_up_adds_on.UCropperActivity;
import com.thewizard91.thejournal.classes.UserInfo;
import com.thewizard91.thejournal.models.image.ImageModel;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;
import com.thewizard91.thejournal.models.post.PostModel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    private static final int CODE_IMAGE_GALLERY = 1;
    private AppCompatImageView showImage;
    private EditText postTitle;
    private EditText postDescription;
    private TransitionButton sendButton;
    private FirebaseFirestore cloudFirebaseDatabaseInstance;
    private DatabaseReference realtimeDatabaseReference;
    private StorageReference dataServerStorage;
    private String userId;
    private String username;
    private Uri newPostImageURI;
    private Uri imageUri;
    private String userProfileImageURI;
    private String randomNameForTheNewPostImage;
    private ActivityResultLauncher<String> cropImage;
    private MaterialButton chooseImageButton;
    private UserInfo userInfo;
    private String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        init();
    }

    private void init() {

        showImage = findViewById(R.id.show_post_image_id);
        postTitle = findViewById(R.id.post_title_id);
        postDescription = findViewById(R.id.post_description_id);
        sendButton = findViewById(R.id.send_button);
        chooseImageButton = findViewById(R.id.post_image_id);

        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(NewPostActivity.this.getApplicationContext(), UCropperActivity.class);
            intent.putExtra("SendImageData",result.toString());
            startActivityForResult(intent, CODE_IMAGE_GALLERY);
        });

        dataServerStorage = FirebaseStorage.getInstance().getReference();
        cloudFirebaseDatabaseInstance = FirebaseFirestore.getInstance();
        FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            userId = currentUser.getUid();
            username = currentUser.getDisplayName();

//            userInfo = new UserInfo(cloudFirebaseDatabaseInstance,userId,"numberOfPosts");
//            userInfo.useUserInfoCallBack();
//            username = userInfo.getUsername();
//            userProfileImageURI = userInfo.getUserProfileImageUri();
            setUsernameAndUserImageProfileURI();
            selectImageToPost();
            sendNewPost();
            userInfo = new UserInfo(cloudFirebaseDatabaseInstance,userId,"numberOfPosts");
            userInfo.useGalleryCallBack();
        }

    }

    private void sendNewPost () {
        sendButton.setOnClickListener(v -> {
            retrieveUserData();
            sendButton.startAnimation();
            final Handler handler = new Handler();
            handler.postDelayed(() -> sendButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                    () -> {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }), 2000);
        });
    }

    private void retrieveUserData() {

        // Retrieve info as the user is typing title and description.
        final String insertTitle = postTitle.getText().toString();
        final String insertDescription = postDescription.getText().toString();

        // Check the entries are filled.
        if(!TextUtils.isEmpty(insertTitle) && !TextUtils.isEmpty(insertDescription)) {
            // Create a random ui sequence (a name) for image that is about to be
            // stored in the storage database.
            randomNameForTheNewPostImage = UUID.randomUUID().toString();

            // Setting image in storage database.
            final StorageReference pathToTheImageInFirebase = dataServerStorage.child(userId)
                    .child("postImages")
                    .child(randomNameForTheNewPostImage+".jpg");

            // Store the image. Go to the bottom where "storeTheInfoInsertedByTheUserInDatabase()" is.
            pathToTheImageInFirebase.putFile(newPostImageURI)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            try {
                                //Reducing size of image in terms of memory.
                                new Compressor(NewPostActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(2)
                                        .compressToBitmap(new File(pathToTheImageInFirebase.getPath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Calling helper method for storing data in storage database.
                            storeTheInfoInsertedByTheUserInDatabase(task,
                                    insertTitle,
                                    insertDescription
                            );
                        }
                    });
        }
    }

    private void storeTheInfoInsertedByTheUserInDatabase(Task<UploadTask.TaskSnapshot> task, String insertTitle, String insertDescription) {

        final String[] downloadURI = {null};

        if (task != null) {
            task.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        downloadURI[0] = uri.toString();// for image uri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            imageUri = Uri.parse(downloadURI[0]);

                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime localDateTime = LocalDateTime.now();
                            String time = localDateTime.format(dateTimeFormatter);

                            /*TODO: Create fields numberOfPosts, numberOfComments,and numberOfLikes.
                            *  Then update the data according to the user's activities.*/

                            // Create post object.
                            PostModel postModel = new PostModel(imageUri.toString(),
                                    userId, time, username, insertTitle, insertDescription,
                                    userProfileImageURI);

                            // After creating the model for the post, populate the model to a map.
                            Map<String, Object> postMap = postModel.postFirebaseDatabaseMap();

                            //
                            addPostToEveryUserFirebaseFireStoreSpace(postMap);

                            ImageModel imageModel = new ImageModel(insertDescription,insertTitle,downloadURI[0],time);
                            Map<String, Object> imageMap = imageModel.imageFirebaseDatabaseMap();

                            // Creating a database and store info in there for the user
                            // it will show his own post only
                            addPostToUniqueUserFirebaseFireStoreSpace(imageMap);

                            // Create model for a notification regarding the latest post.
                            NotificationsModel notificationsModel = new NotificationsModel(username,userId,
                                    time,userProfileImageURI,username+" has just posted.");

                            // Create post map and create the database in realtime database.
                            Map<String,Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                            addToRealtimeDatabase(mapOfRealtimeDatabase);
                        }
                    }));
        } else {
            downloadURI[0] = newPostImageURI.toString();
        }
    }

    private void addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase) {
        realtimeDatabaseReference.child("Notifications")
                .child(randomNameForTheNewPostImage)
                .setValue(mapOfRealtimeDatabase);
    }

    private void addPostToUniqueUserFirebaseFireStoreSpace(Map<String, Object> imageMap) {
        cloudFirebaseDatabaseInstance
                .collection("Gallery")
                .document(userId)
                .collection("images")
                .add(imageMap);
    }

    private void addPostToEveryUserFirebaseFireStoreSpace(Map<String, Object> postMap) {
        cloudFirebaseDatabaseInstance.collection("Posts")
                .document(randomNameForTheNewPostImage)
                .set(postMap)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(NewPostActivity.this, "The Post was Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("taskError",task.toString());
                        Toast.makeText(NewPostActivity.this, "FIRE-STORE ERROR"+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUsernameAndUserImageProfileURI() {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> map = documentSnapshot.getData();
                            if(map != null) {
                                for(Map.Entry<String, Object> entry: map.entrySet()) {
                                    String entryKey = entry.getKey();
                                    String entryKeyValue = entry.getValue().toString();
                                    if (Objects.equals(entryKey, "username")) {
                                        username = entryKeyValue;
                                    }
                                    if (Objects.equals(entryKey, "userProfileImageURI")) {
                                        userProfileImageURI = entryKeyValue;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void selectImageToPost() {
        chooseImageButton.setOnClickListener(v -> _imagePermission());
    }

    private void _imagePermission() {
        Dexter.withContext(NewPostActivity.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(NewPostActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        cropImage.launch("image/*");
                        //READ_EXTERNAL_STORAGE
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(NewPostActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_IMAGE_GALLERY && resultCode == 101) {
            assert data != null;
            String result = data.getStringExtra("CROP");
            Uri uri = data.getData();
            if (result != null) {
                uri = Uri.parse(result);
            }
            showImage.setImageURI(uri);
            newPostImageURI = uri;
        }
    }

}