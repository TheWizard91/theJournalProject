package com.thewizard91.thejournal.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.thewizard91.thejournal.models.notifications.NotificationsModel;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class UpdateAccountActivity extends AppCompatActivity {
    private static final int CODE_IMAGE_GALLERY = 1;

//    private ShapeableImageView showImage;
    private AppCompatImageView showImage;
    private MaterialButton addNewProfileImage;
    private TextInputEditText newUserName;
    private TextInputEditText newEmail;
    private TextInputEditText newPassword;
    private TextInputEditText newPhoneNumber;
    private TextInputEditText newAddress;
    private TransitionButton saveButton;
    private FirebaseAuth userAuthorized;
//    private FirebaseFirestore cloudFirebaseDatabaseInstance;
//    private StorageReference cloudStorageInstance;
//    private DatabaseReference realtimeDatabase;
    private String uId;
    private ActivityResultLauncher<String> cropImage;
    private Uri newProfileImageURI;
    private String currentName;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        showImage = findViewById(R.id.new_image);
        addNewProfileImage = findViewById(R.id.add_new_image);
        newUserName = findViewById(R.id.new_username);
        newEmail = findViewById(R.id.new_email);
        newPassword = findViewById(R.id.new_password);
        newPhoneNumber = findViewById(R.id.new_phone_number);
        newAddress = findViewById(R.id.new_address);
        saveButton = findViewById(R.id.save);
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(UpdateAccountActivity.this.getApplicationContext(), UCropperActivity.class);
            intent.putExtra("SendImageData", result.toString());
            startActivityForResult(intent, CODE_IMAGE_GALLERY);
        });
        newProfileImageURI = null;
        userAuthorized = FirebaseAuth.getInstance();
//        cloudFirebaseDatabaseInstance = FirebaseFirestore.getInstance();
//        uId = userAuthorized.getUid();
//        cloudStorageInstance = FirebaseStorage.getInstance().getReference();
//        realtimeDatabase = FirebaseDatabase.getInstance().getReference();

        // Need user's old name.
//        useGetUsernameCallback();
        // Image picker
        setUpTheImageForTheUser();
        // Set updates to database.
        updateUserInfoInDatabase();
    }

//    private void useGetUsernameCallback () {
//        getUsername (e -> cloudFirebaseDatabaseInstance.collection("Users")
//                .document(uId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
////                    Log.d("currentName1",(String) documentSnapshot.get("username"));
//                    currentName = (String) documentSnapshot.get("username");
////                    Log.d("currentName",currentName);
////                    String oldProfileImagePath = cloudStorageInstance.child("storage_of_"+currentName+"/profile_images/").list(1).toString();
////                    Log.d("oldProfileImagePath",oldProfileImagePath);
////                    String currentImageProfileUri = String.valueOf(documentSnapshot.get("userProfileImageURI"));
////                    Log.d("currentImageProfileUri",currentImageProfileUri);
//////                    cloudStorageInstance.child("storage_of_"+currentName+"/profile_images/").putFile(Uri.parse("F09ba54a8-a010-4ac6-8e9e-d5b1dabcac66.jpg".toLowerCase()));
////                    Uri s = Uri.parse("F09ba54a8-a010-4ac6-8e9e-d5b1dabcac66.jpg".toLowerCase());
////                    cloudStorageInstance.child("storage_of_"+currentName).child("profile_images").putFile(s);
//                })
//        );
//    }
//
//    private void getUsername (getUsernameCallback myCallback) {
//        myCallback.onGetUsername("");
//    }
//
//    private interface getUsernameCallback {
//        void onGetUsername(String un);
//    }

    private void setUpTheImageForTheUser () {
        addNewProfileImage.setOnClickListener(view -> _imagePermission());
    }

    private void _imagePermission() {
        Dexter.withContext(UpdateAccountActivity.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(UpdateAccountActivity.this,"permission Granted",Toast.LENGTH_SHORT).show();
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(UpdateAccountActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    private void updateUserInfoInDatabase () {
        saveButton.setOnClickListener(view -> {
            updateDatabase();
            saveButton.startAnimation();
            final Handler handler = new Handler();
            handler.postDelayed(() ->
                    saveButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,this::sendToLoginActivity),200);
        });
    }

    private void updateDatabase() {
        final String username = Objects.requireNonNull(newUserName.getText()).toString();
        final String email = Objects.requireNonNull(newEmail.getText()).toString();
        final String password = Objects.requireNonNull(newPassword.getText()).toString();
        final String phoneNumber = Objects.requireNonNull(newPhoneNumber.getText()).toString();
        final String address = Objects.requireNonNull(newAddress.getText()).toString();

        // Update Firebase database
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(address)) {
            // Updating author's email and password.
            FirebaseUser currentUser = userAuthorized.getCurrentUser();
            assert currentUser != null;
            currentUser.updateEmail(email);
            currentUser.updatePassword(password);

            String generatedURI = UUID.randomUUID().toString();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(currentUser.getUid() + "/profileImages/" + generatedURI + ".jpg")
                    .putFile(newProfileImageURI)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            try {
                                new Compressor(UpdateAccountActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(2)
                                        .compressToBitmap(new File(storageReference.getPath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Update user's info.
//                            Log.d("potato",task.toString());
                            updateUserTable(task, currentUser, username, phoneNumber, address);
                        }
                    });
        }
    }

    private void updateUserTable(Task<UploadTask.TaskSnapshot> task, FirebaseUser currentUser, String username, String phoneNumber, String address) {
        if (task != null) {
//            Log.d("potato","1");
            task.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
//                        Log.d("potato","2");
                        final String[] downloadURI = new String[] { uri.toString() };
                        final Uri userProfileImageURI = Uri.parse(downloadURI[0]);

                        // Updating firebase.
                        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
                        databaseReference.collection("Users")
                                .document(currentUser.getUid())
                                .update("username",username,"phoneNumber",phoneNumber,"address",address,"userProfileImageURI",userProfileImageURI.toString());

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            // Create date for notifications.
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime localDateTime = LocalDateTime.now();
                            String time = localDateTime.format(dateTimeFormatter);

                            // Create model for a notification regarding the latest post.
                            NotificationsModel notificationsModel = new NotificationsModel(username,currentUser.getUid(),
                                    time,userProfileImageURI.toString(),username+" has just posted.");

                            // Create post map and create the database in realtime database.
                            Map<String,Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                            addToRealtimeDatabase(mapOfRealtimeDatabase);
                        }
                    }));
        }
    }

    private void addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase) {
        String generatedURI = UUID.randomUUID().toString();
        DatabaseReference realtimeDatabase = FirebaseDatabase.getInstance().getReference();
        realtimeDatabase.child("Notifications")
                .child(generatedURI)
                .setValue(mapOfRealtimeDatabase);
    }

    private void sendToLoginActivity() {
//        userAuthorized.signOut();
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_IMAGE_GALLERY && resultCode == 101) {
            String result = data.getStringExtra("CROP");
            Uri uri = data.getData();
            if (result != null) {
                uri = Uri.parse(result);
            }
            showImage.setImageURI(uri);
            newProfileImageURI = uri;
        }
    }
}