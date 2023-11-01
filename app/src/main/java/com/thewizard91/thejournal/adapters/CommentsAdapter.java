package com.thewizard91.thejournal.adapters;

import static java.lang.String.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.thewizard91.thejournal.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.thewizard91.thejournal.models.comments.CommentsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String postId;
    private final List<CommentsModel> listOfComments;
    public Context context;
    private FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebasefirestore;

    public CommentsAdapter(List<CommentsModel> listOfComments, String postId) {
        this.listOfComments = listOfComments;
        this.postId = postId;
    }

    public CommentsAdapter(List<CommentsModel> listOfComments) {
        this.listOfComments = listOfComments;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_section, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        context = parent.getContext();
        firebasefirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new CommentsViewHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean z = false;
        holder.setIsRecyclable(false);
        CommentsViewHolder comments_view_holder = (CommentsViewHolder) holder;
        String current_user_id = (Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
        String current_user_id_and_comment_id = listOfComments.get(position).CommentsModelId;
        String comment_user_id = listOfComments.get(position).getUserId();
        String user_profile_image_uri = listOfComments.get(position).getUserProfileImageUri();
        String username = listOfComments.get(position).getUsername();
        String time = valueOf(listOfComments.get(position).getTimestamp());
        setUserData(comments_view_holder, comment_user_id);

        comments_view_holder.setDate(time);
        refreshAdapter(listOfComments);
        comments_view_holder.setCommentPosted(listOfComments.get(position).getCommentText());
        comments_view_holder.setThumbsUp(listOfComments.get(position).getThumbsUpImageUri());
        setLikesCount(comments_view_holder, postId, current_user_id_and_comment_id);
        setLikes(comments_view_holder, postId, current_user_id_and_comment_id, current_user_id);
        addAndDeleteLikes(comments_view_holder, postId, current_user_id_and_comment_id, current_user_id);
//        deleteCommentsAndLikes(comments_view_holder, current_user_id_and_comment_id, current_user_id);
        replayComments(comments_view_holder, postId, current_user_id_and_comment_id, current_user_id);
        comments_view_holder.setUserProfileImageUri(user_profile_image_uri);
        comments_view_holder.setUsername(username);

    }

    private void replayComments(CommentsViewHolder comments_view_holder, String blogPostId2, String commentsId, String current_user_id) {
    }

//    private void deleteCommentsAndLikes(CommentsViewHolder comments_view_holder, final String current_user_id_and_comment_id, final String current_user_id) {
//        comments_view_holder.deleteButtonView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                deleteLikesOfSelectedComment(current_user_id_and_comment_id, current_user_id);
//                deleteSelectedComments(current_user_id_and_comment_id);
//                Toast.makeText(context, "Comment Deleted!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        refreshAdapter(listOfComments);
//    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshAdapter(List<CommentsModel> listOfComments) {
        new CommentsAdapter(listOfComments).notifyDataSetChanged();
    }

    public void deleteLikesOfSelectedComment(String current_user_id_and_comment_id, String current_user_id) {
        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(current_user_id_and_comment_id)
                .collection("Likes")
                .document(current_user_id)
                .delete();
    }

    public void deleteSelectedComments(String current_user_id_and_comment_id) {
        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(current_user_id_and_comment_id)
                .delete();
    }

    private void addAndDeleteLikes(CommentsViewHolder comments_view_holder, final String blogPostId2, final String current_user_id_and_comment_id, final String current_user_id) {
        comments_view_holder.thumbsUpView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firebasefirestore.collection("Posts")
                        .document(blogPostId2)
                        .collection("Comments")
                        .document(current_user_id_and_comment_id)
                        .collection("Likes")
                        .document(current_user_id)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> commentsLikes = new HashMap<>();
                            commentsLikes.put("timestamp", FieldValue.serverTimestamp());
                            firebasefirestore.collection("Posts")
                                    .document(blogPostId2)
                                    .collection("Comments")
                                    .document(current_user_id_and_comment_id)
                                    .collection("Likes")
                                    .document(current_user_id)
                                    .set(commentsLikes);
                            Toast.makeText(context, "Added a like to ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firebasefirestore.collection("Posts").document(blogPostId2).collection("Comments").document(current_user_id_and_comment_id).collection("Likes").document(current_user_id).delete();
                        Toast.makeText(context, "Removed a like to ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setLikes(final CommentsViewHolder comments_view_holder, String blogPostId2, String current_user_id_and_comment_id, String current_user_id) {
        firebasefirestore.collection("Posts")
                .document(blogPostId2)
                .collection("Comments")
                .document(current_user_id_and_comment_id)
                .collection("Likes")
                .document(current_user_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    throw new AssertionError();
                } else if (documentSnapshot.exists()) {
                    comments_view_holder.thumbsUpView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                } else {
                    comments_view_holder.thumbsUpView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                }
            }
        });
    }

    private void setLikesCount(final CommentsViewHolder comments_view_holder, String blogPostId2, String current_user_id_and_comment_id) {
        firebasefirestore.collection("Posts")
                .document(blogPostId2)
                .collection("Comments")
                .document(current_user_id_and_comment_id)
                .collection("Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots == null) {
                    throw new AssertionError();
                } else if (!queryDocumentSnapshots.isEmpty()) {
                    comments_view_holder.updateLikesCounts(queryDocumentSnapshots.size());
                }
            }
        });
    }

    private void setUserData(final CommentsViewHolder comments_view_holder, String comment_user_id) {
        firebasefirestore.collection("Users")
                .document(comment_user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    comments_view_holder.setUserImageUriAndUsername(task.getResult().getString("username"), task.getResult().getString("userProfileImageURI"));
                    return;
                }
                comments_view_holder.updateLikesCounts(0);
            }
        });
    }

    public int getItemCount() {
        return this.listOfComments.size();
    }

    private class CommentsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView deleteButtonView;
        public ImageView thumbsUpView;
        private final View view;

        public CommentsViewHolder(View v) {
            super(v);
            view = v;
            thumbsUpView = (ImageView) view.findViewById(R.id.thumb_up);
//            this.deleteButtonView = ((CircleImageView) this.view.findViewById(R.id.comments_clear));
        }

        public void setCommentPosted(String commentPosted) {
            ((TextView) view.findViewById(R.id.comment)).setText(commentPosted);
        }

        @SuppressLint("CheckResult")
        public void setUserImageUriAndUsername(String username, String userImageUri) {
            ((TextView) view.findViewById(R.id.comments_username)).setText(format("Posted By: %s", username));
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_account_circle);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption).
                    load(userImageUri)
                    .into((ImageView) (CircleImageView) view.findViewById(R.id.comments_user_image));
        }

        public void setDate(String date) {
            Log.d("dateIs:",date);
            ((TextView) view.findViewById(R.id.comments_date)).setText(date);
        }

        @SuppressLint("CheckResult")
        public void setThumbsUp(String thumpsUpImageURI) {
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_favorite_full);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption)
                    .load(thumpsUpImageURI)
                    .into((ImageView) view.findViewById(R.id.thumb_up));
        }

        @SuppressLint("DefaultLocale")
        public void updateLikesCounts(int count) {
            ((TextView) view.findViewById(R.id.thumb_up_like_count)).setText(format("%dLikes", count));
        }

        @SuppressLint("CheckResult")
        public void setUserProfileImageUri(String user_profile_image_uri) {
            RequestOptions placeholderOptions = new RequestOptions();
//            Log.d("inCVUserProfileIm",user_profile_image_uri);
            placeholderOptions.placeholder(R.drawable.ic_account_circle);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOptions)
                    .load(user_profile_image_uri)
                    .into((CircleImageView) view.findViewById(R.id.comments_user_image));
        }

        @SuppressLint("SetTextI18n")
        public void setUsername(String username) {
//            Log.d("inCVUsername",username);
            ((TextView) view.findViewById(R.id.comments_username)).setText("Posted By: " + username);
        }
    }
}