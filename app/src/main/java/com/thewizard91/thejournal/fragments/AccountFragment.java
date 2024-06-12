package com.thewizard91.thejournal.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.LogInActivity;
import com.thewizard91.thejournal.activities.UpdateAccountActivity;
import com.thewizard91.thejournal.adapters.ListOneAdapter;
import com.thewizard91.thejournal.adapters.ListTwoAdapter;
import com.thewizard91.thejournal.models.listOne.ListOneModel;
import com.thewizard91.thejournal.models.listTwo.ListTwo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
//    https://www.tutlane.com/tutorial/android/android-listview-with-examples
//    https://stackoverflow.com/questions/27293979/android-listview-with-multiple-views
    public Context accountContext;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth userAuthorized;
    private FirebaseUser currentUser;
    public CircleImageView userProfileImage;
    public TextView username;
    private TextView email;
    private String numberOfPosts;
    private String numberOfLikes;
    private String numberOfComments;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        accountContext = container.getContext();
        userAuthorized = FirebaseAuth.getInstance();
        currentUser = userAuthorized.getCurrentUser();
        userProfileImage = view.findViewById(R.id.fragment_account_user_image);
        username = view.findViewById(R.id.username_title_in_fragment_account);
        email = view.findViewById(R.id.user_email_text_in_fragment_account);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String currentUser_id = Objects.requireNonNull(userAuthorized.getCurrentUser()).getUid();
            setUserProfileImageAndUsername(currentUser_id);
            setEmail(currentUser.getEmail());
            useCallback();
            setListViewTwo(view);
            return view;
        }
        throw new AssertionError();
    }

    private void useCallback () {
        getUserInfo(e -> firebaseFirestore.collection("Users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    numberOfPosts = (String) documentSnapshot.get("numberOfPosts");
                    numberOfLikes = (String) documentSnapshot.get("numberOfLikes");
                    numberOfComments = (String) documentSnapshot.get("numberOfComments");
                    Log.d("numberOfPosts",numberOfPosts);
                    Log.d("numberOfLikes",numberOfLikes);
                    Log.d("numberOfComments",numberOfComments);
                    final String[] arrayOfFirebaseData = new String[] {numberOfPosts,numberOfLikes,numberOfComments};
                    setListViewOne(view,arrayOfFirebaseData);
                })
        );
    }
    private void getUserInfo (getUserInfoCallback myCallback) {
        myCallback.onGetUserInfo("");
    }
    private interface getUserInfoCallback {
        void onGetUserInfo(String info);
    }

    private void setListViewTwo(View myView) {
        final String[] arrayOfListViewTwo = {"Edit Profile","Logout"};
        final int[] drawablesImage = {R.drawable.ic_person_pin,R.drawable.ic_logout};
        final int[] drawableArrow = {R.drawable.ic_arrow_forward,R.drawable.ic_arrow_forward};
        final ListTwo[] rows = new ListTwo[]{new ListTwo(), new ListTwo()};
        ArrayList<ListTwo> listTwo = getListTwoData(arrayOfListViewTwo,drawablesImage,drawableArrow,rows);
        final ListView listViewTwo = myView.findViewById(R.id.fragment_account_listview_two);
        listViewTwo.setAdapter(new ListTwoAdapter(myView.getContext(), listTwo));
        listViewTwo.setOnItemClickListener((adapterView, view12, i, l) -> {
            ListTwo listenItemInListTwo = (ListTwo) listViewTwo.getItemAtPosition(i);
            Toast.makeText(getContext(), "Selected " + " " + listenItemInListTwo.getDescription(), Toast.LENGTH_SHORT).show();
            if (Objects.equals(listenItemInListTwo.getDescription(), "Logout")) {
                userAuthorized.signOut();
                sendToLoginActivity();
            } else {
                Intent intent = new Intent(getActivity(), UpdateAccountActivity.class);
                startActivity(intent);
                (requireActivity()).overridePendingTransition(0,0);
            }
        });
    }

    private void setListViewOne(@NonNull View myView, String[] arrayOfFirebaseData) {
        final String[] arrayOfListViewOne = {"Posts","Favorite","Comments"};
//        final String[] arrayOfFirebaseData = new String[] {numberOfPosts,numberOfLikes,numberOfComments};
        final int[] drawables = {R.drawable.ic_baseline_insert_photo_24,R.drawable.ic_favorite,R.drawable.ic_forum};
        final ListOneModel[] rows = new ListOneModel[]{new ListOneModel(), new ListOneModel(), new ListOneModel()};
        ArrayList<ListOneModel> listOneModel = getListOfData(arrayOfListViewOne, arrayOfFirebaseData, drawables, rows);
        final ListView listViewOne = myView.findViewById(R.id.fragment_account_listview_one);
        listViewOne.setAdapter(new ListOneAdapter(myView.getContext(), listOneModel));
        listViewOne.setOnItemClickListener((adapterView, view1, i, l) -> {
            ListOneModel listenItemInListOneModel = (ListOneModel) listViewOne.getItemAtPosition(i);
            Toast.makeText(getContext(), "Selected: " + " " + listenItemInListOneModel.getDescription(),Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ArrayList<ListTwo> getListTwoData(String[] arrayOfListViewTwo, int[] drawablesImage, int[] drawableArrows, ListTwo[] rows) {

        ArrayList<ListTwo> results = new ArrayList<>();

        for (int i = 0; i < arrayOfListViewTwo.length; i++) {
            rows[i].setImageUri(getResources().getDrawable(drawablesImage[i]));
            rows[i].setDescription(arrayOfListViewTwo[i]);
            rows[i].setForwardArrowImage(getResources().getDrawable(drawableArrows[i]));
            results.add(rows[i]);
        }

        return results;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ArrayList<ListOneModel> getListOfData(String[] arrayOfListViewOne, String[] arrayOfFirebaseData, int[] drawables, ListOneModel[] rows) {

        ArrayList<ListOneModel> results = new ArrayList<>();

        for (int i = 0 ; i < arrayOfListViewOne.length; i++) {
            rows[i].setImage(getResources().getDrawable(drawables[i]));
            rows[i].setDescription(arrayOfListViewOne[i]);
            rows[i].setNumberOfElementsInSection(arrayOfFirebaseData[i]);
            results.add(rows[i]);
        }

        return results;
    }

    private void sendToLoginActivity () {
        // Start the Login Activity
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        (requireActivity()).overridePendingTransition(0,0);
    }
    private void setEmail (String e) {
        email.setText(e);
    }
    @SuppressLint("CheckResult")
    private void setUserProfileImageAndUsername(String currentUserId) {
        firebaseFirestore.collection("Users")
                .document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    Map<String, Object> map;
                    DocumentSnapshot documentSnapshot = task.getResult();
                    username.setText(task.getResult().getString("username"));
                    if (documentSnapshot.exists() && (map = documentSnapshot.getData()) != null) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if ("userProfileImageURI".equals(entry.getKey())) {
                                String currentHolderImageURI = entry.getValue().toString();
                                RequestOptions placeholderOption = new RequestOptions();
                                placeholderOption.placeholder(R.drawable.ic_account_circle);
                                Glide.with(accountContext)
                                        .applyDefaultRequestOptions(placeholderOption)
                                        .load(currentHolderImageURI)
                                        .into(userProfileImage);
                            }
                        }
                    }
                });
    }

    public void onStart() {
        super.onStart();
    }

    private static class StableArrayAdapter extends ArrayAdapter<String> {
//        https://www.vogella.com/tutorials/AndroidListView/article.html
        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); i++) {
                mIdMap.put(objects.get(i), i);
            }
        }
        @Override
        public long getItemId (int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}