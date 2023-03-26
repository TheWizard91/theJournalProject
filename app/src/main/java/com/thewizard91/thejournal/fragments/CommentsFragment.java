package com.thewizard91.thejournal.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.thewizard91.thealbumproject.C2521R;
import com.google.android.exoplayer2.util.Log;
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
import com.thewizard91.thejournal.models.comments.CommentsModel;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class CommentsFragment extends Fragment {
    private String blogPostId;
    public String commentId;
    public TextView commentTextView;
    public CommentsAdapter commentsAdapter;
    private RecyclerView commentsListView;
    public CommentsModel commentsModel;
    public List<CommentsModel> commentsModelList;
    public Context context;
    public String currentUserId;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase realtimeDatabase;
    private DatabaseReference realtimeDatabaseReference;
    private StorageReference dataServerStorage;
    public Boolean isTheFirstPageOfCommentsLoaded = true;
    public DocumentSnapshot lastVisibleComment;
    public  String username;
    public String userProfileImageUri;
    private int size=0;
    private int currentSize;
    private View view;
    private MainActivity activity;
    private Button sendCommentButtonView;

    public CommentsFragment() {}
    public CommentsFragment(String blogPostId, String currentUserId) {
        this.blogPostId = blogPostId;
        this.currentUserId = currentUserId;
        Log.d("can I refresh?","yes");
    }
/*TODO: When the query is empty and I add a comment by pressing the send button, the comment info is stored on th bd.
*  However, as soon as I scroll down, the app crashes. One way to fix this is by refreshing the fragment.*/
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        commentsListView = view.findViewById(R.id.comments_recyclerview);
        commentTextView = view.findViewById(R.id.comment_edittext);
        commentsListView = view.findViewById(R.id.comments_recyclerview);
        sendCommentButtonView = view.findViewById(R.id.comment_button);
        context = container.getContext();
        commentsModelList = new ArrayList();
        commentsAdapter = new CommentsAdapter(commentsModelList, blogPostId);
        commentsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentsListView.setAdapter(commentsAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
//        Log.d("idOfUser",currentUserId);
        username = currentUser.getDisplayName();
        getUserImageProfileUri();

        // Getting rid of navigation button at the bottom.
        activity = (MainActivity) getActivity();
        activity.addFloatingButton.setVisibility(View.INVISIBLE);
        activity.bottomAppBar.setVisibility(View.INVISIBLE);
        activity.sendBackFloatingActionButton.setVisibility(View.VISIBLE);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Send the info to firebase by retrieving the data from user and making the map
            sendCommentToDatabase();
            // Create the first few comments and display them.
            createTheFirstQuery();

            sendToHomeFragment ();
            //TODO: DO NOT TOUCH THIS.
//            loadMoreCommentsAsTheUserScrollsDown();
//            if (size > 1) Log.d("now load","yes");
            return view;
        }
        throw new AssertionError();
    }

//    private void refresh () {
//        FragmentTransaction transaction = getParentFragment().getChildFragmentManager().beginTransaction();
//        transaction.detach(this).attach(this).commit();
//        Fragment currentFragment = getActivity()
//                .getSupportFragmentManager()
//                .findFragmentById(R.id.comments_fragment);
//        Log.d("currentFragmentIs", String.valueOf(currentFragment));
//        FragmentTransaction fragmentTransaction = getActivity().
//                getSupportFragmentManager()
//                .beginTransaction();
//        fragmentTransaction.detach(currentFragment);
//        fragmentTransaction.attach(currentFragment);
//        fragmentTransaction.commit();
//        Log.d("Am I refreshing?","yes");
//    }

    private void getUserImageProfileUri() {
        firebaseFirestore.collection("Users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userProfileImageUri = (String) documentSnapshot.get("userProfileImageUri");
                        username = (String) documentSnapshot.get("username");
                    }
                });
    }

    private void loadMoreCommentsAsTheUserScrollsDown() {
        /**It uses the help of loadMoreComments to load more content on the adapter, as the user scroll down.**/
        commentsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(context, "Reached bottom " + lastVisibleComment.getString("commentText"), Toast.LENGTH_SHORT).show();
                    isTheFirstPageOfCommentsLoaded = true;
//                    loadMoreComments();
                }
            }
        });
    }

    public void loadMoreComments() {
        /**Helper of loadMoreCommentsAsTheUserScrollsDown, it load more content on the adapter.**/
        firebaseFirestore.collection("Posts")
                .document(blogPostId)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .startAfter(lastVisibleComment)
                .limit(5)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots == null) {
                            throw new AssertionError();
                        } else if (!queryDocumentSnapshots.isEmpty()) {
                            if (isTheFirstPageOfCommentsLoaded.booleanValue()) {
                                lastVisibleComment = queryDocumentSnapshots.getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);
                                Log.d("lastVisibleComment",lastVisibleComment.getTimestamp("timestamp").toString());
                            }
                            for (DocumentChange documents : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documents.getType() == DocumentChange.Type.ADDED) {
                                    commentId = documents.getDocument().getId();
                                    commentsModel = (CommentsModel) ((CommentsModel) documents.getDocument().toObject(CommentsModel.class)).withId(commentId);
                                    commentsModelList.add(commentsModelList.size(), commentsModel);
                                    commentsAdapter.notifyDataSetChanged();
//                                    lastVisibleComment = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                }
                            }
                            isTheFirstPageOfCommentsLoaded = false;
                        }
                    }
                });
    }

    private void createTheFirstQuery() {
        /**Create the first query, plus, display it.**/
        firebaseFirestore.collection("Posts")
                .document(blogPostId)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(100)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots == null) {
                            throw new AssertionError();
                        } else if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(context, "No comments on this post yet so be the first to comment!", Toast.LENGTH_SHORT).show();
                        } else {
                            lastVisibleComment = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            Log.d("There is a comment","yes");
                            for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                                if (document.getType() == DocumentChange.Type.ADDED) {
                                    commentId = document.getDocument().getId();
                                    Log.d("commentId",commentId);
                                    commentsModel = document.getDocument().toObject(CommentsModel.class).withId(commentId);
                                    Log.d("commentsModel",commentsModel.toString());
                                    commentsModelList.add(commentsModel);
                                    Log.d("commentsModelList",commentsModelList.toString());
                                    commentsAdapter.notifyDataSetChanged();
                                }
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
        activity.sendBackFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) view.getContext())
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new HomeFragment())
                        .commit();
            }
        });
    }

    private void sendCommentToDatabase() {
        /**Extracting the information from(by element fields) the user so the map can be made**/
        sendCommentButtonView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                String comment = commentTextView.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    FieldValue time = FieldValue.serverTimestamp();
                    CommentsModel commentsModel = new CommentsModel(blogPostId,userProfileImageUri,comment,
                                                "","","","",time,
                                                currentUserId,username,userProfileImageUri);
                    // Firestore database.
                    String commentId = UUID.randomUUID().toString();
                    Map<String,Object> newCommentMap = commentsModel.firebaseDatabaseMap();
                    Log.d("newCommentMap",newCommentMap.toString());
                    makeTheDatabase(commentId,newCommentMap);
                    // Create post map and create the database in realtime database.
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now =LocalDateTime.now();
                    String date = dateTimeFormatter.format(now).toString();
                    NotificationsModel notificationsModel = new NotificationsModel(username,currentUserId,
                            date,userProfileImageUri,username+" has just posted a comment.");
                    Map<String,Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                    addToRealtimeDatabase(mapOfRealtimeDatabase,commentId);
//                    Log.d("myImageUri",userProfileImageUri);
                    new CommentsAdapter(commentsModelList);
                }
            }
        });
    }
    private void addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase, String commentId) {
        realtimeDatabaseReference.child("Notifications")
                .child(commentId)
                .setValue(mapOfRealtimeDatabase);
    }
    public void makeTheDatabase(String commentUID,Map<String,Object> map) {
        /**Making the map that will populate the database.*/
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments")
                .document(currentUserId + ":" + commentUID)
                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "There Was An Error Posting The Comment: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                commentTextView.setText("");
                Toast.makeText(context, "Comment sent", Toast.LENGTH_SHORT).show();
                Log.d("lastVisibleComment",lastVisibleComment.toString());
            }
        });
    }
}
