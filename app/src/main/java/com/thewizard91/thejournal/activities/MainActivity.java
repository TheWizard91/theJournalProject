 package com.thewizard91.thejournal.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

//Firebase imports
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaeger.library.StatusBarUtil;
import com.thewizard91.thejournal.R;

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

     boolean isItReady;
     //Firebase instances
     FirebaseFirestore cloudBaseDatabase;
     FirebaseAuth userAuthorized;
     FirebaseUser currentUser;

     //Normal object instances
     private String currentUserId;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         init();

//        sendToLoginActivity();
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         /*
           Populating hte menu bar with its options.
          */

//         inflater = getMenuInflater();
         this.menu = menu;
         getMenuInflater().inflate(R.menu.my_menu, menu);
//         inflater.inflate(R.menu.my_menu, menu);

//         menuItem = menu.findItem(R.id.search);

//         Log.d("menuIs:",menuItem.toString());// Search.

//         Associate searchable configuration with the SearchView
//         searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//         searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//         searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//         Snackbar.make(findViewById(android.R.id.content), "Replace with your own action", Snackbar.LENGTH_LONG)
//                 .setAction("Action", null).show();

//         hideOption(R.id.search);

         return true;
     }

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

     @SuppressLint("ObsoleteSdkInt")
     private void init() {

         /*
          Toolbar that need to be hidden. That means, this is the one with the label
          TheJournal, therefore, we do not need ot touch this code.
          */

//         final Toolbar myToolbar = findViewById(R.id.my_toolbar_id);
//         setSupportActionBar(myToolbar);

         // Get rid of the label in the too bar so that I can display the one that I want to.
//         Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

//         // TODO: Use the following code when necessary. That is when we need to hid the status bar i.e. creating the long story.
//         // If the Android version is lower than Jellybean, use this call to hide
//         // the status bar.
//         if (Build.VERSION.SDK_INT < 16) {
//             getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                     WindowManager.LayoutParams.FLAG_FULLSCREEN);
//         } else {
//             View decorView = getWindow().getDecorView();
//             // Hide the status bar.
//             int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//             decorView.setSystemUiVisibility(uiOptions);
//             // Remember that you should never show the action bar if the
//             // status bar is hidden, so hide that too if necessary.
//             ActionBar actionBar = getActionBar();
//             actionBar.hide();
//
//     }
//         //
//         StatusBarUtil.setTransparent(MainActivity.this);

         isItReady = false;
         myAppBarLayout = findViewById(R.id.main_activity_app_bar_layout_id);

         // Checking if the user is already logged in
         userAuthorized = FirebaseAuth.getInstance();
         cloudBaseDatabase = FirebaseFirestore.getInstance();
         currentUser = userAuthorized.getCurrentUser();
         if (currentUser != null) {
             // TODO: Move on with the next stuff
         }

         myAppBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this);

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