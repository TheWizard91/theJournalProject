package com.thewizard91.thejournal.fragments;

import android.annotation.SuppressLint;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.MainActivity;
import com.thewizard91.thejournal.adapters.PostAdapter;
import com.thewizard91.thejournal.models.post.PostModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView postListView;
    public PostAdapter postAdapter;
    public PostModel postModel;
    public ArrayList<PostModel> postModelList;

    public Context context;
    private FirebaseFirestore firebasefirestore;
    public DocumentSnapshot lastVisiblePost;
    public String postId;
    public Boolean isPostInTheDatabase = false;
    private int size = 0;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        postListView = view.findViewById(R.id.home_fragment_recycler);
        postModelList = new ArrayList<>();
        postAdapter = new PostAdapter(postModelList);
        postListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postListView.setAdapter(postAdapter);
        firebasefirestore = FirebaseFirestore.getInstance();
        isPostInTheDatabase = false;

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Creating new posts and displaying them.
            createTheFirstQuery();
            // Scroll down.
            showContentAsTheUserScrolls();
            // Get size of the query that way you load more posts.
            sizeOfQuery(size, i -> firebasefirestore.collection("Posts")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.size() > 0){
                            size = queryDocumentSnapshots.size();
                        }
                    }));
            return view;
        }
        throw new AssertionError();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        view.setVisibility(View.VISIBLE);
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.backFloatingActionButton.setVisibility(View.INVISIBLE);
        activity.addFloatingActionButton.setVisibility(View.VISIBLE);
        activity.bottomAppBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
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


    @SuppressLint("NotifyDataSetChanged")
    private void createTheFirstQuery() {
        firebasefirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(context, "There is no post yet.", Toast.LENGTH_SHORT).show();
                    } else {
                        lastVisiblePost = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                            if (document.getType() == DocumentChange.Type.ADDED) {
                                postId = document.getDocument().getId();
                                postModel = document.getDocument()
                                        .toObject(PostModel.class)
                                        .withId(postId);
                                postModelList.add(postModel);
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void showContentAsTheUserScrolls() {
        postListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    String postDescription = lastVisiblePost.getString("description");
                    isPostInTheDatabase = true;
                    Toast.makeText(context, postDescription, Toast.LENGTH_SHORT).show();
                    if (size>3) loadMorePosts();
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadMorePosts() {
        firebasefirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisiblePost)
                .limit(6)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) { //
                        if (isPostInTheDatabase) {
                            lastVisiblePost = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                        for (DocumentChange documents : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documents.getType() == DocumentChange.Type.ADDED) {
                                postId = documents.getDocument().getId();
                                postModel = documents.getDocument().toObject(PostModel.class).withId(postId);
                                postModelList.add(postModelList.size(), postModel);
                                postAdapter.notifyDataSetChanged();
//                                Log.d("loadMore","yes");
                            }
                        }
                    }
                });
    }

}