package com.thewizard91.thejournal.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.image.ImageModel;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;
import com.thewizard91.thejournal.models.post.PostModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private static final int CODE_IMAGE_GALLERY = 1;
    private AppCompatImageView image;
    private EditText postTitle;
    private EditText postDescription;
    private TransitionButton sendButton;
    private FirebaseFirestore cloudFirebaseDatabaseInstance;
    private DatabaseReference realtimeDatabaseReference;
    private StorageReference dataServerStorage;
    private String userId;
    private String username;
    private Uri newPostImageURI;
    private Uri image_uri;
    private String userProfileImageUri;
    String randomNameForTheNewPostImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        init();
    }

    private void init() {

        image = findViewById(R.id.post_image_id);
        postTitle = findViewById(R.id.post_title_id);
        postDescription = findViewById(R.id.post_description_id);
        sendButton = findViewById(R.id.send_button);

        dataServerStorage = FirebaseStorage.getInstance().getReference();
        cloudFirebaseDatabaseInstance = FirebaseFirestore.getInstance();
        FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            userId = currentUser.getUid();
            username = currentUser.getDisplayName();
            setUsername();
            selectImageToPost();
            sendNewPost();
        }

    }

    private void sendNewPost () {
        sendButton.setOnClickListener(v -> {
            retrieveUserData();
            sendButton.startAnimation();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                sendButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                        () -> {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        });
            }, 2000);
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
            final StorageReference pathToTheImageInFirebase = dataServerStorage.child("storage_of_"+username)
                    .child("post_images")
                    .child(randomNameForTheNewPostImage+".jpg");

            // Store the image. Go to the bottom where "storeTheInfoInsertedByTheUserInDatabase()" is.
            pathToTheImageInFirebase.putFile(newPostImageURI)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
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
                        }
                    });
        }

        storeTheInfoInsertedByTheUserInDatabase(null,
                insertTitle,
                insertDescription
        );
    }

    private void storeTheInfoInsertedByTheUserInDatabase(Task<UploadTask.TaskSnapshot> task, String insertTitle, String insertDescription) {

        final String[] downloadURI = {null};

        if (task != null) {
            task.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURI[0] = uri.toString();// for image uri
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                image_uri = Uri.parse(downloadURI[0]);
                                Log.d("userImageURI:",image_uri.toString());
                                FieldValue time = FieldValue.serverTimestamp();

                                // Create post object.
                                PostModel post_model = new PostModel(image_uri.toString(),
                                        userId,time,username,insertTitle,insertDescription,
                                        userProfileImageUri);
                                //
                                Map<String, Object>post_map = post_model.postFirebaseDatabaseMap();
                                Log.d("post_model",String.valueOf(post_model));
                                //
                                addPostToEveryUserFirebaseFireStoreSpace(post_map);

                                ImageModel image_model = new ImageModel(insertDescription,insertTitle,downloadURI[0],time);
                                Map<String, Object> image_map = image_model.imageFirebaseDatabaseMap();
                                Log.d("image_model",String.valueOf(image_model));

                                // Creating a database and store info in there for the user
                                // it will show his own post only
                                addPostToUniqueUserFirebaseFireStoreSpace(image_map);

                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime now =LocalDateTime.now();
                                String date = dateTimeFormatter.format(now).toString();

                                // Create model for a notification regarding the latest post.
                                NotificationsModel notificationsModel = new NotificationsModel(username,userId,
                                        date,userProfileImageUri,username+" has just posted.");

                                // Create post map and create the database in realtime database.
                                Map<String,Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                                addToRealtimeDatabase(mapOfRealtimeDatabase);
                            }
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

    private void addPostToUniqueUserFirebaseFireStoreSpace(Map<String, Object> image_map) {
        cloudFirebaseDatabaseInstance
                .collection("Gallery")
                .document(userId)
                .collection("images")
                .add(image_map);
    }

    private void addPostToEveryUserFirebaseFireStoreSpace(Map<String, Object> post_map) {
        cloudFirebaseDatabaseInstance.collection("Posts")
                .document(randomNameForTheNewPostImage)
                .set(post_map)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(NewPostActivity.this, "The Post was Sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("taskError",task.toString());
                        Toast.makeText(NewPostActivity.this, "FIRE-STORE ERROR"+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUsername() {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> map = documentSnapshot.getData();
                            if(map!=null) {
                                for(Map.Entry<String, Object> entry: map.entrySet()) {
                                    String entry_key = entry.getKey();
                                    if ("userProfileImageURI".equals(entry_key)) {
                                        userProfileImageUri = entry.getValue().toString();
                                    }
                                    if ("username".equals(entry_key)) {
                                        username = entry.getValue().toString();
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void selectImageToPost() {

        image.setOnClickListener(v -> startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMAGE_GALLERY));
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileImage = SAMPLE_CROPPED_IMG_NAME;
        destinationFileImage +=".jpg";

        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileImage)));

        ucrop.withAspectRatio(1f,1f);
        ucrop.withAspectRatio(3,4);
        ucrop.useSourceImageAspectRatio();
        ucrop.withAspectRatio(2,3);
        ucrop.withAspectRatio(16,9);

        ucrop.withMaxResultSize(100, 100);
        ucrop.withOptions(getOptions());
        ucrop.start(this);
    }

    public UCrop.Options getOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        // CompressType
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        // Ui
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.darker_blue));
        options.setToolbarTitle("Choose The Image For Your Profile!");

        return options;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//CAMERA_ACTION_PICK_REQUEST_CODE

        if (requestCode == CODE_IMAGE_GALLERY && resultCode == RESULT_OK) {

            assert data != null;
            newPostImageURI = data.getData();
            if(newPostImageURI!=null){
                startCrop(newPostImageURI);
            }

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {

            assert data != null;
            Uri imageUriResultCrop = UCrop.getOutput(data);
            if (imageUriResultCrop != null) {
                image.setImageURI(imageUriResultCrop);
            }
        }
    }

}