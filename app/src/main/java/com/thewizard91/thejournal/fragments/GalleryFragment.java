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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.adapters.ImagesAdapter;
import com.thewizard91.thejournal.models.image.ImageModel;

import java.util.ArrayList;
import java.util.Objects;

public class GalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    public Context context;
    private ArrayList<String> urisListOfImages;
    private FirebaseFirestore firebasefirestore;
    public Boolean isFirstPageFirstLoad = true;
    public DocumentSnapshot lastVisibleImage;
    public boolean reachedBottom;
    private String userId;
    View view;
    public GalleryFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Views
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        context = container.getContext();
        recyclerView = view.findViewById(R.id.gallery_fragment_recycler); // recycler

        //Database (Firebase) instances.
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebasefirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            createFirsQuery();
            asTheUserScrolls();
            return view;
        }

        throw new AssertionError();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void initFlexBoxProgrammatically (ArrayList<ImageModel> listOfImages) {

        RecyclerView localRecyclerView = view.findViewById(R.id.gallery_fragment_recycler); // recycler
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setAlignItems(AlignItems.CENTER);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setJustifyContent(JustifyContent.FLEX_END);
        localRecyclerView.setLayoutManager(layoutManager);
        ImagesAdapter flexImageAdapter = new ImagesAdapter(context, listOfImages);
        localRecyclerView.setAdapter(flexImageAdapter);
        flexImageAdapter.notifyDataSetChanged();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ArrayList<ImageModel> setAllTheImages() {

        ArrayList<ImageModel> flexImageList = new ArrayList<>();

        for (final String s : urisListOfImages) {
            Log.d("s",s);
            ImageModel flexBoxImageModel = new ImageModel();
            flexBoxImageModel.setImageURI(s);
            flexImageList.add(flexBoxImageModel);
        }

        return flexImageList;
    }

    private void asTheUserScrolls() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                reachedBottom = !recyclerView.canScrollVertically(1);
                if (reachedBottom) {
                    Toast.makeText(context, "Reached end album is: " + lastVisibleImage.getString("description"), Toast.LENGTH_SHORT).show();
                    isFirstPageFirstLoad = true;
                    loadMoreImages();
                }
            }
        });
    }

    private void createFirsQuery() {
        firebasefirestore.collection("Gallery")
                .document(userId)
                .collection("images")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .limit(6)
                .addSnapshotListener((value, error) -> {
                    if (value == null) {
                        throw new AssertionError();
                    } else  if (value.isEmpty()) {
                        Toast.makeText(context,"There is no image yet",Toast.LENGTH_SHORT).show();
                    } else {
                        lastVisibleImage = value.getDocuments().get(value.size() - 1);
                        urisListOfImages = new ArrayList<>();
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                urisListOfImages.add(Objects.requireNonNull(documentChange.getDocument().get("imageURI")).toString());
                            }
                        }
                        ArrayList<ImageModel> images = setAllTheImages();
                        initFlexBoxProgrammatically(images);
                    }
                });
    }

    public void loadMoreImages() {
        firebasefirestore.collection("Gallery")
                .document(userId)
                .collection("images")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisibleImage)
                .limit(2).addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) {
                        if (isFirstPageFirstLoad) {
                            lastVisibleImage = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                        for (DocumentChange documents : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documents.getType() == DocumentChange.Type.ADDED) {
                                urisListOfImages.add(Objects.requireNonNull(documents.getDocument().get("imageURI")).toString());
                            }
                        }
                        ArrayList<ImageModel> images = setAllTheImages();
                        initFlexBoxProgrammatically(images);
                    }
                });
    }
}