package com.thewizard91.thejournal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thewizard91.thejournal.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static java.io.File.createTempFile;

public class AddANewPost extends AppCompatActivity {

    private static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private static final int CODE_IMAGE_GALLERY = 1;
    private AppCompatImageView image;
    private EditText postTitle;
    private EditText postDescription;
    private TransitionButton sendButton;

    private FirebaseFirestore cloudFirebaseDatabaseInstance;
    private StorageReference dataServerStorage;
    private FirebaseUser currentUser;

    private String randomName;
    private String userId;
    private String username;

    private Bitmap compressedImageFile;
    private Uri newPostImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_new_post);
        
        init();
    }

    private void init() {

        image = findViewById(R.id.post_image_id);
        postTitle = findViewById(R.id.post_title_id);
        postDescription = findViewById(R.id.post_description_id);
        sendButton = findViewById(R.id.send_button);

        dataServerStorage = FirebaseStorage.getInstance().getReference();
        cloudFirebaseDatabaseInstance = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            userId = currentUser.getUid();
            Log.d("userIdIs:", userId);//TO5yVD9LPKgecQGRz9ucLnvpxxx1
            setUsername();
            selectImageToPost();
            sendNewPost();
        }
//        sendToLogInActivity();
    }

    private void sendNewPost(){
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
        // Get the information inserted from the user and create a database table for it.

        // Retrieve info
        final String insertTitle = postTitle.getText().toString();
        final String insertDescription = postDescription.getText().toString();

        // Add user interests in here.
        String randomNameForTheNewPostImage = UUID.randomUUID().toString();

        if(!TextUtils.isEmpty(insertTitle) && !TextUtils.isEmpty(insertDescription)) {
            final StorageReference pathToTheImageInFirebase = dataServerStorage.child("storage_of_"+username)
                    .child("post_images")
                    .child(randomNameForTheNewPostImage+".jpg");

            pathToTheImageInFirebase.putFile(newPostImageURI)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    new Compressor(AddANewPost.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(new File(pathToTheImageInFirebase.getPath()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // TODO: Add location and time into database no need to display it to the user.
                                // However, it is nice to stare such data into the database.
                                storeTheInfoInsertedByTheUserInDatabase(task, insertTitle, insertDescription);
                            }
                        }
                    });
        }

        storeTheInfoInsertedByTheUserInDatabase((Task<UploadTask.TaskSnapshot>) null, insertTitle, insertDescription);
    }

    private void storeTheInfoInsertedByTheUserInDatabase(Task<UploadTask.TaskSnapshot> task, String insertTitle, String insertDescription) {

        final String[] downloadURI = {null};

        if (task!=null) {
            task.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURI[0] = uri.toString();
                            makeTheMap(downloadURI[0], insertTitle, insertDescription);
                        }
                    }));
        } else {
            downloadURI[0] = newPostImageURI.toString();
        }
        makeTheMap(downloadURI[0], insertTitle, insertDescription);
    }

    private void makeTheMap(String s, String insertTitle, String insertDescription) {

        Map<String, String> postMap = new HashMap<>();
        postMap.put("post", s);
        postMap.put("title", insertTitle);
        postMap.put("description", insertDescription);

        cloudFirebaseDatabaseInstance.collection("Pots")
                .document(userId)
                .set(postMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
//                            sendToMainActivity();
                            Toast.makeText(AddANewPost.this, "The Post was Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddANewPost.this, "FIRE-STORE ERROR"+task.getException(), Toast.LENGTH_SHORT).show();
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
                        if(documentSnapshot.exists()) {
                            List<String> list = new ArrayList<>();
                            Map<String, Object> map = documentSnapshot.getData();
                            if(map!=null) {
                                for(Map.Entry<String, Object> entry: map.entrySet()) {
                                    list.add(entry.getValue().toString());
                                }
                                username = list.get(0); //You can use if for the main activity!
                                Log.d("usernameIs:", username);
                            }
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