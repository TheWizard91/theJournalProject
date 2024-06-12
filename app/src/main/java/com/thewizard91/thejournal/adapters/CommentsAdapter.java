package com.thewizard91.thejournal.adapters;

import static java.lang.String.format;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.florent37.materialtextfield.MaterialTextField;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thewizard91.thejournal.R;
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
    public boolean editButtonStatus = false;
    public String editedText;

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
        holder.setIsRecyclable(false);
        CommentsViewHolder commentViewHolder = (CommentsViewHolder) holder;
        String currentUserId = (Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
        String commentId = listOfComments.get(position).CommentsModelId; // getting commentId:currentUserId;
        String commentUserId = listOfComments.get(position).getUserId();
        String userProfileImageURI = listOfComments.get(position).getUserProfileImageUri();
        String username = listOfComments.get(position).getUsername();
        String time = valueOf(listOfComments.get(position).getTimestamp());

        // Class methods
        setLikesCount(commentViewHolder, postId, commentId);
        setLikesVisibility(commentViewHolder, postId, commentId, currentUserId);
        addAndDeleteLikes(commentViewHolder, postId, commentId, currentUserId);
        deleteComment(commentViewHolder, commentId, currentUserId);
        replayComments(commentViewHolder, postId, commentId, currentUserId);
        editButtonIsPressed(editButtonStatus, b -> editCommentHandler(commentViewHolder, postId, commentId, currentUserId));
        deleteButtonVisibility(commentViewHolder,currentUserId, commentUserId);

        uploadEditedCommentOnBD(commentViewHolder, postId, commentId);

        replyButton(commentViewHolder, postId, commentId);

        // Holders: set initial values.
        commentViewHolder.setDate(time);
        commentViewHolder.setComment(listOfComments.get(position).getCommentText());
        commentViewHolder.setUserProfileImageUri(userProfileImageURI);
        commentViewHolder.setUsername(username);

    }

    private void deleteButtonVisibility(CommentsViewHolder commentViewHolder, String currentUserId, String commentUserId) {
        /**/

        if (currentUserId.equals(commentUserId)) {
            commentViewHolder.setVisibilityForDeleteButton(View.VISIBLE);
        }
    }

    private void replyButton(CommentsViewHolder commentViewHolder, String postId, String commentId) {
        /**/

        commentViewHolder.replayButton.setOnClickListener(view -> Log.d("reply","is clicked"));
    }

    private void uploadEditedCommentOnBD(CommentsViewHolder commentViewHolder, String postId, String commentId) {
        /*Edit comment of selected comment.
        * pre: commentViewHolder is the view of the current comment.
        *      commentId is the id of the current comment (commentId:currentUserId).
        *      currentUserId is the id of the user of the comment.
        * post: comment on db is modified as well as in the screen. */

        commentViewHolder.uploadButton.setOnClickListener(view -> {
            // Get info from edit text.
            editedText = String.valueOf(commentViewHolder.editText.getText());
            Toast.makeText(view.getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            // Now change the textView with the new text.
            firebasefirestore.collection("Posts")
                    .document(postId)
                    .collection("Comments")
                    .document(commentId)
                    .update("commentText", editedText);
            // Display edited text.
            commentViewHolder.setComment(editedText);
            // Make the section to edit comments be disparaged.
            commentViewHolder.setVisibilityForTheRightLikeButton(View.GONE);

            // update the list of comments so that when I change the text int the edited comment,
            // the changes are reflected in the view even if we scroll down or delete comments;
            // thus, changing the view. This is a critical part of the project because when I
            // scrolled down or deleted comments after editing them, I had the old values stills up.
            // Unless I somehow refresh the view via the back button. But, changing the comments text
            // in the list of comments, allowed me correct that.
            for (CommentsModel commentsModel : listOfComments) {
                if (commentId.equals(commentsModel.CommentsModelId)) {
                    commentsModel.setCommentText(editedText);
                    listOfComments.set(listOfComments.indexOf(commentsModel),commentsModel);
                }
            }
        });
    }

    private void editButtonIsPressed(boolean isPressed, callback myCallback) { myCallback.onCallback(isPressed); }

    protected interface callback {
        void onCallback(boolean b);
    }

    private void editCommentHandler(CommentsViewHolder commentViewHolder, String postId, String commentId, String currentUserId) {
        /*The comment section that lets user edit comments is visible if pressed (# of presses
         * odd. Gone otherwise.
         * pre: commentViewHolder is the view of the current comment.
         *      commentId is the id of the current comment (commentId:currentUserId).
         *      currentUserId is the id of the user of the comment.
         * post: comment section for editing is now visible or gone depending on the number of clicks. */

        final int[] clicks = {0};
        commentViewHolder.editButton.setOnClickListener(view -> firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    clicks[0]++;
                    if ((clicks[0] % 2) == 1) { // if clicked once open the layout
                        commentViewHolder.setVisibilityForTheRightLikeButton(View.VISIBLE);
                        editButtonStatus = true;
                    } else {
                        commentViewHolder.setVisibilityForTheRightLikeButton(View.GONE);
                        editButtonStatus = false;
                    }
                }));
    }

    private void replayComments(CommentsViewHolder commentViewHolder, String postId, String commentsId, String currentUserId) {
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteComment(CommentsViewHolder commentViewHolder, final String commentId, final String currentUserId) {
        /*Delete selected and its likes from db.
        * pre: commentViewHolder is the view of the current comment.
        *      commentId is the id of the current comment (commentId:currentUserId).
        *      currentUserId is the id of the user of the comment.
        * post: current comment and its likes are removed from db.*/

        commentViewHolder.deleteButtonView.setOnClickListener(view -> {
            // Remove the selected comments and the likes in them from firebase.
            for (int i = 0; i < listOfComments.size(); i++) {
                // get id of the comment as you iterate, the .CommentsModel provides me with the id of the comment,
                // otherwise, I will receive a google query object such as come.google..... wih the listOfComments.get(i)
                String chosenComment = listOfComments.get(i).CommentsModelId;
                if (chosenComment.equals(commentId)) {
                    // update adapter list.
                    deleteLikesOfSelectedComment(commentId, currentUserId);
                    deleteSelectedComments(commentId);
                    listOfComments.remove(i);
                    Toast.makeText(context, "Comment Deleted!", Toast.LENGTH_SHORT).show();
                    this.notifyDataSetChanged();
                }
            }
        });
    }

    public void deleteLikesOfSelectedComment(String commentId, String currentUserId) {
        /*pre: commentId is id of current comment and currentUserId is id of this.user.
         * post: comment was in db. Now, no more.*/

        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection("Likes")
                .document(currentUserId)
                .delete();
    }

    public void deleteSelectedComments(String commentId) {
        /*pre: commentId is id of current comment.
        * post: comment was in db. Now, no more.*/

        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .delete();
    }

    private void addAndDeleteLikes(CommentsViewHolder commentViewHolder, final String postId, final String commentId, final String currentUserId) {
        /*Add and remove likes as the user presses the like(heart figure) button.
        * pre: commentViewHolder is the view of the current comment.
        *      postId (from firebase) is the id of the post at which this.comment resides.
        *      commentId is the id of the current comment (commentId:currentUserId).
        *      currentUserId is the id of the user of the comment.
        * post: at user press, add 1 like in db if nOfPress%2==1.
        *       else remove it. */

        commentViewHolder.likeButton.setOnClickListener(view -> firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection("Likes")
                .document(currentUserId)
                .get().addOnCompleteListener(task -> {
                    if (!task.getResult().exists()) {
                        addLikes(commentId, currentUserId);
                    } else {
                        removeLikes(postId, commentId, currentUserId);
                    }
                }));
    }

    private void removeLikes(String postId, String commentId, String currentUserId) {
        /*Remove likes from database.
        * pre: (postId, commentId, currentUserId) != null.
        * post: Firebase database updated (a removed has been added to it).*/

        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection("Likes")
                .document(currentUserId)
                .delete();
        Toast.makeText(context, "Removed a like to ", Toast.LENGTH_SHORT).show();
    }

    private void addLikes(String commentId, String currentUserId) {
        /*Add likes from database.
         * pre: (commentId, currentUserId) != null.
         * post: Firebase database updated (a add has been added to it).*/

        Map<String, Object> commentsLikes = new HashMap<>();
        commentsLikes.put("timestamp", FieldValue.serverTimestamp());
        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection("Likes")
                .document(currentUserId)
                .set(commentsLikes);
        Toast.makeText(context, "Added a like to ", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setLikesVisibility(final CommentsViewHolder commentViewHolder, String postId, String commentId, String currentUserId) {
        /*Set likes in the adapter. Meaning when you press the like button, you'll see the heart turning from black to white.
         * pre: commentViewHolder is the view of the current comment.
         *      postId (from firebase) is the id of the post at which this.comment resides.
         *      commentId is the id of the current comment (commentId:currentUserId).
         *      currentUserId is the id of the user of the comment.
         * post: if user presses the likes button, it turns red. White otherwise. */

        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection("Likes")
                .document(currentUserId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot == null) {
                        throw new AssertionError();
                    } else if (documentSnapshot.exists()) {
                        commentViewHolder.likeButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_animated));
                    } else {
                        commentViewHolder.likeButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                    }
                });
    }

    private void setLikesCount(final CommentsViewHolder commentViewHolder, String postId, String commentId) {
        /*Gets number od likes of current post and displays it using updateLikesCounts().
        * pre: (commentViewHolder, postId, commentId) != null.
        * post: sets the number of likes in x post so updateLikesCounts() can display it.*/

        firebasefirestore.collection("Posts")
                .document(postId)
                .collection("Comments")
                .document(commentId)
                .collection("Likes")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) {
                        commentViewHolder.updateLikesCounts(queryDocumentSnapshots.size());
                    } else {
                        commentViewHolder.updateLikesCounts(0);
                    }
                });
    }

    public int getItemCount() {
        return this.listOfComments.size();
    }

    private class CommentsViewHolder extends RecyclerView.ViewHolder {
        /*Initial values of each comment object is being processed.*/

        private final CircleImageView deleteButtonView;
        private final ImageView likeButton;
        private final ImageView editButton;
        private final ImageView replayButton;
        private final ImageButton uploadButton;
        private final View editCommentLayout;
        private MaterialTextField expandableViewForTextView;
        private final EditText editText;
        private TextView commentText;
//        public MaterialCheckBox thumbsUpView;
        private final View view;

        public CommentsViewHolder(View v) {
            super(v);
            view = v;
            deleteButtonView = view.findViewById(R.id.comments_clear);
            likeButton = view.findViewById(R.id.thumb_up);//R.id.animatedLikeBtn
            editButton = view.findViewById(R.id.comment_edit);
            replayButton = view.findViewById(R.id.comment_reply);
            uploadButton = view.findViewById(R.id.upload_button);
            editCommentLayout = view.findViewById(R.id.edit_comment_layout);
            expandableViewForTextView = view.findViewById(R.id.material_text_field);
            editText = view.findViewById(R.id.edit_comment);
            commentText = view.findViewById(R.id.comment);
            Fresco.initialize(context);
        }

        private void setVisibilityForDeleteButton (int visibility) {
            /**/

            deleteButtonView.setVisibility(visibility);
        }

        public void setVisibilityForTheRightLikeButton(int visibility) {
            /*pre: visibility == 0 (not visible), visibility == 1 (visible), visibility == 8 (gone).
            * post: like button is either red or transparent depending the number of click this user has
            *       been engaged in.*/

            editCommentLayout.setVisibility(visibility);
        }

        public void setComment(String commentText) {
            /*pre: commentText is comment content.
            * post: text is places on adapter's view. */
            ((TextView) view.findViewById(R.id.comment)).setText(commentText);
        }

        public void setDate(String date) {
            ((TextView) view.findViewById(R.id.comments_date)).setText(date);
        }

        @SuppressLint("DefaultLocale")
        public void updateLikesCounts(int count) {//R.id.animatedLikeBtn
            String strCount = String.valueOf(count);
            ((TextView) view.findViewById(R.id.thumb_up_like_count)).setText(strCount);
        }

        @SuppressLint("CheckResult")
        public void setUserProfileImageUri(String userProfileImageURI) {
            RequestOptions placeholderOptions = new RequestOptions();
            placeholderOptions.placeholder(R.drawable.ic_account_circle);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOptions)
                    .load(userProfileImageURI)
                    .into((CircleImageView) view.findViewById(R.id.comments_user_image));
        }

        @SuppressLint("SetTextI18n")
        public void setUsername(String username) {
            ((TextView) view.findViewById(R.id.comments_username)).setText("Posted By: " + username);
        }
    }
}