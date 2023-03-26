package com.thewizard91.thejournal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
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
import com.thewizard91.thejournal.models.notifications.NotificationsModel;
import com.thewizard91.thejournal.models.post.PostModel;
import com.thewizard91.thejournal.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import id.zelory.compressor.Compressor;

import static java.io.File.createTempFile;

public class NewPostActivity extends AppCompatActivity {

    private static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private static final int CODE_IMAGE_GALLERY = 1;
    private AppCompatImageView image;
    private EditText postTitle;
    private EditText postDescription;
    private TransitionButton sendButton;
    private FirebaseFirestore cloudFirebaseDatabaseInstance;
    private FirebaseDatabase realtimeDatabase;
    private DatabaseReference realtimeDatabaseReference;
    private StorageReference dataServerStorage;
    public String randomName;
    private String userId;
    private String username;
    // GBet APi to locate the user
    private String location;
    private Bitmap compressedImageFile;
    private Uri newPostImageURI;
    private Uri userImageURI;
    private String userProfileImageUri;
    String randomNameForTheNewPostImage;

    private MainActivity mainActivity;

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
        realtimeDatabase = FirebaseDatabase.getInstance();
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

    private void sendNewPost(){
        /**Sending data to the database upon click*/
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveUserData();
                sendButton.startAnimation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = true;
                        if (isSuccessful) {
                            sendButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                    new TransitionButton.OnAnimationStopEndListener() {
                                        @Override
                                        public void onAnimationStopEnd() {
                                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            sendButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }

    private void retrieveUserData() {
        /**Get the information inserted from the user and create a database table for it.
         * That way (creating the database (the map) we store it into firebase database.*/

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
                                        insertDescription,
                                        randomNameForTheNewPostImage);
                            }
                        }
                    });
        }

        storeTheInfoInsertedByTheUserInDatabase(null,
                insertTitle,
                insertDescription,
                randomNameForTheNewPostImage);
    }

    private void storeTheInfoInsertedByTheUserInDatabase(Task<UploadTask.TaskSnapshot> task, String insertTitle, String insertDescription, String i) {

        final String[] downloadURI = {null};

        if (task!=null) {
            task.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURI[0] = uri.toString();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                userImageURI = uri.parse(downloadURI[0]);
                                FieldValue time = FieldValue.serverTimestamp();

                                //
                                PostModel postModel = new PostModel(downloadURI[0],userId,time,username,insertTitle,insertDescription,userProfileImageUri);
                                Map<String,Object>postMap = postModel.firebaseDatabaseMap();

                                //
                                addPostToEveryUserFirebaseFireStoreSpace(postMap);

                                // Creating a database and store info in there for the user
                                // it will show his own post only
                                addPostToUniqueUserFirebaseFireStoreSpace(postMap);

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
//                .child(userId)
                .child(randomNameForTheNewPostImage)
                .setValue(mapOfRealtimeDatabase);
    }

    private void addPostToUniqueUserFirebaseFireStoreSpace(Map<String, Object> postMap) {
        cloudFirebaseDatabaseInstance
                .collection("Gallery")
                .document("gallery_document_of:" + userId)
                .collection("gallery_collection_of:" + userId)
                .document("images_from_posts")
                .collection("posts")
                .add(postMap);

        cloudFirebaseDatabaseInstance
                .collection("Gallery")
                .document("gallery_document_of:" + userId)
                .collection("images")
                .add(postMap);
    }

    private void addPostToEveryUserFirebaseFireStoreSpace(Map<String, Object> postMap) {
        cloudFirebaseDatabaseInstance.collection("Posts")
                .document(randomNameForTheNewPostImage)
                .set(postMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(NewPostActivity.this, "The Post was Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("taskError",task.toString());
                            Toast.makeText(NewPostActivity.this, "FIRE-STORE ERROR"+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUsername() {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(task.isSuccessful()) { //documentSnapshot.exists()
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
//                                Log.d("Here in if", String.valueOf(document.getData()));
                                List<String> list = new ArrayList<>();
                                Map<String, Object> map = documentSnapshot.getData();
                                if(map!=null) {
                                    for(Map.Entry<String, Object> entry: map.entrySet()) {
                                        list.add(entry.getValue().toString());
//                                        Log.d("entry123",entry.getKey());
                                        String entry_key = entry.getKey();
                                        if ("userProfileImageUri".equals(entry_key)) {
//                                            Log.d("entry_key==pi","yes");
                                            userProfileImageUri = entry.getValue().toString();
                                        }
                                        if ("username".equals(entry_key)) {
//                                            Log.d("entry_key==un","yes");
                                            username = entry.getValue().toString();
                                        }
                                    }
                                }
                            } else {
//                                Log.d("Here in else", "error");
                            }
                        } else {
//                            Log.d("in else 2,", String.valueOf(task.getException()));
                        }
                    }
                });
    }

    private void selectImageToPost() {

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMAGE_GALLERY);
            }
        });
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
//        options.setCompressionQuality(70);
        // CompressType
//        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        // Ui
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.darker_blue));
        options.setToolbarTitle("Choose The Image For Your Profile!");

        return options;
    }

    private File getImageFile() {
        /*
          Returns a new File in the external storage directory with .jpg extension
         */
        String imageFileName = "/JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        File file = null;
        try {
            file = createTempFile(imageFileName, ".jpg", new File("C:" + storageDir.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ErrorDetected:", e.toString());
        }

        // currentPhotoPath is where the image is stored ones taken by the user.
        String currentPhotoPath = "file:" + Objects.requireNonNull(file).getAbsolutePath();
        return file;
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