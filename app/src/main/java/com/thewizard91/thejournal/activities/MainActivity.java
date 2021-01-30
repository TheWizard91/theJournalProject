 package com.thewizard91.thejournal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

//Firebase imports
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaeger.library.StatusBarUtil;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.fragments.AccountFragment;
import com.thewizard91.thejournal.fragments.HomeFragment;
import com.thewizard91.thejournal.fragments.LikesFragment;
import com.thewizard91.thejournal.fragments.NotificationsFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Objects;

 public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

     // Menu
     Menu menu;
     MenuInflater inflater;
//     Menu inflater;
     SearchManager searchManager;
     SearchView searchView;

     MenuItem menuItem;

     AppBarLayout myAppBarLayout;

     AppCompatButton newestButton;
     AppCompatButton popularButton;
     AppCompatButton followingButton;
     AppCompatButton otherButton;

     boolean isItReady;
     //Firebase instances
     FirebaseFirestore cloudBaseDatabase;
     FirebaseAuth userAuthorized;
     FirebaseUser currentUser;

     FloatingActionButton addFloatingButton;
//     ActionBar bottomActionBar;
     private BottomNavigationView bottomNavigationView;

     // Fragments
     HomeFragment homeFragment;
     NotificationsFragment notificationsFragment;
     LikesFragment likesFragment;
     AccountFragment accountFragment;

     //Normal object instances
     private String currentUserId;
     private FragmentTransaction fragmentTransaction;

     @RequiresApi(api = Build.VERSION_CODES.M)
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         if(currentUser != null) init();

        sendToLoginActivity();
     }
//
//     @Override
//     public boolean onCreateOptionsMenu(Menu menu) {
//         /*
//           Populating hte menu bar with its options.
//          */
//
////         inflater = getMenuInflater();
//         this.menu = menu;
//         getMenuInflater().inflate(R.menu.my_menu, menu);
//         inflater.inflate(R.menu.my_menu, menu);
//
//         menuItem = menu.findItem(R.id.info);
//
//         Log.d("menuIs:",menuItem.toString());// Search.
//
//         // Associate searchable configuration with the SearchView
//         searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//         searchView = (SearchView) menu.findItem(R.id.info).getActionView();
//         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//         Snackbar.make(findViewById(android.R.id.content), "Replace with your own action", Snackbar.LENGTH_LONG)
//                 .setAction("Action", null).show();
//
////         hideOption(R.id.search);
//
//         return true;
//     }

//     @Override
//     public boolean onOptionsItemSelected(MenuItem item) {
//
//         // Handle action bar item clicks here. The action bar will
//         // automatically handle clicks on the Home/Up button, so long
//         // as you specify a parent activity in AndroidManifest.xml
//
//         int id = item.getGroupId();
//
//         // non-inspection SimplifiedIfStatement
////         if (id == R.id.search)
//             return true;
//
//         return super.onOptionsItemSelected(item);
//
//     }


     private void hideOption(int id) {
         MenuItem item = menu.findItem(id);
         item.setVisible(false);
     }

     private void showOption(int id) {
         MenuItem item = menu.findItem(id);
         item.setVisible(true);
//         Log.d("ItIs:", menuItem.toString());
     }

     @RequiresApi(api = Build.VERSION_CODES.M)
     @SuppressLint("ObsoleteSdkInt")
     private void init() {

         /*
          Toolbar that need to be hidden. That means, this is the one with the label
          TheJournal, therefore, we do not need ot touch this code.
          */

         isItReady = false;
         myAppBarLayout = findViewById(R.id.main_activity_app_bar_layout_id);

         // Checking if the user is already logged in
         userAuthorized = FirebaseAuth.getInstance();
         cloudBaseDatabase = FirebaseFirestore.getInstance();
         currentUser = userAuthorized.getCurrentUser();

         bottomNavigationView = findViewById(R.id.bottom_navigation_view);
         if (currentUser != null) {
             // TODO: Move on with the next stuff
             // Get the user's id
             currentUserId = userAuthorized.getCurrentUser().getUid();

             // Initialize the fragments starting with the homeFragment.
             // Meaning that the user will be directed into the homeFragment as
             // soon as she/he logged in and account is verified.
             homeFragment = new HomeFragment();
             replaceFragment(homeFragment);
             notificationsFragment = new NotificationsFragment();
             likesFragment = new LikesFragment();
             accountFragment = new AccountFragment();
             fragmentsMenu();

             // The buttons on top of the posts.
             newestButton = findViewById(R.id.newest_id);
             popularButton = findViewById(R.id.popular_id);
             followingButton = findViewById(R.id.following_id);
             otherButton = findViewById(R.id.other_id);

             addFloatingButton = findViewById(R.id.floating_action_button);

             //
             AddANewPost();
         }

         myAppBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this);

     }

     private void AddANewPost() {
         addFloatingButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 sendToAddANewPostActivity();
             }
         });
     }

     private void sendToAddANewPostActivity() {
         startActivity(new Intent(this, AddANewPost.class));
         finish();
     }

     private void fragmentsMenu() {
         /*
         Replacing fragments according to the user's click.
          */


         MainActivity mainActivity = MainActivity.this;
         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @SuppressLint("NonConstantResourceId")
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 switch (item.getItemId()) {
                     case R.id.home:
                         replaceFragment(mainActivity.homeFragment);
                         _helper_of_fragment_menu(item.getItemId());
                         return true;
                     case R.id.notifications:
                         _helper_of_fragment_menu(item.getItemId());
                         replaceFragment(mainActivity.notificationsFragment);
                         return true;
                     case R.id.likes:
                         replaceFragment(mainActivity.likesFragment);
                         _helper_of_fragment_menu(item.getItemId());
                     case R.id.account:
                         replaceFragment(mainActivity.accountFragment);
                         _helper_of_fragment_menu(item.getItemId());
                         return true;
                 }
                 return false;
             }
         });
     }

     private void _helper_of_fragment_menu(int itemId) {
         //Check that we are at home fragment
         if(itemId != R.id.home){
             newestButton.setVisibility(View.INVISIBLE);
             popularButton.setVisibility(View.INVISIBLE);
             followingButton.setVisibility(View.INVISIBLE);
             otherButton.setVisibility(View.INVISIBLE);
         } else {
             newestButton.setVisibility(View.VISIBLE);
             popularButton.setVisibility(View.VISIBLE);
             followingButton.setVisibility(View.VISIBLE);
             otherButton.setVisibility(View.VISIBLE);
         }
     }

     private void replaceFragment(Fragment fragment) {
         /*
         Replacing the fragment that the user is currently in by
         sending them where in the fragment of their choice as they press
         any of those present in the bottom_nav_bar.
          */

         FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
         fragmentTransaction = beginTransaction;
         beginTransaction.replace(R.id.content_frame, fragment);

         // I we are at Home
         if(fragment == homeFragment) {
             fragmentTransaction.addToBackStack("HomeFragment");
         }
         fragmentTransaction.commit();
     }

//     new AppBarLayout.OnOffsetChangedListener() {

         boolean isShow = false;
         int scrollRange = -1;

         @Override
         public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
             if (scrollRange == -1) {
                 scrollRange = myAppBarLayout.getTotalScrollRange();
             }
             if (scrollRange + verticalOffset == 0) {
                 isShow = true;
//                         if(isItReady)
//                            showOption(R.id.search);
             } else if (isShow) {
                 isShow = false;
//                         if(isItReady)
//                             hideOption(R.id.search);
             }
         }
//     }

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

 }