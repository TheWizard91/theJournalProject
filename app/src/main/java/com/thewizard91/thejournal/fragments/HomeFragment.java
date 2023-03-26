package com.thewizard91.thejournal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.MainActivity;
import com.thewizard91.thejournal.adapters.PostAdapter;
import com.thewizard91.thejournal.models.post.PostModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView blogListView;
    public PostAdapter blogPostAdapter;
    public PostModel blogPostImageModel;
    public ArrayList blogPostImageModelList;

    public Context context;
    private FirebaseFirestore firebaseFirestore;
    public DocumentSnapshot lastVisiblePost;
    public String postId;
    public Boolean isThereAPostYes = false;
    private int size = 0;
    public boolean isTheFirstPageLoaded = true;
    View view;

    public MainActivity mainActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        blogListView = view.findViewById(R.id.home_fragment_recycler);
        context = container.getContext();
        blogPostImageModelList = new ArrayList();
        blogPostAdapter = new PostAdapter(this.blogPostImageModelList);
        blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        blogListView.setAdapter(this.blogPostAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        isThereAPostYes=false;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Creating new posts and displaying them.
            createTheFirstQuery();
            // Scroll down.
            showContentAsTheUserScrolls();
            // Get size of the query that way you load more posts.
            sizeOfQuery(size, new callback() {
                @Override
                public void onCallback(int i) {
                    firebaseFirestore.collection("Posts")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.size() > 0){
                                        size = queryDocumentSnapshots.size();
                                        Log.d("sizeIs",String.valueOf(size));
                                    }
                                }
                            });
                }
            });
            return view;
        }
        throw new AssertionError();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart","yes");
    }

    @Override
    public void onResume() {
        super.onResume();
        view.setVisibility(View.VISIBLE);
        MainActivity activity = (MainActivity) getActivity();
        activity.sendBackFloatingActionButton.setVisibility(View.INVISIBLE);
        activity.addFloatingButton.setVisibility(View.VISIBLE);
        activity.bottomAppBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause","yes");
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop","yes");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","yes");
    }

    private void sizeOfQuery(int size, callback myCallback) {
        myCallback.onCallback(size);
    }

    private interface callback {
        void onCallback(int i);
    }

    public void switchFragment(View view, String blogPostId, String currentUserId) {
        ((FragmentActivity) view.getContext())
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new CommentsFragment(blogPostId, currentUserId))
                .commit();
    }

    private void createTheFirstQuery() {
        /**Create the first three posts in Firebase database and display them in the app.*/
        firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots == null) {
                    throw new AssertionError();
                } else if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(context, "There is no post yet.", Toast.LENGTH_SHORT).show();
                    Log.d("no post yet","yes");
                } else {
                    lastVisiblePost = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    Log.d("There is a post","yes");
                    Log.d("lastVisiblePost",lastVisiblePost.toString());
                    for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                        Log.d("document", String.valueOf(document));
                        Log.d("queries", String.valueOf(queryDocumentSnapshots.getDocumentChanges()));
                        if (document.getType() == DocumentChange.Type.ADDED) {
                            postId = document.getDocument().getId();
//                            Log.d("postId",postId);
                            blogPostImageModel = document.getDocument()
                                    .toObject(PostModel.class)
                                    .withId(postId);
                            Log.d("blogPostImageModel", String.valueOf(blogPostImageModel));
                            blogPostImageModelList.add(blogPostImageModel);
                            Log.d("blogPostImageModelList", String.valueOf(blogPostImageModelList));
                            blogPostAdapter.notifyDataSetChanged();
                            Log.d("blogPostAdapter", blogPostAdapter.toString());
                        }
                    }
                }
            }
        });
    }

    private void showContentAsTheUserScrolls() {
        /**Upload more posts from Firebase database.*/
        blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    String postDescription = lastVisiblePost.getString("description");
                    isThereAPostYes = true;
                    Toast.makeText(context, postDescription, Toast.LENGTH_SHORT).show();
                    if (size>3) loadMorePosts();
                }
            }
        });
    }

    public void loadMorePosts() {
        firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisiblePost)
                .limit(2)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) { //
                        if (isThereAPostYes) {
                            lastVisiblePost = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                        for (DocumentChange documents : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documents.getType() == DocumentChange.Type.ADDED) {
                                postId = documents.getDocument().getId();
                                blogPostImageModel = documents.getDocument().toObject(PostModel.class).withId(postId);
                                blogPostImageModelList.add(blogPostImageModelList.size(), blogPostImageModel);
                                blogPostAdapter.notifyDataSetChanged();
                                Log.d("loadMore","yes");
                            }
                        }
                        isTheFirstPageLoaded = false;
                    }
                });
    }

}