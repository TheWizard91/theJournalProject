package com.thewizard91.thejournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.log_in_activities_adds_on.ForgotPasswordActivity;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private TextInputEditText enterEmailOrUserName;
    private TextInputEditText password;
    private TransitionButton logInButton;
    private TextView forgotPassword;
    private TextView needAnAccount;
//    private TextView useSocialMediaAccountToLogIn;

    private String emailOrUsernameInserted;
    private String passwordInserted;

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
        enterEmailOrUserName = findViewById(R.id.log_in_enter_email_or_username_text_view_id);
        password = findViewById(R.id.log_in_enter_your_password_text_view_id);
        logInButton = findViewById(R.id.log_in_image_button_id);
        forgotPassword = findViewById(R.id.log_in_forgot_password_id);
        needAnAccount = findViewById(R.id.log_in_new_account_id);
//        useSocialMediaAccountToLogIn = findViewById(R.id.log_in_with_social_media_account_id);
    }

    private void triggerLogInWithSocialMediaAccount() {
        // Set the user to the activity that will allow the user to log in w social media accounts.
    }

    private void triggerNeedAccountText() {
        /*Handler for new activity.*/
        needAnAccount.setOnClickListener(v -> sendToSigUpActivity());
    }

    private void sendToSigUpActivity() {
        /*Send to Sign up activity.*/
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    private void triggerForgotPasswordText() {
        // Set the user to activity that will update the password.
        forgotPassword.setOnClickListener(v -> sendToForgotPasswordActivity());
    }

    private void sendToForgotPasswordActivity() {
        /*Send to Forgot password activity.*/
        startActivity(new Intent(this, ForgotPasswordActivity.class));
        finish();
    }

    private void triggerLogInButton() {
        /*Handler of Log in button.*/
        logInButton.setOnClickListener(v -> {
            logInButton.startAnimation();
            emailOrUsernameInserted = Objects.requireNonNull(enterEmailOrUserName.getText()).toString();
            passwordInserted = Objects.requireNonNull(password.getText()).toString();
            if (!emailOrUsernameInserted.isEmpty() && !passwordInserted.isEmpty()){
                FirebaseAuth userAuthorized = FirebaseAuth.getInstance();
                userAuthorized.signInWithEmailAndPassword(emailOrUsernameInserted, passwordInserted)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                logInButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, this::sendToMainActivity);
                                Toast.makeText(this, "Logged in.", Toast.LENGTH_SHORT).show();
                                sendToMainActivity();
                            }
                        });
            } else {
                logInButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                Toast.makeText(this, "Wrong credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendToMainActivity() {
        /*Send to Main activity.*/
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}