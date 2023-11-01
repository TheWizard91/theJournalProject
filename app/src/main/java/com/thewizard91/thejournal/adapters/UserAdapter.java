package com.thewizard91.thejournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.user.UserModel;

import java.lang.reflect.Array;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private UserModel userProfileModel;

    public UserAdapter(UserModel userProfileModel) {
        this.userProfileModel = userProfileModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(-1,02));
        context = parent.getContext();
//        TODO: Add number of likes and stuff like that coming from the db.
//         That means, I need to change the db structure to accommodate it.
//         In a nutshell, need additional db info when it is readjusted.
        return new UserProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        UserProfileViewHolder userProfileModel = (UserProfileViewHolder) holder;

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private class UserProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView username_view;
        public TextView email_view;
        public ListView listOneView;
        public ListView listTwoView;
        private View innerView;
        private final String[] array_of_list_one = {"Number of Posts","Favorite","Location"};
        private final String[] array_of_list_two = {"Edit Profile","Logout"};

        public UserProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            innerView = itemView;
            ListView list_one = innerView.findViewById(R.id.fragment_account_listview_one);
            ListView list_two = innerView.findViewById(R.id.fragment_account_listview_two);
            ArrayAdapter<String> arr_one;
            arr_one = new ArrayAdapter<>(innerView.getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    array_of_list_one);
            list_one.setAdapter(arr_one);
            ArrayAdapter<String> arr_two;
            arr_two = new ArrayAdapter<>(innerView.getContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    array_of_list_two);
            list_two.setAdapter(arr_two);
            Fresco.initialize(context);
        }
    }
}
