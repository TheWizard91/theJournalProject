 package com.thewizard91.thejournal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

//Firebase imports
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.QuerySnapshot;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.fragments.AccountFragment;
import com.thewizard91.thejournal.fragments.GalleryFragment;
import com.thewizard91.thejournal.fragments.HomeFragment;
import com.thewizard91.thejournal.fragments.NotificationsFragment;
import com.thewizard91.thejournal.models.notifications.NotificationsModel;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor;
import github.com.st235.lib_expandablebottombar.Notification;
import kotlin.collections.UCollectionsKt;

 public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

     TextView logo;
     AppBarLayout appBarLayout;
     CollapsingToolbarLayout collapsingToolbarLayout;
     public AppCompatImageButton logoutButton;
     AppBarLayout myAppBarLayout;
     private RecyclerView postsList;

     boolean isItReady;
     //Firebase instances
     FirebaseFirestore cloudBaseDatabase;
     FirebaseAuth userAuthorized;
     FirebaseUser currentUser;
     private FirebaseDatabase realtimeDatabase;
     private DatabaseReference realtimeDatabaseReference;
     DatabaseReference databaseReference;
//     SnapshotParser snapshotParser;
//     DataSnapshot dataSnapshot;
     FirebaseDatabase firebaseDatabase;

     public FloatingActionButton addFloatingButton;
     public FloatingActionButton sendBackFloatingActionButton;
//     ActionBar bottomActionBar;
//     public BottomNavigationView bottomNavigationView; //BottomNavigationView bottomNavigationView;
     public ExpandableBottomBar bottomAppBar; // BottomAppBar bottomAppBar
     github.com.st235.lib_expandablebottombar.Menu menu;
     Notification homeNotification;
     Notification bellNotification;

     // Fragments
     HomeFragment homeFragment;
     NotificationsFragment notificationsFragment;
     GalleryFragment galleryFragment;
     AccountFragment accountFragment;

     //Normal object instances
     private String currentUserId;
     private String username;

     private int countPosts;
     private int currentSize;
     @RequiresApi(api = Build.VERSION_CODES.M)
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         init();

     }

     @RequiresApi(api = Build.VERSION_CODES.M)
     @SuppressLint({"ObsoleteSdkInt", "CutPasteId"})
     private void init() {
         isItReady = false;
         collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
         appBarLayout = findViewById(R.id.main_activity_app_bar_layout_id);
         myAppBarLayout = findViewById(R.id.main_activity_app_bar_layout_id);
         logo = findViewById(R.id.main_activity_home_fragment_title_id);
         logoutButton = findViewById(R.id.logout);

         if (getSupportActionBar()!=null) this.getSupportActionBar().hide();

         // Checking if the user is already logged in
         userAuthorized = FirebaseAuth.getInstance();
         cloudBaseDatabase = FirebaseFirestore.getInstance();
         currentUser = userAuthorized.getCurrentUser();
         realtimeDatabase = FirebaseDatabase.getInstance();
         realtimeDatabaseReference = realtimeDatabase.getReference();
         /*Setting up the database from*/
         databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

         bottomAppBar = findViewById(R.id.main_bottom_nav);
         menu = bottomAppBar.getMenu();
         menu.add(new MenuItemDescriptor.Builder(this,R.id.home,R.drawable.ic_home,R.string.home,Color.GRAY).build());
         menu.add(new MenuItemDescriptor.Builder(this,R.id.notifications,R.drawable.ic_notifications,R.string.notifications,Color.GRAY).build());
         menu.add(new MenuItemDescriptor.Builder(this,R.id.gallery,R.drawable.ic_gallery,R.string.gallery,Color.GRAY).build());
         menu.add(new MenuItemDescriptor.Builder(this,R.id.account,R.drawable.ic_my_account,R.string.account,Color.GRAY).build());
         homeNotification = menu.findItemById(R.id.home).notification();
         bellNotification = menu.findItemById(R.id.notifications).notification();
         currentSize=0;

         if (currentUser != null) {
             currentUserId = userAuthorized.getCurrentUser().getUid();
             // Initialize the fragments starting with the homeFragment.
             // Meaning that the user will be directed into the homeFragment as
             // soon as she/he logged in and account is verified.
             homeFragmentMethod();
             fragmentsMenu();
             setDocumentsCount();
             addFloatingButton = findViewById(R.id.floating_action_button);
             sendBackFloatingActionButton = findViewById(R.id.send_back_button);
             sendBackFloatingActionButton.setVisibility(View.INVISIBLE);

             // Add an new post in Firestore database.
             AddANewPost();
         }
         myAppBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this);
     }
/*onResume count is 0.
* onStop count is 3
* onRestart count is 3*/

     @Override
     protected void onPause() {
         super.onPause();
//         setDocumentsCount();
         currentSize=countPosts;
         Log.d("onPause currentSize",String.valueOf(countPosts));
     }

     @Override
     protected void onRestart() {
         super.onRestart();
//         setDocumentsCount();
//         currentSize=countPosts;
//         Log.d("onRestart currentSize",String.valueOf(currentSize));
     }

     @Override
     protected void onStop() {
         super.onStop();
//         setDocumentsCount();
//         currentSize=countPosts;
//         Log.d("onStop currentSize",String.valueOf(currentSize));
     }

     @Override
     protected void onResume() {
         super.onResume();
//         currentSize=countPosts;
//         setDocumentsCount(currentSize);
         Log.d("onResume currentSize",String.valueOf(currentSize));
     }
     public void setDocumentsCount() {
         realtimeDatabaseReference.child("Notifications")
                 .addChildEventListener(new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                         NotificationsModel notificationsModel=snapshot.getValue(NotificationsModel.class);
//                         Log.d("main_username: ",notificationsModel.getUsername());
//                         Log.d("main_userUri: ",notificationsModel.getUserProfileImageURI());
//                         Log.d("main_date: ",notificationsModel.getDate());
//                         Log.d("main_key",snapshot.getKey());
//                         Log.d("main_snapshot",snapshot.toString());
//                         Log.d("main_value: ", String.valueOf(snapshot.getValue()));
//                         Log.d("main_size: ", String.valueOf(snapshot.getChildrenCount()));
//                         snapshot.
                         currentSize++;
//                         Log.d("print n times",String.valueOf(currentSize));
                         bellNotification.show(String.valueOf(currentSize));

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

     private void homeFragmentMethod() {
         homeFragment = new HomeFragment();
         replaceFragment(homeFragment);
         notificationsFragment = new NotificationsFragment();
         galleryFragment = new GalleryFragment();
         accountFragment = new AccountFragment();
     }

     private void AddANewPost() {
         addFloatingButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) { sendToAddANewPostActivity(); }
         });
     }

     private void sendToAddANewPostActivity() {
         startActivity(new Intent(this, NewPostActivity.class));
//         finish();
     }

     private void fragmentsMenu() {
         /**Replacing fragments according to the user's click.*/
         MainActivity mainActivity = MainActivity.this;
         bottomAppBar.setOnItemSelectedListener((view, item, byUser) -> {
             switch (item.getId()) {
                 case R.id.home:
                     replaceFragment(mainActivity.homeFragment);
//                     homeNotification.clear();
                     break;
                 case R.id.notifications:
                     replaceFragment(mainActivity.notificationsFragment);
                     bellNotification.clear();
                     break;
                 case R.id.gallery:
                     replaceFragment(mainActivity.galleryFragment);
                     break;
                 case R.id.account:
                     replaceFragment(mainActivity.accountFragment);
                     break;
             }
             return null;
         });
     }

     private void replaceFragment(Fragment fragment) {
         /*Replacing the fragment that the user is currently in by
         sending them where in the fragment of their choice as they press
         any of those present in the bottom_nav_bar.
          */

         FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
         beginTransaction.replace(R.id.content_frame, fragment);

         // We are at Home
         if(fragment == homeFragment) {
             beginTransaction.addToBackStack("HomeFragment");
         }

         beginTransaction.commit();
     }

         boolean isShow = false;
         int scrollRange = -1;

         @Override
         public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
             if (scrollRange == -1) {
                 scrollRange = myAppBarLayout.getTotalScrollRange();
             }
             if (scrollRange + verticalOffset == 0) {
                 isShow = true;
             } else if (isShow) {
                 isShow = false;
             }
         }
     @Override
     protected void onNewIntent(Intent intent) {

         super.onNewIntent(intent);
         handleIntent(intent);
     }

     private void handleIntent(Intent intent) {

         if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             String query = intent.getStringExtra(SearchManager.QUERY);
             //use the query to search your data somehow
         }
     }

     private void sendToLoginActivity() {
         // Start the Login Activity
         startActivity(new Intent(this, LogInActivity.class));
         finish();
     }

     private void setUsername() {
         cloudBaseDatabase.collection("Users")
                 .document(currentUserId)
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         DocumentSnapshot documentSnapshot = task.getResult();
                         List<String> list = new ArrayList<>();
                         Map<String, Object> map = documentSnapshot.getData();

                         if(map != null) {
                             for (Map.Entry<String, Object> entry : map.entrySet()) {
                                 list.add(entry.getValue().toString());
                             }
                             username = list.get(0);
                         }
                     }
                 });
     }
 }