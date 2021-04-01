package com.thewizard91.thejournal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.sing_up_adds_on.AccountSettingsActivity;

import android.content.Intent;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText confirmPassword;
    private TextView haveAnAccountAlready;
    private ImageButton signUpButton;
    
    private FirebaseAuth userAuthorized;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        
        progressBar = findViewById(R.id.sign_up_progress_bar_id);
        enterEmail = findViewById(R.id.sign_up_mail_address_text_view_id);
        enterPassword = findViewById(R.id.sign_up_password_text_view_id);
        confirmPassword = findViewById(R.id.sign_password_confirmation_id);
        haveAnAccountAlready = findViewById(R.id.already_have_an_account_message_id);
        signUpButton = findViewById(R.id.sign_up_button_id);
        userAuthorized = FirebaseAuth.getInstance();
//        finishTheLogInActivity();
        createANewAccount();
        triggerHaveAlreadyAnAccount();
    }

    private void triggerHaveAlreadyAnAccount() {
        haveAnAccountAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogInActivity();
            }
        });
    }

    private void sendToLogInActivity() {
        startActivity(new Intent(this, LogInActivity.class));
    }

    private void createANewAccount() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                String emailInserted = enterEmail.getText().toString();
                String passwordInserted = enterPassword.getText().toString();
                String passwordConfirmed = confirmPassword.getText().toString();
                if(!TextUtils.isEmpty(emailInserted) && !TextUtils.isEmpty(passwordConfirmed) && !TextUtils.isEmpty(passwordConfirmed)){
                    if(passwordInserted.equals(passwordConfirmed)){
                        progressBar.setVisibility(View.VISIBLE);
                        userAuthorized.createUserWithEmailAndPassword(emailInserted, passwordInserted)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            sendToAccountSettingsActivity();
//                                            test();
                                        } else {
                                            Toast.makeText(SignUpActivity.this,
                                                    "Error: "+((Exception) Objects.requireNonNull(task.getException()))
                                            .getMessage(), Toast.LENGTH_LONG).show();
                                        }
//                                        progressBar.setVisibility(4);
                                    }
                                });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void sendToAccountSettingsActivity() {
        startActivity(new Intent(this, AccountSettingsActivity.class));
        finish();
    }

    private void finishTheLogInActivity() {
        haveAnAccountAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                Toast.makeText(SignUpActivity.this, "DO NOTHING", Toast.LENGTH_SHORT).show();
            }
        });
    }
}