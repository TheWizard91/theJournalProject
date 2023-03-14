package com.thewizard91.thejournal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;
import com.thewizard91.thejournal.adapters.NotificationsAdapter;
import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private RecyclerView notificationsListView;
    public NotificationsAdapter notificationAdapter;
    public NotificationsModel notificationsModel;
    public ArrayList notificationModelList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot documentSnapshot;
    private FirebaseDatabase realtimeDatabase;
    private DatabaseReference realtimeDatabaseReference;
    public String notificationId;
    public Boolean isThereANotification = false;
    public int size = 0;
    public  Boolean isTheFirstPageLoaded = true;
    View view;
    private DocumentSnapshot lastVisibleNotification;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notofications, container, false);
        notificationsListView = view.findViewById(R.id.notifications_fragment_recycler);
        context = container.getContext();
        notificationModelList = new ArrayList();
        notificationAdapter = new NotificationsAdapter(this.notificationModelList);
        notificationsListView.setLayoutManager(new LinearLayoutManager((getActivity())));
        notificationsListView.setAdapter(this.notificationAdapter);
//        Log.d("notificationAdapter...", String.valueOf(notificationAdapter));
        firebaseFirestore = FirebaseFirestore.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeDatabaseReference = realtimeDatabase.getReference();
        notificationId = "G9p72sm630SoAdAir6WQs83wXQF3";
        isThereANotification = false;

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            createTheFirstQuery();
            showMoreNotificationsAsTheUSerScrolls();
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void showMoreNotificationsAsTheUSerScrolls() {
    }

    /*TODO:I used the same trick that I used on the comments -- to initialize the first query with the first 100 comments.
    *  That way, I do not worry about to load more comments (in this case notifications).*/
    private void createTheFirstQuery() {
        realtimeDatabaseReference.child("Notifications")
//                .child(notificationId)
                .orderByPriority()
                .limitToFirst(100)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot == null) {
                            throw new AssertionError();
                        } else if(!snapshot.exists()) {
                            Toast.makeText(context,"There is notification yet.",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,"Else",Toast.LENGTH_SHORT).show();
//                            lastVisibleNotification = snapshot.
                            notificationsModel = snapshot.getValue(NotificationsModel.class).withId(notificationId);
//                            Log.d("uName",notificationsModel.getUsername());
//                            Log.d("uId",notificationsModel.getUserId());
//                            Log.d("time", "String.valueOf(notificationsModel.getTime())");
//                            Log.d("text",notificationsModel.getNotificationText());
//                            Log.d("uri",notificationsModel.getUserProfileImageURI());
//                            Log.d("notificationsModel",notificationsModel.toString());
                            notificationModelList.add(notificationsModel);
                            Log.d("notificationModelList", String.valueOf(notificationModelList));
                            notificationAdapter.notifyDataSetChanged();
                            Log.d("notificationAdapter", String.valueOf(notificationAdapter));
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}