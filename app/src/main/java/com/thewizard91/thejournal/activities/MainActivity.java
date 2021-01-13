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
import com.thewizard91.thejournal.R;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

 public class MainActivity extends AppCompatActivity {

     // Menu
//     Menu menu;
//     MenuInflater inflater;
     Menu inflater;
     SearchManager searchManager;
     SearchView searchView;

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
        /**
         * Display the search button
         */

//        inflater = getMenuInflater();
        inflater=menu;
        getMenuInflater().inflate(R.menu.my_menu, inflater);

        // Associate searchable configuration with the SearchView
        searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        hideOption(R.id.search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        int id = item.getGroupId();

        // non-inspection SimplifiedIfStatement
        if (id == R.id.search)
            return true;

        return super.onOptionsItemSelected(item);

    }


    private void init() {

        // Toolbar
        final Toolbar myToolbar = findViewById(R.id.my_collapsing_toolbar_id);
        setSupportActionBar(myToolbar);

//        final SearchView searchView =

        // Search bar animation
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AppBarLayout myAppBarLayout = findViewById(R.id.main_activity_app_bar_layout_id);
        myAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = false;
            int scrollRange= -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = myAppBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    isShow = true;
                    showOption(R.id.search);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.search);
                }
            }
        });

        // Checking if the user is already logged in
        userAuthorized = FirebaseAuth.getInstance();
        cloudBaseDatabase = FirebaseFirestore.getInstance();
        currentUser = userAuthorized.getCurrentUser();
        if(currentUser != null) {
            // TODO: Move on with the next stuff
        }
    }

     private void hideOption(int id) {
         MenuItem item = inflater.findItem(id);
         item.setVisible(false);
     }

     private void showOption(int id) {
         MenuItem item = inflater.findItem(id);
         item.setVisible(true);
     }

     private void sendToLoginActivity() {
        // Start the Login Activity
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }
}