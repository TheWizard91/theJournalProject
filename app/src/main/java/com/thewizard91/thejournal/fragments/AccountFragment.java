package com.thewizard91.thejournal.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.thewizard91.thejournal.models.listOne.ListenItemInListOne;
import com.thewizard91.thejournal.models.listTwo.ListenItemInListTwo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

//
public class AccountFragment extends Fragment {
//    https://www.tutlane.com/tutorial/android/android-listview-with-examples
//    https://stackoverflow.com/questions/27293979/android-listview-with-multiple-views
    public Context accountContext;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth userAuthorized;
    public CircleImageView userProfileImage;
    public TextView username;
    private TextView email;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        accountContext = container.getContext();
        userAuthorized = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuthorized.getCurrentUser();
        userProfileImage = view.findViewById(R.id.fragment_account_user_image);
        username = view.findViewById(R.id.username_title_in_fragment_account);
        email = view.findViewById(R.id.user_email_text_in_fragment_account);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String currentUser_id = Objects.requireNonNull(userAuthorized.getCurrentUser()).getUid();
            setUserProfileImageAndUsername(currentUser_id);
            setEmail(currentUser.getEmail());
            setListViewOne(view);
            setListViewTwo(view);
            return view;
        }
        throw new AssertionError();
    }

    private void setListViewTwo(View myView) {
        final String[] arrayOfListViewTwo = {"Edit Profile","Logout"};
        final int[] drawablesImage = {R.drawable.ic_person_pin,R.drawable.ic_logout};
        final int[] drawableArrow = {R.drawable.ic_arrow_forward,R.drawable.ic_arrow_forward};
        final ListenItemInListTwo[] rows = new ListenItemInListTwo[]{new ListenItemInListTwo(), new ListenItemInListTwo()};
        ArrayList listTwo = getListTwoData(arrayOfListViewTwo,drawablesImage,drawableArrow,rows);
        final ListView listViewTwo = myView.findViewById(R.id.fragment_account_listview_two);
        listViewTwo.setAdapter(new ListTwoAdapter(myView.getContext(), listTwo));
        listViewTwo.setOnItemClickListener((adapterView, view12, i, l) -> {
            ListenItemInListTwo listenItemInListTwo = (ListenItemInListTwo) listViewTwo.getItemAtPosition(i);
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

    private void setListViewOne(View myView) {
        final String[] arrayOfListViewOne = {"Posts","Favorite","Comments"};
        final int[] drawables = {R.drawable.ic_baseline_insert_photo_24,R.drawable.ic_favorite,R.drawable.ic_forum};
        final ListenItemInListOne[] rows = new ListenItemInListOne[]{new ListenItemInListOne(), new ListenItemInListOne(), new ListenItemInListOne()};
        ArrayList listOne = getListOfData(arrayOfListViewOne, drawables, rows);
        final ListView listViewOne = myView.findViewById(R.id.fragment_account_listview_one);
        listViewOne.setAdapter(new ListOneAdapter(myView.getContext(), listOne));
        listViewOne.setOnItemClickListener((adapterView, view1, i, l) -> {
            ListenItemInListOne listenItemInListOne = (ListenItemInListOne) listViewOne.getItemAtPosition(i);
            Toast.makeText(getContext(), "Selected: " + " " + listenItemInListOne.getDescription(),Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ArrayList getListTwoData(String[] arrayOfListViewTwo, int[] drawablesImage, int[] drawableArrows, ListenItemInListTwo[] rows) {

        ArrayList<ListenItemInListTwo> results = new ArrayList<>();

        for (int i = 0; i < arrayOfListViewTwo.length; i++) {
            rows[i].setImageUri(getResources().getDrawable(drawablesImage[i]));
            rows[i].setDescription(arrayOfListViewTwo[i]);
            rows[i].setForwardArrowImage(getResources().getDrawable(drawableArrows[i]));
            results.add(rows[i]);
        }

        return results;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ArrayList getListOfData(String[] arrayOfListViewOne, int[] drawables, ListenItemInListOne[] rows) {

        ArrayList<ListenItemInListOne> results = new ArrayList<>();

        for (int i = 0 ; i < arrayOfListViewOne.length; i++) {
            rows[i].setImage(getResources().getDrawable(drawables[i]));
            rows[i].setDescription(arrayOfListViewOne[i]);
            rows[i].setNumberOfElementsInSection(String.valueOf(i));
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

    private class StableArrayAdapter extends ArrayAdapter<String> {
//        https://www.vogella.com/tutorials/AndroidListView/article.html
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

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