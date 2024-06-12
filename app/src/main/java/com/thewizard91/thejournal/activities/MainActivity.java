 package com.thewizard91.thejournal.activities;

 import android.annotation.SuppressLint;
 import android.content.Intent;
 import android.graphics.Color;
 import android.os.Build;
 import android.os.Bundle;
 import android.view.View;
 import android.widget.TextView;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.annotation.RequiresApi;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentActivity;
 import androidx.fragment.app.FragmentTransaction;

 import com.google.android.material.appbar.AppBarLayout;
 import com.google.android.material.floatingactionbutton.FloatingActionButton;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.auth.FirebaseUser;
 import com.google.firebase.database.ChildEventListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.thewizard91.thejournal.R;
 import com.thewizard91.thejournal.fragments.AccountFragment;
 import com.thewizard91.thejournal.fragments.GalleryFragment;
 import com.thewizard91.thejournal.fragments.HomeFragment;
 import com.thewizard91.thejournal.fragments.NotificationsFragment;

 import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
 import github.com.st235.lib_expandablebottombar.MenuItemDescriptor;
 import github.com.st235.lib_expandablebottombar.Notification;

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private TextView appBarLayoutTitle;
    private AppBarLayout appBarLayout;
    boolean isItReady;
    private DatabaseReference realTimeDatabaseReference;
    public FloatingActionButton addFloatingActionButton;
    public FloatingActionButton backFloatingActionButton;
    public ExpandableBottomBar bottomAppBar; // BottomAppBar bottomAppBar
    private Notification notificationsBell;
    // Fragments
    private HomeFragment homeFragment;
    private NotificationsFragment notificationsFragment;
//    private Notification homeNotification;
    private GalleryFragment galleryFragment;
    private AccountFragment accountFragment;
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
         appBarLayout = findViewById(R.id.main_activity_app_bar_layout_id);
         appBarLayoutTitle = findViewById(R.id.main_activity_home_fragment_title_id);

         if (getSupportActionBar()!=null) this.getSupportActionBar().hide();

         // Checking if the user is already logged in
         FirebaseAuth userAuthorized = FirebaseAuth.getInstance();
         //Firebase instances
         FirebaseUser currentUser = userAuthorized.getCurrentUser();
         FirebaseDatabase realtimeDatabase = FirebaseDatabase.getInstance();
         realTimeDatabaseReference = realtimeDatabase.getReference();

         bottomAppBar = findViewById(R.id.main_bottom_nav);
         github.com.st235.lib_expandablebottombar.Menu menu = bottomAppBar.getMenu();
         menu.add(new MenuItemDescriptor.Builder(this,R.id.home,R.drawable.ic_home,R.string.home,Color.GRAY).build());
         menu.add(new MenuItemDescriptor.Builder(this,R.id.notifications,R.drawable.ic_notifications,R.string.notifications,Color.GRAY).build());
         menu.add(new MenuItemDescriptor.Builder(this,R.id.gallery,R.drawable.ic_gallery,R.string.gallery,Color.GRAY).build());
         menu.add(new MenuItemDescriptor.Builder(this,R.id.account,R.drawable.ic_my_account,R.string.account,Color.GRAY).build());

         notificationsBell = menu.findItemById(R.id.notifications).notification();
         currentSize= 0;

         if (currentUser != null) {
             //Normal object instances
             // Initialize the fragments starting with the homeFragment.
             // Meaning that the user will be directed into the homeFragment as
             // soon as she/he logged in and account is verified.
             homeFragmentMethod();
             fragmentsMenu();
             setDocumentsCount();
             addFloatingActionButton = findViewById(R.id.floating_action_button);
             backFloatingActionButton = findViewById(R.id.send_back_button);
             backFloatingActionButton.setVisibility(View.INVISIBLE);

             // Add an new post in Firestore database.
             AddANewPost();

         }
         appBarLayout.addOnOffsetChangedListener(this);
     }

     @Override
     protected void onPause() {
         super.onPause();
         deleteNotifications();
     }

     @Override
     protected void onRestart() {
         super.onRestart();
     }

     @Override
     protected void onStop() {
         super.onStop();
     }

     @Override
     protected void onResume() {
         super.onResume();
     }
     public void setDocumentsCount() {
         realTimeDatabaseReference.child("Notifications")
                 .addChildEventListener(new ChildEventListener() {
                     @Override
                     public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                         // currentSize increase until we count all the items in the queries.
                         currentSize++;
                         notificationsBell.show(String.valueOf(currentSize));
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
         addFloatingActionButton.setOnClickListener(v -> sendToAddANewPostActivity());
     }

     private void sendToAddANewPostActivity() {
         startActivity(new Intent(this, NewPostActivity.class));
     }

     @SuppressLint("NonConstantResourceId")
     private void fragmentsMenu() {

         MainActivity mainActivity = MainActivity.this;
         String home_page_title = "Home Page";
         String notifications_page_title = "Notifications";
         String gallery_page_title = "Your Images";
         String account_page_title = "Update Your Account";

         bottomAppBar.setOnItemSelectedListener((view, item, byUser) -> {

             switch (item.getId()) {

                 case R.id.home:
                     replaceFragment(mainActivity.homeFragment);
                     appBarLayoutTitle.setText(home_page_title);
                     break;

                 case R.id.notifications:
                     replaceFragment(mainActivity.notificationsFragment);
                     addFloatingActionButton.setVisibility(View.INVISIBLE);
                     appBarLayoutTitle.setText(notifications_page_title);
    //                     deleteNotifications();
                     notificationsBell.clear();
                     break;

                 case R.id.gallery:
                     replaceFragment(mainActivity.galleryFragment);
                     addFloatingActionButton.setVisibility(View.INVISIBLE);
                     appBarLayoutTitle.setText(gallery_page_title);
                     break;

                 case R.id.account:
                     replaceFragment(mainActivity.accountFragment);
                     addFloatingActionButton.setVisibility(View.INVISIBLE);
                     appBarLayoutTitle.setText(account_page_title);
                     backButtonHandler();

                     break;
             }

             return null;
         });
     }
     private void backButtonHandler() {
         backFloatingActionButton.setOnClickListener(view -> ((FragmentActivity) view.getContext())
                 .getSupportFragmentManager()
                 .beginTransaction()
                 .replace(R.id.content_frame, new HomeFragment())
                 .commit());
     }

     private void deleteNotifications() {
         realTimeDatabaseReference.child("Notifications").removeValue();
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
         public void onOffsetChanged(AppBarLayout app_bar_layout, int verticalOffset) {
             if (scrollRange == -1) {
                 scrollRange = appBarLayout.getTotalScrollRange();
             }
             if (scrollRange + verticalOffset == 0) {
                 isShow = true;
             } else if (isShow) {
                 isShow = false;
             }
         }
 }