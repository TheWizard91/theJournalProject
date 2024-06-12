package com.thewizard91.thejournal.classes;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostInfo {
    private final FirebaseFirestore cloudFirebaseDatabaseInstance;
    private final String pId;
    private final String uId;
    private final String field;
    private final String collection;
    private String value;
    private String username;

    public PostInfo (FirebaseFirestore cloudFirebaseDatabaseInstance, String uId, String pId, String field, String collection) {
        this.cloudFirebaseDatabaseInstance = cloudFirebaseDatabaseInstance;
        this.pId = pId;
        this.uId = uId;
        this.field = field;
        this.collection = collection;
    }

    public void usePostCallBack () {
        //
        postQuery(value, e -> cloudFirebaseDatabaseInstance.collection("Posts")
                .document(pId)
                .collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int numberOfCommentsOrLikes = 0;
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        DocumentSnapshot documentSnapshot = documents.get(i);
                        String commentOfCurrentUser = String.valueOf(documentSnapshot.get("userId"));
                        if (commentOfCurrentUser.equals(uId)) {
                            numberOfCommentsOrLikes++;
                        }
                    }

                    String strNumberOfCommentsOrLikes = String.valueOf(numberOfCommentsOrLikes);
                    if (collection.equals("Comments") || collection.equals("Likes")) {
                        updateField(field, strNumberOfCommentsOrLikes);
                    }
                })
        );
    }

    private void updateField(String field, String strNumberOfCommentsOrLikes) {
        cloudFirebaseDatabaseInstance.collection("Users")
                .document(uId)
                .update(field, strNumberOfCommentsOrLikes);
    }

    private void postQuery (String val, postCallBack myCallback) {
        myCallback.onPostInfoCallBack(val);
    }

    private interface postCallBack {
        void onPostInfoCallBack(String i);
    }

}
