package com.thewizard91.thejournal.activities.log_in_activities_adds_on;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.LogInActivity;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText email;
    private TextInputEditText oldPassword;
    private TextInputEditText newPassword;
    private TransitionButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        sendButtonHandler();
    }

    private void sendButtonHandler() {
        /*Update password in database.*/

        sendButton.setOnClickListener(view -> {
            sendButton.startAnimation();
            String emailString = Objects.requireNonNull(email.getText()).toString();
            String oldPasswordString = Objects.requireNonNull(oldPassword.getText()).toString();
            String newPasswordString = Objects.requireNonNull(newPassword.getText()).toString();

            if (!emailString.isEmpty() && !oldPasswordString.isEmpty() && !newPasswordString.isEmpty()) {
                FirebaseAuth userAuthentication = FirebaseAuth.getInstance();
                userAuthentication.signInWithEmailAndPassword(emailString, oldPasswordString)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = userAuthentication.getCurrentUser();
                                assert currentUser != null;
                                if (emailString.equals(Objects.requireNonNull(currentUser.getEmail()))) {
                                    currentUser.updatePassword(newPasswordString);
                                    sendButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, this::sendToLoginActivity);
                                    showToastMessage("Password Updated.");
                                    userAuthentication.signOut();
                                    sendToLoginActivity();
                                } else {
                                    sendButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, null);
                                    showToastMessage("Wrong email.");
                                }
                            }
                        });
            } else {
                showToastMessage("Wrong email or password.");
            }
        });
    }

    private void showToastMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    private void sendToLoginActivity() {
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    private void init() {
        /*Initialize class variable members.*/

        email = findViewById(R.id.email_text_view_id);
        oldPassword = findViewById(R.id.old_password_text_view_id);
        newPassword = findViewById(R.id.new_password_text_view_id);
        sendButton = findViewById(R.id.send_transition_button_id);
    }
}