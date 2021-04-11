package com.thewizard91.nao;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NewUsersActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPassword;
    private EditText userConfirmPassword;
    private Button createNewUserButton;

    private FirebaseAuth userAuthorized;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_users);

        init();
        createNewUserAccount();
    }

    private void createNewUserAccount() {
        createNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email=userEmail.getText().toString();
                String user_password=userPassword.getText().toString();
                String user_confirmation_password=userConfirmPassword.getText().toString();
                if(!TextUtils.isEmpty(user_email)&&!TextUtils.isEmpty(user_password)&&!TextUtils.isEmpty(user_confirmation_password)){
                    if(user_password.equals(user_confirmation_password)){
                        userAuthorized.createUserWithEmailAndPassword(user_email, user_password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            sendToNewUsersActivityInfo();
                                        } else {
                                            Toast.makeText(NewUsersActivity.this, "Error:"+task.getException(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(NewUsersActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void sendToNewUsersActivityInfo() {
        Intent newUserActivityInfoIntent=new Intent(NewUsersActivity.this, NewUsersActivityInfo.class);
        startActivity(newUserActivityInfoIntent);
        finish();
    }

    private void init() {
        userEmail=findViewById(R.id.user_email);
        userPassword=findViewById(R.id.user_password);
        userConfirmPassword=findViewById(R.id.user_password_confirmation);
        createNewUserButton=findViewById(R.id.create_new_user);

        userAuthorized=FirebaseAuth.getInstance();
    }
}