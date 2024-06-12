package com.thewizard91.thejournal.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.thewizard91.thealbumproject.C2521R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.MainActivity;
import com.thewizard91.thejournal.adapters.CommentsAdapter;
import com.thewizard91.thejournal.classes.PostInfo;
import com.thewizard91.thejournal.models.comments.CommentsModel;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class CommentsFragment extends Fragment {
    private String postId;
    public String commentsId;
    public TextView commentTextView;
    public CommentsAdapter commentAdapter;
    private RecyclerView commentListModelView;
    public CommentsModel commentListModel;
    public List<CommentsModel> commentList;
    public Context context;
    public String currentUserId;
    private FirebaseFirestore firebasefirestore;
    private DatabaseReference realtimeDatabaseReference;
    public Boolean isTheFirstLoadOfCommentsLoaded = true;
    public DocumentSnapshot lastVisibleComment;
    public  String username;
    public String userProfileImageURI;
    private final int size = 0;
    private int currentSize;
    private View view;
    private MainActivity activity;
    private Button sendCommentButton;
    private PostInfo postInfo;

    public CommentsFragment() {}
    public CommentsFragment(String postId, String currentUserId) {
        this.postId = postId;
        this.currentUserId = currentUserId;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        commentListModelView = view.findViewById(R.id.comments_recyclerview);
        commentTextView = view.findViewById(R.id.comment_edittext);
        commentListModelView = view.findViewById(R.id.comments_recyclerview);
        sendCommentButton = view.findViewById(R.id.comment_button);
        context = container.getContext();
        commentList = new ArrayList<>();
        commentAdapter = new CommentsAdapter(commentList, postId);
        commentListModelView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentListModelView.setAdapter(commentAdapter);
        firebasefirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebasefirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        assert currentUser != null;
        username = currentUser.getDisplayName();
        getUserImageProfileUri();

        // Getting rid of navigation button at the bottom.
        activity = (MainActivity) getActivity();
        assert activity != null;
        activity.addFloatingActionButton.setVisibility(View.INVISIBLE);
        activity.bottomAppBar.setVisibility(View.INVISIBLE);
        activity.backFloatingActionButton.setVisibility(View.VISIBLE);
        postInfo = new PostInfo(firebasefirestore,currentUserId,postId,"numberOfComments","Comments");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Send the info to firebase by retrieving the data from user and making the map
            sendNewCommentToDatabase();

            // Create the first few comments and display them.
            createTheFirstQuery();

            // DO NOT TOUCH THIS.
//            loadMoreCommentsAsTheUserScrollsDown();

            // For the back button that will send you to the home fragment (main activity.)
            sendToHomeFragment();
//            if (size > 1) Log.d("now load","yes");
            return view;
        }
        throw new AssertionError();
    }

    private void getUserImageProfileUri() {
        firebasefirestore.collection("Users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    userProfileImageURI = (String) documentSnapshot.get("userProfileImageURI");
                    username = (String) documentSnapshot.get("username");
                });
    }

    private void loadMoreCommentsAsTheUserScrollsDown() {
        /*It uses the help of loadMoreComments to load more content on the adapter, as the user scroll down.*/

        commentListModelView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(context, "Reached bottom " + lastVisibleComment.getString("commentText"), Toast.LENGTH_SHORT).show();
                    isTheFirstLoadOfCommentsLoaded = true;
                    loadMoreComments();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadMoreComments() {
        /*Helper of loadMoreCommentsAsTheUserScrollsDown, it load more content on the adapter.**/

        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .startAfter(lastVisibleComment)
                .limit(10) // loading up to five comments at time.
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) {
                        if (isTheFirstLoadOfCommentsLoaded) {
                            lastVisibleComment = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                        for (DocumentChange documents : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documents.getType() == DocumentChange.Type.ADDED) {
                                commentsId = documents.getDocument().getId();
                                commentListModel = documents.getDocument().toObject(CommentsModel.class).withId(commentsId);
                                commentList.add(commentList.size(), commentListModel);
                                commentAdapter.notifyDataSetChanged();
                                lastVisibleComment = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }
                        }
                        isTheFirstLoadOfCommentsLoaded = false; // now last visible comment is already been uploaded.
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void createTheFirstQuery() {
        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(100)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(context, "No comments on this post yet so be the first to comment!", Toast.LENGTH_SHORT).show();
                    } else {
                        lastVisibleComment = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                            if (document.getType() == DocumentChange.Type.ADDED) {
                                commentsId = document.getDocument().getId();
                                commentListModel = document.getDocument().toObject(CommentsModel.class).withId(commentsId);
                                commentList.add(commentListModel);
                                commentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void sizeOfQuery (int size, callback myCallback) { myCallback.onCallback(size); }

    private interface callback {
        void onCallback(int i);
    }

    private void sendToHomeFragment () {
        /*Send user back to home fragment (main activity).
        * post: user in comment fragment.
        * Then, they are in the home fragment upon the click of the back button.*/

        activity.backFloatingActionButton.setOnClickListener(v -> ((FragmentActivity) view.getContext())
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new HomeFragment())
                .commit());
    }

    private void sendNewCommentToDatabase() {
        /*Send new comment in db.
        * post: comment was not in firebase. Now it is.
        *       comment was not in realtime database. Now it is. */

        sendCommentButton.setOnClickListener(view -> {
            String comment = commentTextView.getText().toString();
            if (!TextUtils.isEmpty(comment)) {
                FieldValue time = FieldValue.serverTimestamp();
                CommentsModel commentListModel = new CommentsModel(postId,userProfileImageURI,comment,
                                            "","","","",time,
                                            currentUserId,username,userProfileImageURI);
                // Firestore database.
                String commentsId = UUID.randomUUID().toString();
                Map<String,Object> newCommentMap = commentListModel.firebaseDatabaseMap();
                makeTheDatabase(commentsId,newCommentMap);

                // Create post map and create the database in realtime database.
                DateTimeFormatter dateTimeFormatter = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                }

                LocalDateTime timeRightNow = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    timeRightNow = LocalDateTime.now();
                }

                String date = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    date = dateTimeFormatter.format(timeRightNow);
                }

                // Make a notification about it.
                NotificationsModel notificationsModel = new NotificationsModel(username, currentUserId, date, userProfileImageURI,username+" has just posted a comment.");
                Map<String,Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                addToRealtimeDatabase(mapOfRealtimeDatabase,commentsId);

                postInfo.usePostCallBack();

                new CommentsAdapter(commentList);
            }
        });
    }
    private void addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase, String commentsId) {
        realtimeDatabaseReference.child("Notifications")
                .child(commentsId)
                .setValue(mapOfRealtimeDatabase);
    }
    public void makeTheDatabase(String commentUID,Map<String,Object> map) {
        firebasefirestore.collection("Posts/" + postId + "/Comments")
                .document(currentUserId + ":" + commentUID)
                .set(map)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "There Was An Error Posting The Comment: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    commentTextView.setText("");
                    Toast.makeText(context, "Comment sent", Toast.LENGTH_SHORT).show();
                });
    }
}
