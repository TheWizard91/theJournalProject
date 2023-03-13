package com.thewizard91.thejournal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
//import com.thewizard91.thejournal.C2521R;
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
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.models.comments.CommentsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String blogPostId;
    private List<CommentsModel> commentsModelList;
    public Context context;
    private FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebaseFirestore;

    public CommentsAdapter(List<CommentsModel> commentsModelList, String blogPostId) {
        this.commentsModelList = commentsModelList;
        this.blogPostId = blogPostId;
    }

    public CommentsAdapter(List<CommentsModel> commentsModelList) {
        this.commentsModelList = commentsModelList;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_section, parent, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new CommentsViewHolder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean z = false;
        holder.setIsRecyclable(false);
        CommentsViewHolder commentsViewHolder = (CommentsViewHolder) holder;
        String currentUserId = (Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
        String currentUserIdAndCommentsId = commentsModelList.get(position).CommentsModelId;
        String commentUserId = commentsModelList.get(position).getUserId();
        String userProfileImageUri = commentsModelList.get(position).getUserProfileImageUri();
        String username = commentsModelList.get(position).getUsername();
        String time  = String.valueOf(commentsModelList.get(position).getTimestamp());
        Log.d("CmmUsername",username);
        Log.d("CmmUserProfileIm",userProfileImageUri);
        Log.d("commentTime",time);
//        setUserData(commentsViewHolder, commentUserId);
        if (commentUserId == null) {
            z = true;
        }

        Log.d("commentUserIdIs", String.valueOf(z));
        commentsViewHolder.setDate(time);
        refreshAdapter(commentsModelList);
        commentsViewHolder.setCommentPosted(commentsModelList.get(position).getCommentText());
        commentsViewHolder.setThumbsUp(commentsModelList.get(position).getThumbsUpImageUri());
        setLikesCount(commentsViewHolder, blogPostId, currentUserIdAndCommentsId);
        setLikes(commentsViewHolder, blogPostId, currentUserIdAndCommentsId, currentUserId);
        addAndDeleteLikes(commentsViewHolder, blogPostId, currentUserIdAndCommentsId, currentUserId);
//        deleteCommentsAndLikes(commentsViewHolder, currentUserIdAndCommentsId, currentUserId);
        replayComments(commentsViewHolder, blogPostId, currentUserIdAndCommentsId, currentUserId);
        commentsViewHolder.setUserProfileImageUri(userProfileImageUri);
        commentsViewHolder.setUsername(username);

    }

    private void replayComments(CommentsViewHolder commentsViewHolder, String blogPostId2, String commentsId, String currentUserId) {
    }

//    private void deleteCommentsAndLikes(CommentsViewHolder commentsViewHolder, final String currentUserIdAndCommentsId, final String currentUserId) {
//        commentsViewHolder.deleteButtonView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                deleteLikesOfSelectedComment(currentUserIdAndCommentsId, currentUserId);
//                deleteSelectedComments(currentUserIdAndCommentsId);
//                Toast.makeText(context, "Comment Deleted!", Toast.LENGTH_SHORT).show();
//            }
//        });
//        refreshAdapter(commentsModelList);
//    }

    private void refreshAdapter(List<CommentsModel> commentsModelList) {
        new CommentsAdapter(commentsModelList).notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void deleteLikesOfSelectedComment(String currentUserIdAndCommentsId, String currentUserId) {
        firebaseFirestore.collection("Posts").document(blogPostId).collection("Comments").document(currentUserIdAndCommentsId).collection("Likes").document(currentUserId).delete();
    }

    /* access modifiers changed from: private */
    public void deleteSelectedComments(String currentUserIdAndCommentsId) {
        firebaseFirestore.collection("Posts").document(blogPostId).collection("Comments").document(currentUserIdAndCommentsId).delete();
    }

    private void addAndDeleteLikes(CommentsViewHolder commentsViewHolder, final String blogPostId2, final String currentUserIdAndCommentsId, final String currentUserId) {
        commentsViewHolder.thumbsUpView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firebaseFirestore.collection("Posts").document(blogPostId2).collection("Comments").document(currentUserIdAndCommentsId).collection("Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> commentsLikes = new HashMap<>();
                            commentsLikes.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts").document(blogPostId2).collection("Comments").document(currentUserIdAndCommentsId).collection("Likes").document(currentUserId).set(commentsLikes);
                            Toast.makeText(CommentsAdapter.this.context, "Added a like to ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CommentsAdapter.this.firebaseFirestore.collection("Posts").document(blogPostId2).collection("Comments").document(currentUserIdAndCommentsId).collection("Likes").document(currentUserId).delete();
                        Toast.makeText(CommentsAdapter.this.context, "Removed a like to ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setLikes(final CommentsViewHolder commentsViewHolder, String blogPostId2, String currentUserIdAndCommentsId, String currentUserId) {
        firebaseFirestore.collection("Posts").document(blogPostId2).collection("Comments").document(currentUserIdAndCommentsId).collection("Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    throw new AssertionError();
                } else if (documentSnapshot.exists()) {
                    commentsViewHolder.thumbsUpView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                } else {
                    commentsViewHolder.thumbsUpView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                }
            }
        });
    }

    private void setLikesCount(final CommentsViewHolder commentsViewHolder, String blogPostId2, String currentUserIdAndCommentsId) {
        this.firebaseFirestore.collection("Posts").document(blogPostId2).collection("Comments").document(currentUserIdAndCommentsId).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots == null) {
                    throw new AssertionError();
                } else if (!queryDocumentSnapshots.isEmpty()) {
                    commentsViewHolder.updateLikesCounts(queryDocumentSnapshots.size());
                }
            }
        });
    }

//    private void setUserData(final CommentsViewHolder commentsViewHolder, String commentUserId) {
//        this.firebaseFirestore.collection("Users").document(commentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    commentsViewHolder.setUserImageUriAndUsername(task.getResult().getString("profile_name_of"), task.getResult().getString("profile_image"));
//                    return;
//                }
//                commentsViewHolder.updateLikesCounts(0);
//            }
//        });
//    }

    public int getItemCount() {
        return this.commentsModelList.size();
    }

    private class CommentsViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView deleteButtonView;
        public ImageView thumbsUpView;
        private View view;

        public CommentsViewHolder(View v) {
            super(v);
            view = v;
            Log.d("in_omments_v_is:",view.toString());
            thumbsUpView = (ImageView) view.findViewById(R.id.thumb_up);
//            this.deleteButtonView = ((CircleImageView) this.view.findViewById(R.id.comments_clear));
        }

        public void setCommentPosted(String commentPosted) {
            ((TextView) view.findViewById(R.id.comment)).setText(commentPosted);
        }

        public void setUserImageUriAndUsername(String username, String userImageUri) {
            ((TextView) view.findViewById(R.id.comments_username)).setText("Posted By: " + username);
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

        public void setThumbsUp(String thumpsUpImageURI) {
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_favorite_full);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption)
                    .load(thumpsUpImageURI)
                    .into((ImageView) view.findViewById(R.id.thumb_up));
        }

        public void updateLikesCounts(int count) {
            ((TextView) view.findViewById(R.id.thumb_up_like_count)).setText(count + "Likes");
        }

        public void setUserProfileImageUri(String userProfileImageUri) {
            RequestOptions placeholderOptions = new RequestOptions();
            Log.d("inCVUserProfileIm",userProfileImageUri);
            placeholderOptions.placeholder(R.drawable.ic_account_circle);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOptions)
                    .load(userProfileImageUri)
                    .into((CircleImageView) view.findViewById(R.id.comments_user_image));
        }

        public void setUsername(String username) {
            Log.d("inCVUsername",username);
            ((TextView) view.findViewById(R.id.comments_username)).setText("Posted By: " + username);
        }
    }
}