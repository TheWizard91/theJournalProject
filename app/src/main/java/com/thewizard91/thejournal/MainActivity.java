 package com.thewizard91.thejournal;

import androidx.appcompat.app.AppCompatActivity;

//Firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thewizard91.thejournal.sing_up_adds_on.AccountSettingsActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

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

        // Checking if the user is already logged in
        userAuthorized = FirebaseAuth.getInstance();
        cloudBaseDatabase = FirebaseFirestore.getInstance();
        currentUser = userAuthorized.getCurrentUser();
        if(currentUser != null) {
            // TODO: Move on with the next stuff
        }
        sendToLoginActivity();
    }

    private void sendToLoginActivity() {
        // Start the Login Activity
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }
}