package com.thewizard91.thejournal.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.activities.sing_up_adds_on.AccountSettingsActivity;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText confirmPassword;
    private TextView haveAnAccountAlready;
    private TransitionButton signUpButton;
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
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // Retrieve information inserted by the user in the Text boxes.
                _retrieveUserData();
                // Start the loading animation when the user tap the button.
                signUpButton.startAnimation();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSuccessful = true;
                        if(isSuccessful) {
                            signUpButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                    new TransitionButton.OnAnimationStopEndListener() {
                                        @Override
                                        public void onAnimationStopEnd() {
                                            sendToAccountSettingsActivity();
                                        }
                                    });
                        } else {
                            signUpButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE,null);
                        }
                    }
                },2000);
            }
        });
    }

    private void _retrieveUserData() {
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
                                    Toast.makeText(SignUpActivity.this, "Account has been made.", Toast.LENGTH_SHORT).show();
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

    private void sendToAccountSettingsActivity() {
//        startActivity(new Intent(this, AccountSettingsActivity.class));
//        finish();
        Intent intent = new Intent(getBaseContext(),AccountSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
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