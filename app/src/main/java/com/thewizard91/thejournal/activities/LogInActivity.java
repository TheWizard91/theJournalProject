package com.thewizard91.thejournal.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.log_in_activities_adds_on.ForgotPasswordActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText enterEmailOrUserName;
    private EditText password;
    private TransitionButton logInButton;
    private TextView forgotPassword;
    private TextView needAnAccount;
    private TextView useSocialMediaAccountToLogIn;

    private FirebaseAuth userAuthorized;

    String emailOrUsernameInserted;
    String passwordInserted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        init();
        triggerLogInButton();
        triggerForgotPasswordText();
        triggerNeedAccountText();
        triggerLogInWithSocialMediaAccount();
    }

    private void init() {
        progressBar = findViewById(R.id.log_in_progress_bar_id);
        enterEmailOrUserName = findViewById(R.id.log_in_enter_email_or_username_text_view_id);
        password = findViewById(R.id.log_in_enter_your_password_text_view_id);
        logInButton = findViewById(R.id.log_in_image_button_id);
        forgotPassword = findViewById(R.id.log_in_forgot_password_id);
        needAnAccount = findViewById(R.id.log_in_new_account_id);
        useSocialMediaAccountToLogIn = findViewById(R.id.log_in_with_social_media_account_id);

        userAuthorized = FirebaseAuth.getInstance();
    }

    private void triggerLogInWithSocialMediaAccount() {
        // Set the user to the activity that will allow the user to log in w social media accounts.
    }

    private void triggerNeedAccountText() {
        needAnAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToSigUpActivity();
            }
        });
    }

    private void sendToSigUpActivity() {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    private void triggerForgotPasswordText() {
        // Set the user to activity that will update the password.
        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToForgotPasswordActivity();
            }
        });
    }

    private void sendToForgotPasswordActivity() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        finish();
    }

    private void triggerLogInButton() {
        logInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logInButton.startAnimation();
//                String emailOrUsernameInserted =
                        emailOrUsernameInserted = enterEmailOrUserName.getText().toString();
//                String passwordInserted =
                        passwordInserted = password.getText().toString();
                if (!emailOrUsernameInserted.isEmpty() && !passwordInserted.isEmpty()){
                    userAuthorized.signInWithEmailAndPassword(emailOrUsernameInserted, passwordInserted)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
//                                        progressBar.setVisibility(0);
                                        logInButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                                new TransitionButton.OnAnimationStopEndListener() {
                                                    @Override
                                                    public void onAnimationStopEnd() {
                                                        sendToMainActivity();
                                                    }
                                                });
//                                        sendToMainActivity();
                                    }
//                                    Toast.makeText(LogInActivity.this, "Error: "+((Exception)Objects.requireNonNull(task.getException()))
//                                            .getMessage(), 0).show();
                                }
                            });
                }
            }
        });
    }

    private void sendToMainActivity() {
        userAuthorized.signInWithEmailAndPassword(emailOrUsernameInserted,passwordInserted);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}