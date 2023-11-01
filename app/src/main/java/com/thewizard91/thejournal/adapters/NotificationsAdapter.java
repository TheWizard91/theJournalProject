package com.thewizard91.thejournal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thewizard91.thejournal.R;

import com.thewizard91.thejournal.models.notifications.NotificationsModel;

import java.util.List;
import java.util.Objects;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public List<NotificationsModel> listOfNotifications;
    public Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    public NotificationsAdapter(List<NotificationsModel> notificationsList) {
        this.listOfNotifications = notificationsList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications,parent,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(-1,-2));
        this.context = parent.getContext();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        return new ViewHolderOfNotifications(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        ViewHolderOfNotifications holderOfNotifications = (ViewHolderOfNotifications) holder;
        String notificationId = listOfNotifications.get(position).NotificationId;
        String currentUserId = ((FirebaseUser) Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
        String notificationUserId = listOfNotifications.get(position).getUserId();
        String profileImageURI = listOfNotifications.get(position).getUserProfileImageURI();
        String username = listOfNotifications.get(position).getUsername();
        String time = String.valueOf(listOfNotifications.get(position).getDate());
        String notificationText = listOfNotifications.get(position).getNotificationText();

//        Log.d("notificationId",notificationId);
//        Log.d("currentUserId",currentUserId);
//        Log.d("notificationUserId",notificationUserId);
//        Log.d("profileImageURI",profileImageURI);
//        Log.d("username",username);

        if(listOfNotifications.get(position).getDate() !=null && !listOfNotifications.isEmpty()) {
            holderOfNotifications.setTime(time);
        } else {
            holderOfNotifications.setTime("set time");
        }

        holderOfNotifications.setUserProfileImage(profileImageURI);
        holderOfNotifications.setNotification(notificationText);
//        holderOfNotifications.sete
    }
    @Override
    public int getItemCount() {
        return this.listOfNotifications.size();
    }
    public class ViewHolderOfNotifications extends RecyclerView.ViewHolder {
        public ImageView userProfileImage;
        public TextView username;
        public TextView date;
        public TextView notification;
        private View view;
        public ViewHolderOfNotifications(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            userProfileImage = view.findViewById(R.id.notificationsCircleImageView);
            date = view.findViewById(R.id.notificationsDate);
            notification = view.findViewById(R.id.notificationsTextView);
        }
        public void setUserProfileImage(String user_profile_image_uri) {
            Log.d("notAdapter",user_profile_image_uri);
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.image_placeholder);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption)
                    .load(user_profile_image_uri)
                    .into((ImageView) view.findViewById(R.id.notificationsCircleImageView));
        }
        public void setTime(String time) {
            Log.d("notAdapter",time);
            ((TextView) view.findViewById(R.id.notificationsDate)).setText(time);
        }
        public void setNotification(String notificationText) {
            Log.d("notAdapter",notificationText);
            ((TextView) view.findViewById(R.id.notificationsTextView)).setText(notificationText);
        }
    }
}
