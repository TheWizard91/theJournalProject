package com.thewizard91.thejournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.sing_up_adds_on.CreateNewAccountActivity;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText enterEmail;
    private TextInputEditText enterPassword;
    private TextInputEditText confirmPassword;
    private TransitionButton signUpButton;
    private FirebaseAuth userAuthorized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        enterEmail = findViewById(R.id.sign_up_mail_address_text_view_id);
        enterPassword = findViewById(R.id.sign_up_password_text_view_id);
        confirmPassword = findViewById(R.id.sign_password_confirmation_id);
        signUpButton = findViewById(R.id.sign_up_button_id);
        userAuthorized = FirebaseAuth.getInstance();
        createANewAccount();
    }

    private void createANewAccount() {
        signUpButton.setOnClickListener(v -> {
            // Retrieve information inserted by the user in the Text boxes.
            _retrieveUserData();
            // Start the loading animation when the user tap the button.
            signUpButton.startAnimation();
            final Handler handler = new Handler();
            handler.postDelayed(() -> signUpButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                    this::sendToAccountSettingsActivity),2000);
        });
    }

    private void _retrieveUserData() {
        String emailInserted = Objects.requireNonNull(enterEmail.getText()).toString();
        String passwordInserted = Objects.requireNonNull(enterPassword.getText()).toString();
        String passwordConfirmed = Objects.requireNonNull(confirmPassword.getText()).toString();
        if(!TextUtils.isEmpty(emailInserted) && !TextUtils.isEmpty(passwordConfirmed) && !TextUtils.isEmpty(passwordConfirmed)){
            if(passwordInserted.equals(passwordConfirmed)){
                userAuthorized.createUserWithEmailAndPassword(emailInserted, passwordInserted)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Account has been made.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        "Error: "+ Objects.requireNonNull(task.getException())
                                                .getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(SignUpActivity.this, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendToAccountSettingsActivity() {
        Intent intent = new Intent(getBaseContext(),CreateNewAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}