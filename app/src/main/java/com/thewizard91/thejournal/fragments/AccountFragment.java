package com.thewizard91.thejournal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thewizard91.thejournal.R;

import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
//
public class AccountFragment extends Fragment {
//
//    public TextView aboutUser;
//    public Context accountContext;
//    private FirebaseUser currentUser;
//    private FirebaseAuth firebaseAuth;
//    private FirebaseFirestore firebaseFirestore;
//    private FirebaseStorage firebaseStorage;
//    private FloatingActionButton heartFloatingActionButton;
//    private ActionBar mainActivityActionBar;
//    private Toolbar mainActivityToolBar;
//    private FloatingActionButton messageFloatingActionButton;
//    private NavigationView navigationView;
//    private TextView recentPhotosAddedByTheUser;
//    private StorageReference storageReference;
//    public TextView userDescription;
//    public CircleImageView userProfileImage;
//    private String userProfileImageURI;
//    public TextView username;
//    private FloatingActionButton videoChatFloatingActionButton;
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_account, container, false);
//        this.accountContext = container.getContext();
//        this.userProfileImage = (CircleImageView) view.findViewById(R.id.account_fragment_user_image);
////        this.username = (TextView) view.findViewById(C2521R.C2524id.account_fragment_username);
////        this.recentPhotosAddedByTheUser = (TextView) view.findViewById(C2521R.C2524id.photo_added_counter_text_view_id);
////        this.messageFloatingActionButton = (FloatingActionButton) view.findViewById(C2521R.C2524id.chat_floating_button);
////        this.videoChatFloatingActionButton = (FloatingActionButton) view.findViewById(C2521R.C2524id.video_chat_floating_button);
////        this.heartFloatingActionButton = (FloatingActionButton) view.findViewById(C2521R.C2524id.heart_floating_button);
////        this.aboutUser = (TextView) view.findViewById(C2521R.C2524id.about_user_text);
////        this.userDescription = (TextView) view.findViewById(C2521R.C2524id.about_user_description_id);
//        this.firebaseFirestore = FirebaseFirestore.getInstance();
//        FirebaseAuth instance = FirebaseAuth.getInstance();
//        this.firebaseAuth = instance;
//        FirebaseUser currentUser2 = instance.getCurrentUser();
//        this.currentUser = currentUser2;
//        if (currentUser2 != null) {
//            String currentUserId = currentUser2.getUid();
//            this.storageReference = FirebaseStorage.getInstance().getReference();
//            setUserProfileImageAndUsername(currentUserId);
//            setMessageFloatingActionButtonFunction();
//            setVideoChatFloatingActionButton();
//            setHeartFloatingActionButton();
//            setAboutUser(currentUserId);
//            return view;
//        }
//        throw new AssertionError();
//    }
//
//    private void setAboutUser(String currentUserId) {
//        this.firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(Task<DocumentSnapshot> task) {
//                DocumentSnapshot userInformation = task.getResult();
//                AccountFragment.this.aboutUser.setText("About " + userInformation.getString("profile_name_of") + ":");
//                AccountFragment.this.userDescription.setText(userInformation.getString("user_description"));
//            }
//        });
//    }
//
//    private void setHeartFloatingActionButton() {
//        this.heartFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Toast.makeText(AccountFragment.this.accountContext, "HeartFragment", 0).show();
//            }
//        });
//    }
//
//    private void setVideoChatFloatingActionButton() {
//        this.videoChatFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Toast.makeText(AccountFragment.this.accountContext, "VideoCharFunction", 0).show();
//            }
//        });
//    }
//
//    private void setMessageFloatingActionButtonFunction() {
//        this.messageFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Toast.makeText(AccountFragment.this.accountContext, "MessageFragment", 0).show();
//            }
//        });
//    }
//
//    private void setUserProfileImageAndUsername(String currentUserId) {
//        this.firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(Task<DocumentSnapshot> task) {
//                Map<String, Object> map;
//                DocumentSnapshot documentSnapshot = task.getResult();
//                AccountFragment.this.username.setText(task.getResult().getString("profile_name_of"));
//                if (documentSnapshot.exists() && (map = documentSnapshot.getData()) != null) {
//                    for (Map.Entry<String, Object> entry : map.entrySet()) {
//                        if ("profile_image".equals(entry.getKey())) {
//                            String currentHolderImageURI = entry.getValue().toString();
//                            RequestOptions placeholderOption = new RequestOptions();
//                            placeholderOption.placeholder((int) R.drawable.ic_account_circle);
//                            Glide.with(AccountFragment.this.accountContext).applyDefaultRequestOptions(placeholderOption).load(currentHolderImageURI).into((ImageView) AccountFragment.this.userProfileImage);
//                            return;
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    public void onStart() {
//        super.onStart();
//    }
}