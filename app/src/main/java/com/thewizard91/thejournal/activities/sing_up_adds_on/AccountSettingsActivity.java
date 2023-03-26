package com.thewizard91.thejournal.activities.sing_up_adds_on;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.royrodriguez.transitionbutton.TransitionButton;
//import com.soundcloud.android.crop.Crop;
import com.thewizard91.thejournal.BuildConfig;
import com.thewizard91.thejournal.activities.LogInActivity;
import com.thewizard91.thejournal.activities.MainActivity;
import com.thewizard91.thejournal.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
//import spartons.com.imagecropper.utils.UiHelper;
import org.apache.commons.io.FileUtils;
import id.zelory.compressor.Compressor;
//import com.takusemba.cropme.*;

import static java.io.File.createTempFile;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;
public class AccountSettingsActivity extends AppCompatActivity {

    //
    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
    public static final int ONLY_STORAGE_REQUEST_CODE = 613;
    private static final int CODE_IMAGE_GALLERY = 1;
    private static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    //    private UiHelper uiHelper = new UiHelper();
    private String currentPhotoPath = "";

    // .XML Components
    private ContentLoadingProgressBar progressBar;
    private AppCompatImageView userImage;
//    private CropImageView userImage;
    //    private CropView userImage;
//    private CropLayout userImage;
    private EditText username;
    private EditText description;
    private EditText phoneNumber;
    private EditText address;
    private RadioGroup radioButtons;
    private RadioButton getMale, getFemale, genderRadioButton;
    private TransitionButton readyButton;

    // Firebase instances
    private FirebaseAuth userAuthorization;
    private FirebaseFirestore cloudBaseDatabaseInstance;
    private StorageReference cloudStorageInstance;
    private FirebaseDatabase realtimeDatabase;
    private DatabaseReference realtimeDatabaseReference;

    // Java Objects;
    private boolean isTheUserChanged = false;
    private Uri userImageUri;
    private String userId;
    private String gender = "user's gender currently not available";
    private String age = "user's age currently not available";
    private String[] interests = {"None"};

    private File testFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //
        init();

        // Create the User in Cloud Fire-store
//        createTheUserDatabaseTable();

        // Set up the user image
        setUpTheImageForTheUser();

        // Handle the ready button
        triggerTheReadyTransactionButton();

    }

    public void init(){

        // Instantiating .XML components
        progressBar = findViewById(R.id.account_settings_progress_bar);
        userImage = findViewById(R.id.account_settings_user_account_image_id);
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
        realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();

        // Instantiation of Java Objects
        userImageUri = null;
        userId = String.valueOf(Objects.requireNonNull(userAuthorization.getCurrentUser()).getUid());

        Log.d("In account uid:", userId );// THIS WORKS

    }
    private void _addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase) {
        String notificationUri = UUID.randomUUID().toString();
        realtimeDatabaseReference.child("Notifications")
//                .child(userId)
                .child(notificationUri)
                .setValue(mapOfRealtimeDatabase);
    }
    private void setUpTheImageForTheUser() {
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMAGE_GALLERY);
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                    openImagesDocument();
//                    openCamera();
////                    _selectAndCropImage();
//                    Toast.makeText(AccountSettingsActivity.this, "In if", Toast.LENGTH_SHORT).show();
//                } else if (ContextCompat.checkSelfPermission(AccountSettingsActivity.this,
//                        Manifest.permission.READ_EXTERNAL_STORAGE) != 0) {
//                    ActivityCompat.requestPermissions(AccountSettingsActivity.this,
//                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
//                    Toast.makeText(AccountSettingsActivity.this,
//                            "Permission Denied", Toast.LENGTH_LONG).show();
//                } else {
//                    openImagesDocument();
//                    openCamera();
////                    _selectAndCropImage();
//                }
            }
        });
    }

//    private void _selectAndCropImage() {
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .setCropShape(CropImageView.CropShape.OVAL)
//                .setRequestedSize(100, 100)
//                .setMultiTouchEnabled(true)
//                .setAspectRatio(1, 1)
//                .start(this);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                assert result != null;
//                userImageUri = result.getUri();
//                userImage.setImageURI(userImageUri);
//                isTheUserChanged = true;
//                Toast.makeText(this, "Sending The Profile Image", Toast.LENGTH_SHORT).show();
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                assert result != null;
//                Toast.makeText(this, "" + result.getError(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    private void createTheUserDatabaseTable() {
//
//        // Creating the the User Database Table from collection -> document
//        cloudBaseDatabaseInstance.collection("Users")
//                .document(userId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @SuppressLint({"WrongConstant", "CheckResult", "ShowToast"})
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                        // If User Database Table is not created because of some error, then notify the user.
//                        // Else, create one.
//                        if (!task.isSuccessful()) {
//
//                            Toast.makeText(AccountSettingsActivity.this,
//                                    "FIREBASE RETRIEVE ERROR: " + ((Exception) Objects.requireNonNull(task.getException()))
//                                            .getMessage(), Toast.LENGTH_LONG).show();
//
//                        } else if (task.getResult().exists()) {
//
//                            _helperOfCreateTheUserDatabaseTable(task);
//                            Toast.makeText(AccountSettingsActivity.this, "Updating Account", Toast.LENGTH_LONG).show();
//                        } else {
//
//                            Toast.makeText(AccountSettingsActivity.this, "Account Does Not Exists", Toast.LENGTH_LONG).show();
//                        }
//                        readyButton.setEnabled(true);
//                    }
//                });
//    }

    @SuppressLint("CheckResult")
    private void _helperOfCreateTheUserDatabaseTable(Task<DocumentSnapshot> task) {
        /*Retrieve information on the User database table.*/

        // Get user's profile image and nickname.
        String userProfileImage = task.getResult().getString("user_profile_image");
        String userProfileName = task.getResult().getString("username");
        userImageUri = Uri.parse(userProfileImage);
        username.setText(userProfileName);

        // Replace the image chose by the user in the right place.
        RequestOptions placeHolderRequest = new RequestOptions();
        placeHolderRequest.placeholder(R.mipmap.baseline_account_circle_black_24dp);
        Glide.with((FragmentActivity) AccountSettingsActivity.this)
                .setDefaultRequestOptions(placeHolderRequest)
                .load(userProfileImage)
                .into(userImage);
    }

    private void triggerTheReadyTransactionButton() {
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get gender
                addListenerRadioButton();

                // Retrieve information inserted by the user in the Text boxes.
                _retrieveUserData();

                // Start the loading animation when the user tap the button
                readyButton.startAnimation();

                // Do your networking task or background work here.
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        /*
                        TODO: User the isSuccessful variable the right way.
                        Meaning when the user is created already.
                         */
                        boolean isSuccessful = true;

                        // Choose a stop animation if your call was successful or not
                        readyButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {

                                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);

                                    }
                                });
                    }
                }, 2000);
            }
        });
    }

    private void _retrieveUserData() {
        /*Helper method of retrieve information entered by the user so that we can populate the database
        that we are about to create and put them in the Storage.
         */

        // Retrieve info inserted by the user.
        final String userName = username.getText().toString();
        final String userDescription = description.getText().toString();
        final String userPhoneNumber = phoneNumber.getText().toString();
        final String userAddress = address.getText().toString();
        final String userGender = gender;
        final String userAge = age;

        // Add user interests in here.
        String randomNameForUserProfileImage = UUID.randomUUID().toString();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userDescription) && !TextUtils.isEmpty(userPhoneNumber)) {

            Log.d("InIf0:", "yes");
            // Progress bar code goes here
            if (isTheUserChanged) {

                Log.d("InIf1:", "yes");
//                userId = String.valueOf(userAuthorization.getCurrentUser());

                // Putting the image to the Firebase Storage. Look at "storage_of_userName"
                final StorageReference pathToTheUserProfileImageInFirebase = cloudStorageInstance.child("storage_of_" + userName)
                        .child("profile_images")
                        .child(randomNameForUserProfileImage + ".jpg");

                Log.d("afterStorage", "yes");
                // Saving the Path where the image profile has
                // been sent so that we can add more info about the user
                // as well as their image profile in.jpg format.
                pathToTheUserProfileImageInFirebase.putFile(userImageUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @SuppressLint({"WrongConstant", "ShowToast"})
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    // Wrapping the image compressor object in
                                    // an exception because it fixed the bug.
                                    try {

                                        // Compress the quality of the image
                                        new Compressor(AccountSettingsActivity.this)
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
                                            userGender,
                                            userAge);

                                } else {

                                    Toast.makeText(AccountSettingsActivity.this,
                                            "IMAGE ERROR: " + ((Exception) Objects.requireNonNull(task.getException()))
                                                    .getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }

//            Log.d("outOfIf1:", "yes");
            _storeUserInfoFieldsInFireStore((Task<UploadTask.TaskSnapshot>) null,
                    userName, userDescription, userPhoneNumber, userAddress, userGender, userAge);

        }

    }

    private void addListenerRadioButton() {
        int selectId = radioButtons.getCheckedRadioButtonId();
//        radioButtons = findViewById(selectId);
        genderRadioButton = findViewById(selectId);
        gender = genderRadioButton.getText().toString();
    }
    @SuppressLint("NonConstantResourceId")
    private void _triggerTheRadioButtonsForUserGender(View view) {
        // Is the button now clicked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked.
        switch (view.getId()){
            case R.id.account_settings_radio_button_one_id:
                if(checked)
                    gender = "Male";
                break;
            case R.id.account_settings_radio_button_two_id:
                if (checked)
                    gender = "Female";
                break;
        }
    }

    private void _storeUserInfoFieldsInFireStore(Task<UploadTask.TaskSnapshot> task,
                                                 String userName,
                                                 String userDescription,
                                                 String userPhoneNumber,
                                                 String userAddress,
                                                 String userGender,
                                                 String userAge) {
        /*
        Method helper of _retrieveUserData so that filled can be filled.
         */

        String[] downloadURI = {null};
        if (task != null) {

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
//                                    userImageUri= Uri.parse(downloadURICopy[0]);

                                    // Mapping each single field with their respective values.
                                    _makeTheMap(downloadURICopy[0],
                                            userName,
                                            userDescription,
                                            userPhoneNumber,
                                            userAddress,
                                            userGender,
                                            userAge);

                                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                    LocalDateTime now =LocalDateTime.now();
                                    String date = dateTimeFormatter.format(now).toString();
                                    NotificationsModel notificationsModel=new NotificationsModel();
                                    notificationsModel.setUsername(String.valueOf(username));
                                    notificationsModel.setUserId(userId);
                                    notificationsModel.setDate(date);
                                    notificationsModel.setUserProfileImageURI(downloadURICopy[0]);
                                    notificationsModel.setNotificationText("Welcome "+username);
                                    Map<String,Object> mapOfRealtimeDatabase=notificationsModel.realTimeDatabaseMap();
                                    _addToRealtimeDatabase(mapOfRealtimeDatabase);
                                }
                            });
                }
            });
        }
//        else {

            downloadURI[0] = userImageUri.toString();

//        }

        _makeTheMap(downloadURI[0],
                userName,
                userDescription,
                userPhoneNumber,
                userAddress,
                userGender,
                userAge);
    }

    private void _makeTheMap(String imageUri,
                             String userName,
                             String userDescription,
                             String userPhoneNumber,
                             String userAddress,
                             String userGender,
                             String userAge) {
        /*
            Mapping each single field with their respective values.
         */

        Map<String, String> userMap = new HashMap<>();
        userMap.put("userProfileImageUri", imageUri);
        userMap.put("username", userName);
        userMap.put("description", userDescription);
        userMap.put("phoneNumber", userPhoneNumber);
        userMap.put("address", userAddress);
        userMap.put("gender", userGender);
        userMap.put("age", userAge);

        // Now populating the fields.
        cloudBaseDatabaseInstance.collection("Users")
                .document(userId)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            sendToLoginActivity();
//                            sendToMainActivity();
                            Toast.makeText(AccountSettingsActivity.this,
                                    "The user's settings are updated!",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(AccountSettingsActivity.this,
                                    "FIRE-STORE ERROR" + ((Exception) Objects.requireNonNull(task.getException()))
                                            .getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendToLoginActivity() {
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    private void sendToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void openCamera() {
        /*
         Opening the camera when the user clicks on the openCamera dialog action
         */
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile(); // 1 imageFile is created
        Uri uri;
        // If the SDK >= 23, create the Uri with FileProvider to prevent FileUriExposedException.
        // The BuildConfig gives name to the package of our application and concat the.provider in manifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            //  Creates the Uri fromFile utility method if SDK < 24.
            uri = Uri.fromFile(file);
        // Set the path to where we want to store the selected image, to then read the image
        // in the onActivityResult method from the uri path
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    private File getImageFile() {
        /*
          Returns a new File in the external storage directory with .jpg extension
         */
//        Context c=getBaseContext();
//        File f = c.getCacheDir();
//        Unable to create temporary file, C:/data/user/0/com.thewizard91.thejournal/cache/JPEG_1610117421924_4991651777243470602.jpg
        String imageFileName = "/JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
//        StringBuilder storageDirString = new StringBuilder(String.valueOf(storageDir));
//        File correctedStorageDir = new File(String.valueOf(storageDir).substring(1));//storage/emulated/0/DCIM/Camera
        Log.d("storageDirIs: ", String.valueOf(storageDir));///storage/emulated/0/DCIM/Camera
        Log.d("imageNameIs:", imageFileName);//JPEG_1609904306162_
//        Log.d("correctedStorageDirIs:", correctedStorageDir.toString());

        File file = null;
        try {
            file = createTempFile(imageFileName, ".jpg", new File("C:" + storageDir.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ErrorDetected:", e.toString());
        }
//        file = new File(storageDir, imageFileName+".jpg");
//        Log.d("fileIs", file.toString());

        // currentPhotoPath is where the image is stored ones taken by the user.
        currentPhotoPath = "file:" + Objects.requireNonNull(file).getAbsolutePath();
        return file;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        /*
        Replacing the image with the newly cropped one.
        That is because the two parameters are going to have the same value passed
        resulting to the same destination.
        If a different logic is needed, then change their values.
         */
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(300, 300)
                .withAspectRatio(5f, 5f)
                .start(this);
    }
    private void showImage(Uri imageUri) {
        /*
        Show cropped image inside the imageView by getting the imageUri.
         */
        File file = FileUtils.getFile(String.valueOf(imageUri));
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        // inserting the image cropped in the android component where the user  profile image is
        userImage.setImageBitmap(bitmap);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void openImagesDocument() {
        /*
        Allows user to choose image from the gallery by the selectImage dialog action.
         */
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);

        // Specifying that we need images only and nothing else.
        pictureIntent.setType("image/*");

        // The CATEGORY_OPENABLE tells which Uri can be opened.
        // However, more category can be opened at: https://developer.android.com/reference/android/content/Intent.html#CATEGORY_OPENABLE
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Specifying that we need only the two types below.
            String[] mimeTypes = new String[] {"image/jpeg", "image/png", "image/jpg"};
            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        }

        // The option to choose the type of image only if there are multiple types.
        startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);  // 4
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//CAMERA_ACTION_PICK_REQUEST_CODE

        if (requestCode == CODE_IMAGE_GALLERY && resultCode == RESULT_OK) {

//            Uri uri = Uri.parse(currentPhotoPath);
//            openCropActivity(uri, uri);
//            showImage(uri);
            assert data != null;
            userImageUri = data.getData();
            isTheUserChanged = true;
            if(userImageUri!=null){
                startCrop(userImageUri);
            }

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {

//            assert data != null;
//            Uri uri = UCrop.getOutput(data);
//            showImage(uri);
            assert data != null;
            Uri imageUriResultCrop = UCrop.getOutput(data);
            if (imageUriResultCrop != null) {
                userImage.setImageURI(imageUriResultCrop);
            }
        }
//        } else (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//
//            // Get the selected image Uri from data
//            Uri sourceUri = data.getData();
//
//            // Create the image File where you want to store the cropped image result.
//            File file = getImageFile();
//            /*File is none, this is what gives me the error.*/
//
//            // Simply creates the destinationUri fromFile utility method.
//            Uri destinationUri = Uri.fromFile(file);
//            openCropActivity(sourceUri, destinationUri);
//        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileImage = SAMPLE_CROPPED_IMG_NAME;
        destinationFileImage +=".jpg";

        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileImage)));

        ucrop.withAspectRatio(1f,1f);
//        ucrop.withAspectRatio(3,4);
//        ucrop.useSourceImageAspectRatio();
//        ucrop.withAspectRatio(2,3);
//        ucrop.withAspectRatio(16,9);

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
}

    // FROM HERE IS UCROP CODE
//
//    private void openCamera() {
//        /*
//         Opening the camera when the user clicks on the openCamera dialog action
//         */
//        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File file = getImageFile(); // 1 imageFile is created
//        Uri uri;
//        // If the SDK >= 23, create the Uri with FileProvider to prevent FileUriExposedException.
//        // The BuildConfig gives name to the package of our application and concat the.provider in manifest
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
//        else
//            //  Creates the Uri fromFile utility method if SDK < 24.
//            uri = Uri.fromFile(file);
//        // Set the path to where we want to store the selected image, to then read the image
//        // in the onActivityResult method from the uri path
//        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
//    }
//
//    private File getImageFile() {
//        /*
//          Returns a new File in the external storage directory with .jpg extension
//         */
////        Context c=getBaseContext();
////        File f = c.getCacheDir();
////        Unable to create temporary file, C:/data/user/0/com.thewizard91.thejournal/cache/JPEG_1610117421924_4991651777243470602.jpg
//        String imageFileName = "/JPEG_" + System.currentTimeMillis() + "_";
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
////        StringBuilder storageDirString = new StringBuilder(String.valueOf(storageDir));
////        File correctedStorageDir = new File(String.valueOf(storageDir).substring(1));//storage/emulated/0/DCIM/Camera
//        Log.d("storageDirIs: ", String.valueOf(storageDir));///storage/emulated/0/DCIM/Camera
//        Log.d("imageNameIs:", imageFileName);//JPEG_1609904306162_
////        Log.d("correctedStorageDirIs:", correctedStorageDir.toString());
//
//        File file = null;
//        try {
//            file = File.createTempFile(imageFileName, ".jpg", new File("C:" + storageDir.getAbsolutePath()));
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("ErrorDetected:", e.toString());
//        }
////        file = new File(storageDir, imageFileName+".jpg");
////        Log.d("fileIs", file.toString());
//
//        // currentPhotoPath is where the image is stored ones taken by the user.
//        currentPhotoPath = "file:" + Objects.requireNonNull(file).getAbsolutePath();
//        return file;
//    }
//
//    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
//        /*
//        Replacing the image with the newly cropped one.
//        That is because the two parameters are going to have the same value passed
//        resulting to the same destination.
//        If a different logic is needed, then change their values.
//         */
//        UCrop.of(sourceUri, destinationUri)
//                .withMaxResultSize(300, 300)
//                .withAspectRatio(5f, 5f)
//                .start(this);
//    }
//    private void showImage(Uri imageUri) {
//        /*
//        Show cropped image inside the imageView by getting the imageUri.
//         */
//        File file = FileUtils.getFile(String.valueOf(imageUri));
//        InputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//        // inserting the image cropped in the android component where the user  profile image is
//        userImage.setImageBitmap(bitmap);
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    private void openImagesDocument() {
//        /*
//        Allows user to choose image from the gallery by the selectImage dialog action.
//         */
//        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
//
//        // Specifying that we need images only and nothing else.
//        pictureIntent.setType("image/*");
//
//        // The CATEGORY_OPENABLE tells which Uri can be opened.
//        // However, more category can be opened at: https://developer.android.com/reference/android/content/Intent.html#CATEGORY_OPENABLE
//        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//
//            // Specifying that we need only the two types below.
//            String[] mimeTypes = new String[] {"image/jpeg", "image/png", "image/jpg"};
//            pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//
//        }
//
//        // The option to choose the type of image only if there are multiple types.
//        startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);  // 4
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
//
//            Uri uri = Uri.parse(currentPhotoPath);
//            openCropActivity(uri, uri);
//            showImage(uri);
//
//        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
//
//            assert data != null;
//            Uri uri = UCrop.getOutput(data);
//            showImage(uri);
//
//        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//
//            // Get the selected image Uri from data
//            Uri sourceUri = data.getData();
//
//            // Create the image File where you want to store the cropped image result.
//            File file = getImageFile();
//            /*File is none, this is what gives me the error.*/
//
//            // Simply creates the destinationUri fromFile utility method.
//            Uri destinationUri = Uri.fromFile(file);
//            openCropActivity(sourceUri, destinationUri);
//        }
//    }