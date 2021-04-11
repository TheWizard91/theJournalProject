package com.thewizard91.nao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        // Need to test the NewUserActivity responsible for the creation of a new user.
        sendToNewUsersActivity();
    }

    private void sendToNewUsersActivity() {
        Intent newUsersInfo=new Intent(this, NewUsersActivity.class);
        startActivity(newUsersInfo);
//        finish();
    }
}