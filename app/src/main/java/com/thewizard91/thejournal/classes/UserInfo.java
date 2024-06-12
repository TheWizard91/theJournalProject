package com.thewizard91.thejournal.classes;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class UserInfo {
    private final FirebaseFirestore cloudFirebaseDatabaseInstance;
    private final String uId;
    private final String field;
    private String value;
    private String username;
    private String userProfileImageUri;

    public UserInfo(FirebaseFirestore cloudFirebaseDatabaseInstance, String uId, String field) {
        this.cloudFirebaseDatabaseInstance = cloudFirebaseDatabaseInstance;
        this.uId = uId;
        this.field = field;
    }

    public void usernameAndUserProfileImageURIData() {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(uId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d("document", String.valueOf(document));
                        if (document.exists()) {
                            Map<String, Object> documentFieldMap =  documentSnapshot.getData();
                            Log.d("documentFieldMap",String.valueOf(documentFieldMap));
                            if (documentFieldMap != null) {
                                for (Map.Entry<String, Object> entry : documentFieldMap.entrySet()) {
                                    String entryKey = entry.getKey();
                                    String entryKeyValue = entry.getValue().toString();
                                    if (Objects.equals(entryKey, "username")) {
                                        username =entryKeyValue;
                                    }
                                    if (Objects.equals(entryKey, "userProfileImageURI")) {
                                        userProfileImageUri = entryKeyValue;
                                    }
                                }
                            }
                        }
                    }
                });
    }

//    public void useUserInfoCallBack () {
//
//        _userQuery(value, i -> cloudFirebaseDatabaseInstance.collection("Users")
//                .document(uId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if(task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            Map<String, Object> map = documentSnapshot.getData();
//                            if(map != null) {
//                                for(Map.Entry<String, Object> entry: map.entrySet()) {
//                                    String entryKey = entry.getKey();
//                                    String entryKeyValue = entry.getValue().toString();
//                                    if (Objects.equals(entryKey, "username")) {
//                                        username = entryKeyValue;
//                                    }
//                                    if (Objects.equals(entryKey, "userProfileImageURI")) {
//                                        userProfileImageUri = entryKeyValue;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                })
//        );
//    }

    public void useGalleryCallBack () {
        // Updating some of the user's information.

        galleryQuery(value, i -> cloudFirebaseDatabaseInstance.collection("Gallery")
                .document(uId)
                .collection("images")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        int val = Integer.parseInt(String.valueOf(queryDocumentSnapshots.size()));
                        val++;
                        String strVal = String.valueOf(val);
                        _getData(field, strVal);
                    }
                })
        );
    }

//    private void _userQuery (String val, _userCallBack myCallback) {
//        myCallback._onUserCallBack(val);
//    }

//    private interface _userCallBack {
//        void _onUserCallBack(String i);
//    }
    private void galleryQuery (String val, galleryCallBack myCallback) {
        myCallback.onGalleryInfoCallback(val);
    }

    private interface galleryCallBack {
        void onGalleryInfoCallback(String i);
    }

    private void _getData(String entryKey, String entryKeyValue) {
        switch (entryKey) {
            case "numberOfPosts":
            case "numberOfComments":
            case "numberOfLikes":
                _setUpdatedFields(entryKeyValue);
                break;
            case "userProfileImageURI":
                userProfileImageUri = entryKeyValue;
                break;
            case "username":
                username = entryKeyValue;
                break;
            default:
                break;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getUserProfileImageUri() {
        _userProfileImageURI();
        return userProfileImageUri;
    }

    private void _setUpdatedFields(String entryKeyValue) {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(uId)
                .update(field, entryKeyValue);
    }

    private void _userProfileImageURI() {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(uId)
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
                                    _getData(entryKey, entryKeyValue);
                                }
                            }
                        }
                    }
                });
    }
}
