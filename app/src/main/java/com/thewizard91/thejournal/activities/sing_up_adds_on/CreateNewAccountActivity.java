package com.thewizard91.thejournal.activities.sing_up_adds_on;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
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
import com.thewizard91.thejournal.activities.LogInActivity;
import com.thewizard91.thejournal.activities.NewPostActivity;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;
import com.thewizard91.thejournal.models.user.UserModel;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;


public class CreateNewAccountActivity extends AppCompatActivity {
    private static final int CODE_IMAGE_GALLERY = 1;
    private ShapeableImageView userImage;
    private MaterialButton addPhoto;
    private TextInputEditText username;
    private TextInputEditText description;
    private TextInputEditText phoneNumber;
    private TextInputEditText address;
    private RadioGroup radioButtons;
    private TransitionButton readyButton;
    private Uri userImageUri;
    private FirebaseFirestore cloudBaseDatabaseInstance;
    private StorageReference cloudStorageInstance;
    private DatabaseReference realtimeDatabaseReference;
    private String userId;
    private String gender = "user's gender currently not available";
//    private ActivityMainBinding binding;
    private ActivityResultLauncher<String> cropImage;

//    https://www.youtube.com/watch?v=GShdLXQiBxs&t=458s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        //
        init();
        // Set up the user image
        setUpTheImageForTheUser();
        // Handle the ready button
        triggerTheReadyTransactionButton();
    }

    public void init(){

        // Instantiating .XML components
        // .XML Components
        userImage = findViewById(R.id.user_image_display);
        addPhoto = findViewById(R.id.add_photo);
        username = findViewById(R.id.account_settings_enter_username_id);
        description = findViewById(R.id.account_settings_user_description_id);
        phoneNumber = findViewById(R.id.account_settings_user_phone_number_id);
        address = findViewById(R.id.account_settings_user_address_id);
        radioButtons = findViewById(R.id.account_settings_group_button_id);
        readyButton = findViewById(R.id.account_settings_set_up_id);
        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(CreateNewAccountActivity.this.getApplicationContext(), UCropperActivity.class);
            intent.putExtra("SendImageData",result.toString());
            startActivityForResult(intent, CODE_IMAGE_GALLERY);
        });

        // Instantiating Firebase Database components.
        // Firebase instances
        FirebaseAuth userAuthorization = FirebaseAuth.getInstance();
        cloudBaseDatabaseInstance = FirebaseFirestore.getInstance();
        cloudStorageInstance = FirebaseStorage.getInstance().getReference();
        FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();

        // Instantiation of Java Objects
        userImageUri = null;
        userId = Objects.requireNonNull(userAuthorization.getCurrentUser()).getUid();

    }

    private void _addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase) {
        String notificationUri = UUID.randomUUID().toString();
        realtimeDatabaseReference.child("Notifications")
                .child(notificationUri)
                .setValue(mapOfRealtimeDatabase);
    }
    private void setUpTheImageForTheUser() {
        addPhoto.setOnClickListener(v -> _imagePermission());
    }

    private void _imagePermission() {
        Dexter.withContext(CreateNewAccountActivity.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(CreateNewAccountActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(CreateNewAccountActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void triggerTheReadyTransactionButton() {
        readyButton.setOnClickListener(v -> {

            // Get gender
            addListenerRadioButton();

            // Retrieve information inserted by the user in the Text boxes.
            retrieveUserDataAndSendItToDatabase();

            // Start the loading animation when the user tap the button
            readyButton.startAnimation();

            // Do your networking task or background work here.
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                // Choose a stop animation if your call was successful or not
                readyButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                        () -> {
                            Intent intent = new Intent(getBaseContext(), LogInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        });
            }, 2000);
        });
    }

    private void retrieveUserDataAndSendItToDatabase() {
        /*Helper method of retrieve information entered by the user so that we can populate the database
        that we are about to create and put them in the Storage.*/

        /*TODO: Check user's inputs.*/
        // Retrieve info inserted by the user.
        final String userName = Objects.requireNonNull(username.getText()).toString();
        final String userDescription = Objects.requireNonNull(description.getText()).toString();
        final String userPhoneNumber = Objects.requireNonNull(phoneNumber.getText()).toString();
        final String userAddress = Objects.requireNonNull(address.getText()).toString();
        final String userGender = gender;
        
        _createDatabase(userName, userDescription, userPhoneNumber, userAddress, userGender);
        }

    private void _createDatabase(String userName, String userDescription, String userPhoneNumber, String userAddress, String userGender) {
        String randomNameForUserProfileImage = UUID.randomUUID().toString();
        // Putting the image to the Firebase Storage. Look at "storage_of_userName"
        final StorageReference pathToTheUserProfileImageInFirebase = cloudStorageInstance.child(userId)
                .child("profileImages")
                .child(randomNameForUserProfileImage + ".jpg");
        // Saving the Path where the image profile has
        // been sent so that we can add more info about the user
        // as well as their image profile in.jpg format.
        pathToTheUserProfileImageInFirebase.putFile(userImageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Wrapping the image compressor object in
                        // an exception because it fixed the bug.
                        try {
                            // Compress the quality of the image
                            new Compressor(CreateNewAccountActivity.this)
                                    .setMaxWidth(100)
                                    .setMaxHeight(100)
                                    .setQuality(2)
                                    .compressToBitmap(new File(pathToTheUserProfileImageInFirebase.getPath()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("ErrorDetected:", e.toString());
                        }
                        // Make the map - make and populate the table.
                        _storeUserInfoFieldsInFireStore(task,
                                userName,
                                userDescription,
                                userPhoneNumber,
                                userAddress,
                                userGender);
                    } else {
                        Toast.makeText(CreateNewAccountActivity.this,
                                "IMAGE ERROR: " + ((Exception) Objects.requireNonNull(task.getException()))
                                        .getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addListenerRadioButton() {
        int selectId = radioButtons.getCheckedRadioButtonId();
        RadioButton genderRadioButton = findViewById(selectId);
        gender = genderRadioButton.getText().toString();
    }
    private void _storeUserInfoFieldsInFireStore(Task<UploadTask.TaskSnapshot> task,
                                                 String userName,
                                                 String userDescription,
                                                 String userPhoneNumber,
                                                 String userAddress,
                                                 String userGender) {
        final String[] downloadURICopy = new String[]{null};
        task.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage()
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    downloadURICopy[0] = uri.toString();
                    UserModel userModel = new UserModel("email", userName, downloadURICopy[0],
                            userPhoneNumber, userDescription, userAddress, userGender,
                            "user's age currently not available", "0", "0",
                            "0");
                    Map<String, Object> userData = userModel.userDatabase();
                    _setDatabase(userData);

                    DateTimeFormatter dateTimeFormatter = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    }
                    LocalDateTime now = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        now = LocalDateTime.now();
                    }
                    String date = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        date = dateTimeFormatter.format(now);
                    }

                    NotificationsModel notificationsModel = new NotificationsModel();
                    notificationsModel.setUsername(String.valueOf(username));
                    notificationsModel.setUserId(userId);
                    notificationsModel.setDate(date);
                    notificationsModel.setUserProfileImageURI(downloadURICopy[0]);
                    notificationsModel.setNotificationText("Welcome " + username);
                    Map<String, Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                    _addToRealtimeDatabase(mapOfRealtimeDatabase);
                }));
    }
    private void _setDatabase(Map<String,Object> userMap) {
        // Now populating the fields.
        cloudBaseDatabaseInstance.collection("Users")
                .document(userId)
                .set(userMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendToLoginActivity();
                        Toast.makeText(CreateNewAccountActivity.this,
                                "The user's settings are updated!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateNewAccountActivity.this,
                                "FIRE-STORE ERROR" + Objects.requireNonNull(task.getException())
                                        .getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendToLoginActivity() {
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }
    private void showImage(Uri imageUri) {
        /*Show cropped image inside the imageView by getting the imageUri.*/
        userImage.setImageURI(imageUri);
        userImageUri = imageUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        https://www.youtube.com/watch?v=GShdLXQiBxs&t=1070s
        if (requestCode == CODE_IMAGE_GALLERY && resultCode == 101) {
            assert data != null;
            String result = data.getStringExtra("CROP");
            Uri uri = data.getData();
            if (result != null) {
                uri = Uri.parse(result);
            }
            showImage(uri);
        }
    }
}