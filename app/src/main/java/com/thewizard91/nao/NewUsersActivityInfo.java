package com.thewizard91.nao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static java.io.File.createTempFile;

//import io.grpc.Compressor;

public class NewUsersActivityInfo extends AppCompatActivity {

    // XML file elements instances.
    private ImageView userImage;
    private EditText username;
    private Button button;

    // Firebase database instances.
    private FirebaseAuth userAuthorized;// For user verification.
    private FirebaseFirestore cloudBaseDatabaseInstance;// For user database(all info in there).
    private StorageReference cloudStorageInstance;// For digital storage.

    // Primitive Types.
    private Uri userImageUri;// For userImage filepath.
    private String userId;// For useId.
    private String currentPhotoPath = "";
    private boolean isTheUserChanged = false;

    //
    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
    public static final int ONLY_STORAGE_REQUEST_CODE = 613;
    private static final int CODE_IMAGE_GALLERY = 1;
    private static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_users_info);

        inti();
        createTheUserDatabase();
        setUpTheImageForTheUser();
        addButtonHandler();
    }

    private void inti() {
        //
        userImage = findViewById(R.id.user_image);
        username = findViewById(R.id.user_name);
        button = findViewById(R.id.update_button);

        //
        userAuthorized = FirebaseAuth.getInstance();
        cloudBaseDatabaseInstance = FirebaseFirestore.getInstance();
        cloudStorageInstance = FirebaseStorage.getInstance().getReference();

        //
//        userImage=null;
        if (userAuthorized.getCurrentUser() != null) {
            userId = userAuthorized.getCurrentUser().getUid();
        } else {
            userId = UUID.randomUUID().toString();
        }
        Log.d("userIdIs:", userId);
    }

    private void setUpTheImageForTheUser() {
        // Will let user choose an image from the say gallery and upload it.
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMAGE_GALLERY);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//CAMERA_ACTION_PICK_REQUEST_CODE

        if (requestCode == CODE_IMAGE_GALLERY && resultCode == RESULT_OK) {
            assert data != null;
            userImageUri = data.getData();
            isTheUserChanged = true;
            if(userImageUri!=null){
                startCrop(userImageUri);
            }

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            assert data != null;
            Uri imageUriResultCrop = UCrop.getOutput(data);
            if (imageUriResultCrop != null) {
                userImage.setImageURI(imageUriResultCrop);
            }
        }
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

        // Ui
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.white));
        options.setToolbarTitle("Choose The Image For Your Profile!");

        return options;
    }
    private void createTheUserDatabase() {
        // In the creation of the database we have no userId.
        // Therefore, we let firebase automate for one, hence, the empty parameter in .document.
        cloudBaseDatabaseInstance.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // If User Database Table is not created because of some error, then notify the user.
                        // Else, create one.
                        if (!task.isSuccessful()) {

                            Toast.makeText(NewUsersActivityInfo.this,
                                    "FIREBASE RETRIEVE ERROR: " + ((Exception) Objects.requireNonNull(task.getException()))
                                            .getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("getError:", task.getException().getMessage());

                        } else if (task.getResult().exists()) {

                            _helperOfCreateTheUserDatabaseTable(task);
                            Toast.makeText(NewUsersActivityInfo.this, "Updating Account", Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(NewUsersActivityInfo.this, "Account Does Not Exists", Toast.LENGTH_LONG).show();
                        }
                        button.setEnabled(true);
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void _helperOfCreateTheUserDatabaseTable(Task<DocumentSnapshot> task) {
        /*
        Retrieve information from the User to update the database table.
         */

        // Get user's profile image and nickname.
        String userProfileImage = task.getResult().getString("user_profile_image");
        String userProfileName = task.getResult().getString("username");
        userImageUri = Uri.parse(userProfileImage);
        username.setText(userProfileName);

        // Replace the image chose by the user in the right place.
        RequestOptions placeHolderRequest = new RequestOptions();
        placeHolderRequest.placeholder(R.drawable.ic_person);// Need to check result in here.
        Glide.with((FragmentActivity) NewUsersActivityInfo.this)
                .setDefaultRequestOptions(placeHolderRequest)
                .load(userProfileImage)
                .into(userImage);
    }

    private void retrieveUserInfo(){
        /*
        Helper method of retrieve information entered by the user so that we can populate the database
        that we are about to create and put them in the Storage.
         */

        // Retrieve info inserted by the user.
        final String user_name = username.getText().toString();
        Log.d("MadeItHere","Yes"); // I DO GET HERE.

        // Add user interests in here.
        String randomNameForUserProfileImage = UUID.randomUUID().toString();

        if (!TextUtils.isEmpty(user_name)) {

            Log.d("InIf0:", "yes");
            // Progress bar code goes here
            if (isTheUserChanged) {

                Log.d("InIf1:", "yes");
//                userId = String.valueOf(userAuthorized.getCurrentUser());

                // Putting the image to the Firebase Storage. Look at "storage_of_userName"
                final StorageReference pathToTheUserProfileImageInFirebase = cloudStorageInstance.child("storage_of_" + user_name)
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

//                                    Log.d("InIf2:", "yes");

                                    // Wrapping the image compressor object in
                                    // an exception because it fixed the bug.
                                    try {

                                        // Compress the quality of the image
                                        new Compressor(NewUsersActivityInfo.this)
                                                .setMaxWidth(100)
                                                .setMaxHeight(100)
                                                .setQuality(2)
                                                .compressToBitmap(new File(pathToTheUserProfileImageInFirebase.getPath()));

                                    } catch (IOException e) {

                                        e.printStackTrace();
                                        Log.d("ErrorDetected:", e.toString());

                                    }

                                    // Make the map - make and populate the table.
                                    _storeUserInfoFieldsInFireStore(task, user_name);

                                } else {

//                                    Log.d("inElse:", "yes");
                                    Toast.makeText(NewUsersActivityInfo.this,
                                            "IMAGE ERROR: " + ((Exception) Objects.requireNonNull(task.getException()))
                                                    .getMessage(), Toast.LENGTH_LONG).show();

                                }
//
//                                Toast.makeText(AccountSettingsActivity.this,
//                                        "IMAGE ERROR: " + ((Exception) Objects.requireNonNull(task.getException()))
//                                                .getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });
            }

//            Log.d("outOfIf1:", "yes");
            _storeUserInfoFieldsInFireStore((Task<UploadTask.TaskSnapshot>) null, user_name);

        }

//        Log.d("outOfIf0:", "yes");
    }

    private void _storeUserInfoFieldsInFireStore(Task<UploadTask.TaskSnapshot> task, String userName) {
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
                                    userImageUri= Uri.parse(downloadURICopy[0]);

                                    // Mapping each single field with their respective values.
                                    _makeTheMap(downloadURICopy[0], userName);
                                }
                            });
                }
            });
        }
//        else {

        downloadURI[0] = userImageUri.toString();

//        }

        _makeTheMap(downloadURI[0],userName);
    }

    private void _makeTheMap(String imageUri, String userName) {
        /*
        Mapping each single field with their respective values.
         */

        Map<String, String> userMap = new HashMap<>();
        userMap.put("profile_image_of", imageUri);
        userMap.put("username", userName);

        // Now populating the fields.
        cloudBaseDatabaseInstance.collection("Users")
                .document(userId)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

//                            sendToMainActivity();
                            Toast.makeText(NewUsersActivityInfo.this,
                                    "The user's settings are updated!",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(NewUsersActivityInfo.this,
                                    "FIRE-STORE ERROR" + ((Exception) Objects.requireNonNull(task.getException()))
                                            .getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        // Progress bar code.
                    }
                });
    }

    private void sendToMainActivity() {
        Intent mainActivityIntent= new Intent(NewUsersActivityInfo.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    private void addButtonHandler() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveUserInfo();
//                userAuthorized.signOut();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}