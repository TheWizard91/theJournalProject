package com.thewizard91.thejournal.fragments;

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
import com.thewizard91.thejournal.models.comments.CommentsModel;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class CommentsFragment extends Fragment {
    private String post_id;
    public String comments_id;
    public TextView comment_text_view;
    public CommentsAdapter comments_adapter;
    private RecyclerView comments_list_view;
    public CommentsModel comments_model;
    public List<CommentsModel> comments_model_list;
    public Context context;
    public String current_user_id;
    private FirebaseFirestore firebasefirestore;
    private FirebaseDatabase realtime_database;
    private DatabaseReference realtime_database_reference;
    public Boolean is_the_first_load_of_comments_loaded = true;
    public DocumentSnapshot last_visible_comment;
    public  String username;
    public String user_profile_image_uri;
    private int size = 0;
    private int currentSize;
    private View view;
    private MainActivity activity;
    private Button send_comment_button;

    public CommentsFragment() {}
    public CommentsFragment(String post_id, String current_user_id) {
        this.post_id = post_id;
        this.current_user_id = current_user_id;
        Log.d("can I refresh?","yes");
    }

    /*TODO: When the query is empty and I add a comment by pressing the send button, the comment info is stored on the bd.
    *  However, as soon as I scroll down, the app crashes. One way to fix this is by refreshing the fragment.*/
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments, container, false);
        comments_list_view = view.findViewById(R.id.comments_recyclerview);
        comment_text_view = view.findViewById(R.id.comment_edittext);
        comments_list_view = view.findViewById(R.id.comments_recyclerview);
        send_comment_button = view.findViewById(R.id.comment_button);
        context = container.getContext();
        comments_model_list = new ArrayList();
        comments_adapter = new CommentsAdapter(comments_model_list, post_id);
        comments_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        comments_list_view.setAdapter(comments_adapter);
        firebasefirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebasefirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        realtime_database = FirebaseDatabase.getInstance();
        realtime_database_reference = realtime_database.getReference();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        username = currentUser.getDisplayName();
        getUserImageProfileUri();

        // Getting rid of navigation button at the bottom.
        activity = (MainActivity) getActivity();
        activity.addFloatingActionButton.setVisibility(View.INVISIBLE);
        activity.bottomAppBar.setVisibility(View.INVISIBLE);
        activity.backFloatingActionButton.setVisibility(View.VISIBLE);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // Send the info to firebase by retrieving the data from user and making the map
            sendCommentToDatabase();
            // Create the first few comments and display them.
            createTheFirstQuery();

            sendToHomeFragment ();
            // DO NOT TOUCH THIS.
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
        firebasefirestore.collection("Users")
                .document(current_user_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_profile_image_uri = (String) documentSnapshot.get("userProfileImageURI");
                        username = (String) documentSnapshot.get("username");
                    }
                });
    }

    private void loadMoreCommentsAsTheUserScrollsDown() {
        /**It uses the help of loadMoreComments to load more content on the adapter, as the user scroll down.**/
        comments_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(context, "Reached bottom " + last_visible_comment.getString("commentText"), Toast.LENGTH_SHORT).show();
                    is_the_first_load_of_comments_loaded = true;
                    /*TODO: The following method is supposed to load more comments from the database,
                     * but an issues has occurred please get it fixed. I don't know that the issue is since
                     * I did not code in this project for months and found it in this state. However, there
                     * was an error that I fixed (could not access comments_id from the model's id class).It was
                     * a casting issue.
                     */
//                    loadMoreComments();
                }
            }
        });
    }

    public void loadMoreComments() {
        /**Helper of loadMoreCommentsAsTheUserScrollsDown, it load more content on the adapter.**/
        firebasefirestore.collection("Posts")
                .document(post_id)
                .collection("Comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .startAfter(last_visible_comment)
                .limit(5) // loading up to five comments at time.
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots == null) {
                            throw new AssertionError();
                        } else if (!queryDocumentSnapshots.isEmpty()) {
                            if (is_the_first_load_of_comments_loaded.booleanValue()) {
                                last_visible_comment = queryDocumentSnapshots.getDocuments()
                                        .get(queryDocumentSnapshots.size() - 1);
                                Log.d("last_visible_comment",last_visible_comment.getTimestamp("timestamp").toString());
                            }
                            for (DocumentChange documents : queryDocumentSnapshots.getDocumentChanges()) {
                                if (documents.getType() == DocumentChange.Type.ADDED) {
                                    comments_id = documents.getDocument().getId();
                                    comments_model = documents.getDocument().toObject(CommentsModel.class).withId(comments_id);
                                    comments_model_list.add(comments_model_list.size(), comments_model);
                                    comments_adapter.notifyDataSetChanged();
//                                    last_visible_comment = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                                }
                            }
                            is_the_first_load_of_comments_loaded = false;
                        }
                    }
                });
    }

    private void createTheFirstQuery() {
        /**Create the first query, plus, display it.**/
        firebasefirestore.collection("Posts")
                .document(post_id)
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
                            last_visible_comment = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
//                            Log.d("There is a comment","yes");
                            for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                                if (document.getType() == DocumentChange.Type.ADDED) {
                                    comments_id = document.getDocument().getId();
//                                    Log.d("comments_id",comments_id);
                                    comments_model = document.getDocument().toObject(CommentsModel.class).withId(comments_id);
//                                    Log.d("comments_model",comments_model.toString());
                                    comments_model_list.add(comments_model);
//                                    Log.d("comments_model_list",comments_model_list.toString());
                                    comments_adapter.notifyDataSetChanged();
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
        activity.backFloatingActionButton.setOnClickListener(new View.OnClickListener() {
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
        send_comment_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View view) {
                String comment = comment_text_view.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    FieldValue time = FieldValue.serverTimestamp();
                    CommentsModel comments_model = new CommentsModel(post_id,user_profile_image_uri,comment,
                                                "","","","",time,
                                                current_user_id,username,user_profile_image_uri);
                    // Firestore database.
                    String comments_id = UUID.randomUUID().toString();
                    Map<String,Object> newCommentMap = comments_model.firebaseDatabaseMap();
                    Log.d("newCommentMap",newCommentMap.toString());
                    makeTheDatabase(comments_id,newCommentMap);

                    // Create post map and create the database in realtime database.
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now =LocalDateTime.now();
                    String date = dateTimeFormatter.format(now).toString();
                    NotificationsModel notificationsModel = new NotificationsModel(username,current_user_id,
                            date,user_profile_image_uri,username+" has just posted a comment.");
                    Map<String,Object> mapOfRealtimeDatabase = notificationsModel.realTimeDatabaseMap();
                    addToRealtimeDatabase(mapOfRealtimeDatabase,comments_id);
//                    Log.d("myImageUri",user_profile_image_uri);
                    new CommentsAdapter(comments_model_list);
                }
            }
        });
    }
    private void addToRealtimeDatabase(Map<String, Object> mapOfRealtimeDatabase, String comments_id) {
        realtime_database_reference.child("Notifications")
                .child(comments_id)
                .setValue(mapOfRealtimeDatabase);
    }
    public void makeTheDatabase(String commentUID,Map<String,Object> map) {
        /**Making the map that will populate the database.*/
        firebasefirestore.collection("Posts/" + post_id + "/Comments")
                .document(current_user_id + ":" + commentUID)
                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "There Was An Error Posting The Comment: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                comment_text_view.setText("");
                Toast.makeText(context, "Comment sent", Toast.LENGTH_SHORT).show();
                Log.d("last_visible_comment",last_visible_comment.toString());
            }
        });
    }
}
