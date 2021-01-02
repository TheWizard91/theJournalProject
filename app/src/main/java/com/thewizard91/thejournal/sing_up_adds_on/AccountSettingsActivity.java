package com.thewizard91.thejournal.sing_up_adds_on;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thewizard91.thejournal.MainActivity;
import com.thewizard91.thejournal.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static java.util.jar.Pack200.Packer.ERROR;

public class AccountSettingsActivity extends AppCompatActivity {

    // .XML Components
    private ContentLoadingProgressBar progressBar;
    private ImageView userImage;
    private EditText username;
    private EditText description;
    private EditText phoneNumber;
    private EditText address;
    private RadioGroup radioButtons;
    private RadioButton getMale, getFemale;
    private TransitionButton readyButton;

    // Firebase instances
    FirebaseAuth userAuthorization;
    FirebaseFirestore cloudBaseDatabaseInstance;
    StorageReference cloudStorageInstance;

    // Java Objects;
    private boolean isTheUserChanged = false;
    private Uri userImageUri;
    private String userId;
    private String gender="user's gender currently not available";
    private String age="user's age currently not available";
    private String[] interests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        // Instantiating .XML components
        progressBar = findViewById(R.id.account_settings_progress_bar);
        userImage = (ImageView) findViewById(R.id.account_settings_user_account_image_id);
        username = findViewById(R.id.account_settings_enter_username_id);
        description = findViewById(R.id.account_settings_user_description_id);
        phoneNumber = findViewById(R.id.account_settings_user_phone_number_id);
        address = findViewById(R.id.account_settings_user_address_id);
        radioButtons = findViewById(R.id.account_settings_group_button_id);
        getMale = findViewById(R.id.account_settings_radio_button_one_id);
        getFemale = findViewById(R.id.account_settings_radio_button_two_id);
        readyButton = findViewById(R.id.account_settings_set_up_id);

        // Instantiating Firebase Database components.
        userAuthorization = FirebaseAuth.getInstance();
        cloudBaseDatabaseInstance = FirebaseFirestore.getInstance();
        cloudStorageInstance = FirebaseStorage.getInstance().getReference();

        // Instantiation of Java Objects
        userImageUri = null;
        userId = String.valueOf(userAuthorization.getCurrentUser());

        // Create the User in Cloud Fire-store
        createTheUserDatabaseTable();

        // Set up the user image
        setUpTheImageForTheUser();

        // Handle the ready button
        triggerTheEffectOfTheTransactionButton();

    }

    private void setUpTheImageForTheUser() {
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(Build.VERSION.SDK_INT<23){
//                    _selectAndCropImage();
//                } else if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this,
//                        "android.permission.READ_EXTERNAL_STORAGE")!=0){
//                    ActivityCompat.requestPermissions(AccountSettingsActivity.this,
//                            new String[] {"android.permission.READ_EXTERNAL_STORAGE"},
//                            1);
//                    Toast.makeText(AccountSettingsActivity.this,
//                            "Permission Denied", Toast.LENGTH_LONG).show();
//                } else {
                    _selectAndCropImage();
//                }
            }
        });
    }

    private void _selectAndCropImage() {
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setMinCropResultSize(512, 512)
//                .setAspectRatio(1, 1)
//                .start(this);
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==203){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==-1){
                assert result != null;
                Uri uri = result.getUri();
                userImageUri = uri;
                userImage.setImageURI(uri);
                isTheUserChanged = true;
            } else if (resultCode == 204) {
                assert result != null;
                Toast.makeText(this,
                        ""+result.getError(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void createTheUserDatabaseTable() {
        cloudBaseDatabaseInstance.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint({"WrongConstant", "CheckResult", "ShowToast"})
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(AccountSettingsActivity.this,
                                    "FIREBASE RETRIEVE ERROR: "+((Exception)Objects.requireNonNull(task.getException()))
                                            .getMessage(), Toast.LENGTH_LONG).show();
                        } else if (task.getResult().exists()){

                            String userProfileImage = task.getResult().getString("user_profile_image");
                            String userProfileName = task.getResult().getString("username");
                            userImageUri = Uri.parse(userProfileImage);
                            username.setText(userProfileName);

                            RequestOptions placeHolderRequest = new RequestOptions();
                            placeHolderRequest.placeholder(R.mipmap.baseline_account_circle_black_24dp);
                            Glide.with((FragmentActivity)AccountSettingsActivity.this)
                                    .setDefaultRequestOptions(placeHolderRequest)
                                    .load(userProfileImage)
                                    .into(userImage);
                        } else {
                            Toast.makeText(AccountSettingsActivity.this,
                                    "The Data Not Exists", Toast.LENGTH_LONG).show();
                        }
                        readyButton.setEnabled(true);
                    }
                });
    }

    private void triggerTheEffectOfTheTransactionButton() {
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve information inserted by the user in the Text boxes.
                _retrieveUserData();

                // Start the loading animation when the user tap the button
                readyButton.startAnimation();

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //TODO: User the isSuccessful variable the right way.
                        boolean isSuccessful = true;

                        // Choose a stop animation if your call was successful or not
                        if (isSuccessful){
                            readyButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                    new TransitionButton.OnAnimationStopEndListener() {
                                        @Override
                                        public void onAnimationStopEnd() {
                                            sendToMainActivity();
//                                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            readyButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    }
                }, 2000);
            }
        });
    }

    private void _retrieveUserData() {
        final String userName = username.getText().toString();
        final String userDescription = description.getText().toString();
        final String userPhoneNumber = phoneNumber.getText().toString();
        final String userAddress = address.getText().toString();
        final String userGender = gender;
        final String userAge = age;
        final String[] userInterests = interests;
        // Add user interests in here.
        String randomNameForUserProfileImage = UUID.randomUUID().toString();
        if (!TextUtils.isEmpty(userName)&&userImageUri!=null){
            // Progress bar code goes here
            if (isTheUserChanged){
                userId = String.valueOf(userAuthorization.getCurrentUser());
                // Putting the image to the Firebase Storage. Look at "storage_of_userName"
                final StorageReference pathToTheUserProfileImageInFirebase = cloudStorageInstance.child("storage_of_"+userName)
                        .child("profile_images")
                        .child(randomNameForUserProfileImage+".jpg");
                // Saving the Path where the image profile has
                // been sent so that we can add more info about the user
                // as well as their image profile in.jpg format.
                pathToTheUserProfileImageInFirebase.putFile(userImageUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @SuppressLint({"WrongConstant", "ShowToast"})
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()){
                                    try {
                                        new Compressor(AccountSettingsActivity.this)
                                                .setMaxWidth(100)
                                                .setMaxHeight(100)
                                                .setQuality(2)
                                                .compressToBitmap(new File(pathToTheUserProfileImageInFirebase.getPath()));

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    _storeUserInfoFieldsInFireStore(task,
                                            userName,
                                            userDescription,
                                            userPhoneNumber,
                                            userAddress,
                                            userGender,
                                            userAge,
                                            userInterests);
                                }
                                Toast.makeText(AccountSettingsActivity.this,
                                        "IMAGE ERROR: "+((Exception)Objects.requireNonNull(task.getException()))
                                                .getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
            _storeUserInfoFieldsInFireStore((Task<UploadTask.TaskSnapshot>) null, userName, userDescription, userPhoneNumber, userAddress, userGender, userAge, userInterests);
        }
    }

    private void _triggerTheRadioButtonsForUserGender(View view) {
        // Is the button now clicked?
        boolean checked = ((RadioButton) view).isChecked();
    }

    private void _storeUserInfoFieldsInFireStore(Task<UploadTask.TaskSnapshot> task,
                                                 String userName,
                                                 String userDescription,
                                                 String userPhoneNumber,
                                                 String userAddress,
                                                 String userGender,
                                                 String userAge,
                                                 String[] userInterests)
    {
        String[] downloadURI = {null};
        if(task!=null){
            final String[] downloadURICopy = downloadURI;
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage()
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURICopy[0] = uri.toString();
                                    _makeTheMap(downloadURICopy[0],
                                            userName,
                                            userDescription,
                                            userPhoneNumber,
                                            userAddress,
                                            userGender,
                                            userAge,
                                            userInterests);
                                }
                            });
                }
            });
        } else {
            downloadURI[0] = userImageUri.toString();
        }
        _makeTheMap(downloadURI[0],
                userName,
                userDescription,
                userPhoneNumber,
                userAddress,
                userGender,
                userAge,
                userInterests);
    }

    private void _makeTheMap(String imageUri, String userName, String userDescription, String userPhoneNumber, String userAddress, String userGender, String userAge, String[] userInterests) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("profile_image_of", imageUri);
        userMap.put("username", userName);
        userMap.put("description", userDescription);
        userMap.put("phone_number", userPhoneNumber);
        userMap.put("address", userAddress);
        userMap.put("gender", userGender);
        userMap.put("age", userAge);
        for (String userInterest : userInterests)
            userMap.put("interests", "" + userInterest + ", ");
        cloudBaseDatabaseInstance.collection("Users")
                .document(userId)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            sendToMainActivity();
                            Toast.makeText(AccountSettingsActivity.this,
                                    "The user's settings are updated!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountSettingsActivity.this,
                                    "FIRESTORE ERROR"+((Exception)Objects.requireNonNull(task.getException()))
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        // Progress bar code.
                    }
                });
    }

    private void sendToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}