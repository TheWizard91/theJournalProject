package com.thewizard91.thejournal.adapters;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.fragments.HomeFragment;
import com.thewizard91.thejournal.models.post.PostModel;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<PostModel> listOfPosts;
    public Context context;
    private FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebasefirestore;
    private StorageReference storageReference;
    public interface CallBackMethodHelperForSetCurrentHolderImageURICallback {
        void onCallback(String str);
    }

    public PostAdapter(List<PostModel> blogPostImageModels) {
        listOfPosts = blogPostImageModels;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_row, parent, false);
        myView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        context = parent.getContext();
        firebasefirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        return new ViewHolderOfPost(myView);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ViewHolderOfPost holderOfPost = (ViewHolderOfPost) holder;
        String postId = listOfPosts.get(position).PostId;
        Log.d("pID",postId);
        String currentUserId = ((FirebaseUser) Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
        String posts_user_id = listOfPosts.get(position).getUserId();
        String time = String.valueOf(listOfPosts.get(position).getTimestamp());
        String image_uri = listOfPosts.get(position).getImageURI();
        String thumbnailURI = listOfPosts.get(position).getThumbnailURI();
        String description = listOfPosts.get(position).getDescription();
        String username = listOfPosts.get(position).getUsername();
        String userProfileImageURI = listOfPosts.get(position).getUserProfileImageURI();

        // Holder's methods.
        holderOfPost.setUserProfileImageURI(userProfileImageURI);
        holderOfPost.setTime(time);
        holderOfPost.setUsername(username);
        holderOfPost.setDescription(description);
        holderOfPost.setImageURI(image_uri);
        holderOfPost.setUserProfileImageURI(userProfileImageURI);
//        setUsernameAndImage(holderOfPost, posts_user_id);
//        holderOfBlogPost.setImageURIAndThumbnailURI(imageURI, thumbnailURI);
//        if (listOfPosts.get(position).getTimestamp() != null && !listOfPosts.isEmpty()) {
//            holderOfPost.setTime(time);//listOfPosts.get(position).getTimestamp(), String.valueOf(DateFormat.format("MM/dd/yyyy", new Date(listOfPosts.get(position).getTimestamp().getTime())))
//        } else {
//            holderOfPost.setTime("set time");
//        }

        // Class' methods.
        setLikesCount(holderOfPost, postId);
        setLikesVisibility(holderOfPost, postId, currentUserId);
        setOrRemoveLikesInDatabase(holderOfPost, postId, currentUserId, description);
        clickOnCommentsImage(holderOfPost, postId, currentUserId);
//        clickOnLocationImage(holderOfBlogPost, blogPostId);
//        setLocation(holderOfBlogPost, blogPostId);
//        clickOnDeletePostImage(holderOfBlogPost, blogPostId);
        setCommentsCount(holderOfPost, postId);
        holderOfPost.zoom(position);
    }

//    private void setLocation(final ViewHolderOfBlogPost holderOfBlogPost, String blogPostId) {
//        this.firebasefirestore.collection("Posts").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(Task<DocumentSnapshot> task) {
//                Map<String, Object> map;
//                DocumentSnapshot documentSnapshot = task.getResult();
//                if (documentSnapshot.exists() && (map = documentSnapshot.getData()) != null) {
//                    for (Map.Entry<String, Object> entry : map.entrySet()) {
//                        if (FirebaseAnalytics.Param.LOCATION.equals(entry.getKey())) {
//                            String location = entry.getValue().toString();
//                            holderOfBlogPost.setLocationInViewer("Click To Open Maps");
//                            holderOfBlogPost.sendUserToMapFragment(location);
//                        }
//                    }
//                }
//            }
//        });
//    }

//    private void clickOnLocationImage(ViewHolderOfBlogPost holderOfBlogPost, final String blogPostId) {
//        holderOfBlogPost.location_button_view.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                firebasefirestore.collection("Post").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    public void onComplete(Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(context, "This Post Does Not Contain A Location, Sorry.", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(context, "This image has no location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }

    private void setCommentsCount(final ViewHolderOfPost holderOfBlogPost, String blogPostId) {
        firebasefirestore.collection("Posts")
                .document(blogPostId)
                .collection("Comments")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) {
                        holderOfBlogPost.updateCommentsCount(queryDocumentSnapshots.size());
                    } else {
                        holderOfBlogPost.updateCommentsCount(0);
                    }
                });
    }

//    private void clickOnDeletePostImage(ViewHolderOfBlogPost holderOfBlogPost, final String blogPostId) {
//        holderOfBlogPost.delete_post_view.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                setCurrentHolderImageURI(blogPostId, new CallBackMethodHelperForSetCurrentHolderImageURICallback() {
//                    public void onCallback(String postImageURI) {
//                        deleteImageFromFirebaseStorageForCommonPosts(postImageURI);
//                        deleteImageFromFirebaseStoreForUserAccessOnly(postImageURI);
//                        deleteImageFromFirebaseFirestone(blogPostId);
//                    }
//                });
//                PostAdapter blogPostAdapter = PostAdapter.this;
//                blogPostAdapter.refreshAdapter(listOfPosts);
//                Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void deleteImageFromFirebaseFirestone(String postId) {
        firebasefirestore.collection("Posts").document(postId).delete();
    }

    public void deleteImageFromFirebaseStoreForUserAccessOnly(String postImageURI) {
        storageReference.child("storage_of: t/").child("post_images").child(postImageURI).delete();
    }

    /* access modifiers changed from: private */
    public void deleteImageFromFirebaseStorageForCommonPosts(String postImageURI) {
        storageReference.child("post_images_for_everyone_to_see/").child(postImageURI).delete();
    }

    public void setCurrentHolderImageURI(String blogPostId, final CallBackMethodHelperForSetCurrentHolderImageURICallback callBackMethodHelperForSetCurrentHolderImageURICallback) {
        firebasefirestore.collection("Posts")
                .document(blogPostId)
                .get()
                .addOnCompleteListener(task -> {
                    Map<String, Object> map;
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists() && (map = documentSnapshot.getData()) != null) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if ("image_uri".equals(entry.getKey())) {
                                String currentHolderImageURI = entry.getValue().toString();
                                String imageIdJPG = currentHolderImageURI.substring(currentHolderImageURI.indexOf("%2Fpost_images%2F"));
                                callBackMethodHelperForSetCurrentHolderImageURICallback.onCallback(imageIdJPG.substring(17, imageIdJPG.indexOf("?")));
                            }
                        }
                    }
                });
    }

//    public void setCurrentHolderProfileImageURI (String blogPID, final CallBackMethodHelperForSetCurrentHolderImageURICallback callBackMethodHelperForSetCurrentHolderImageURICallback) {
//        firebasefirestore.collection("Post")
//                .document()
//    }

    /* access modifiers changed from: private */
//    public void refreshAdapter(List<PostModel> listOfPosts2) {
//        new PostAdapter(listOfPosts2).notifyDataSetChanged();
//    }

    private void clickOnCommentsImage(ViewHolderOfPost holderOfBlogPost, final String blogPostId, final String currentUserId) {
        holderOfBlogPost.commentButtonView.setOnClickListener(view -> sendToCommentsFragment(view, blogPostId, currentUserId));
    }

    public void sendToCommentsFragment(View view, String idOfPost, String currentUserId) {
        new HomeFragment().switchFragment(view, idOfPost, currentUserId);
    }

    private void setOrRemoveLikesInDatabase(ViewHolderOfPost holderOfPost, final String postId, final String currentUserId, final String description) {
        holderOfPost.likesButtonView.setOnClickListener(
                view -> firebasefirestore
                .collection("Posts")
                .document(postId)
                .collection("Likes")
                .document(currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().exists()) {
                        addLikes(postId, currentUserId, description);
                    } else {
                        removeLikes(postId, currentUserId, description);
                    }
                }));
    }

    private void removeLikes(String postId, String currentUserId, String description) {
        firebasefirestore
                .collection("Posts")
                .document(postId)
                .collection("Likes")
                .document(currentUserId)
                .delete();
        Toast.makeText(context, "Removed a like to " + description, Toast.LENGTH_LONG).show();
    }

    private void addLikes(String postId, String currentUserId, String description) {
        Map<String, Object> likestMap = new HashMap<>();
        firebasefirestore
                .collection("Posts")
                .document(postId)
                .collection("Likes")
                .document(currentUserId)
                .set(likestMap);
        Toast.makeText(context, "Added a like to " + description, Toast.LENGTH_LONG).show();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setLikesVisibility(final ViewHolderOfPost h, String idOfPost, String idOfCurrentUser) {
        firebasefirestore.collection("Posts")
                .document(idOfPost)
                .collection("Likes")
                .document(idOfCurrentUser)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot == null) {
                        throw new AssertionError();
                    } else if (documentSnapshot.exists()) {
                        h.likesButtonView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_animated));
                    } else {
                        h.likesButtonView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                    }
                });
    }

    private void setLikesCount(final ViewHolderOfPost h, String blogPostId) {
        firebasefirestore.collection("Posts")
                .document(blogPostId)
                .collection("Likes")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots == null) {
                        throw new AssertionError();
                    } else if (!queryDocumentSnapshots.isEmpty()) {
                        h.updateLikesCount(queryDocumentSnapshots.size());
                    } else {
                        h.updateLikesCount(0);
                    }
                });
    }

//    private void setUsernameAndImage(final ViewHolderOfPost h, String postsUserId) {
//        this.firebasefirestore.collection("Users").document(String.valueOf(postsUserId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    h.setUsernameAndUserProfileImage(task.getResult().getString("profile_name_of"), task.getResult().getString("profile_image"));
//                }
//            }
//        });
//    }

    public int getItemCount() {
        return listOfPosts.size();
    }

    private class ViewHolderOfPost extends RecyclerView.ViewHolder {

        public ImageView commentButtonView;
        public ImageView likesButtonView;
        public ImageView location_button_view;
        public CircleImageView delete_post_view;
        public CircleImageView userProfileImage;
        private final View view;

        public ViewHolderOfPost(View v) {
            super(v);
            view = v;
            commentButtonView = view.findViewById(R.id.new_post_comment);
            Log.d("commentButtonView",commentButtonView.toString());
//            location_button_view = view.findViewById(R.id.new_post_location_thumbnail);
//            delete_post_view = view.findViewById(R.id.post_clear);
            likesButtonView = view.findViewById(R.id.new_post_likes_thumb);
            userProfileImage = view.findViewById(R.id.new_post_user_image);
            Fresco.initialize(context);
        }

        public void setDescription(String description) {
            ((TextView) view.findViewById(R.id.new_post_description)).setText(description);
        }

        public void setTime(String date) {
            ((TextView) view.findViewById(R.id.new_post_date)).setText(date);
        }

        public void setUsername(String userName) {
            ((TextView) view.findViewById(R.id.new_post_username_text)).setText(userName);
        }

        @SuppressLint("SetTextI18n")
        public void updateLikesCount(int count) {
            ((TextView) view.findViewById(R.id.new_post_likes)).setText(count + "");
        }

//        public void setImageURIAndThumbnailURI(String imageURI, String thumbnailURI) {
//            RequestOptions placeholderOption = new RequestOptions();
//            placeholderOption.placeholder((int) R.drawable.image_placeholder);
//            Glide.with(context)
//                    .applyDefaultRequestOptions(placeholderOption)
//                    .load(imageURI)
//                    .thumbnail(Glide.with(context).load(thumbnailURI)).into((ImageView) view.findViewById(R.id.new_post_image));
//        }

        @SuppressLint("CheckResult")
        public void setImageURI(String image_uri) {
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.image_placeholder);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption)
                    .load(image_uri)
                    .into((ImageView) view.findViewById(R.id.new_post_image));
        }

//        public void setUsernameAndUserProfileImage(String username, String userProfileImage) {
//            ((TextView) view.findViewById(R.id.new_post_username_text)).setText(username);
//            RequestOptions placeholderOption = new RequestOptions();
//            placeholderOption.placeholder((int) R.drawable.ic_account_circle);
//            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(userProfileImage).into((ImageView) (CircleImageView) view.findViewById(R.id.new_post_user_image));
//        }

        @SuppressLint("CheckResult")
        public void setUserProfileImageURI(String user_profile_image) {
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_account_circle);
//            Log.d("user_profile_image",user_profile_image);
            Glide.with(context)
                    .applyDefaultRequestOptions(placeholderOption)
                    .load(user_profile_image)
                    .into((CircleImageView) view.findViewById(R.id.new_post_user_image));
        }

        public void zoom(final int position) {
            ((ImageView) view.findViewById(R.id.new_post_image)).setOnClickListener(view -> {
                List<String> uris = new ArrayList<>();
                for (int i = 0; i < listOfPosts.size(); i++) {
                    uris.add(((PostModel) listOfPosts.get(i)).getImageURI());
                }
                new ImageViewer.Builder(context, uris).setStartPosition(position).hideStatusBar(false).allowZooming(true).allowSwipeToDismiss(true).show();
            });
        }

//        public void sendUserToMapFragment(final String location) {
//            location_button_view.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MapsFragment(location)).commit();
//                }
//            });
//        }

        @SuppressLint("SetTextI18n")
        public void updateCommentsCount(int numberOfComments) {
            ((TextView) view.findViewById(R.id.post_blog_comments_counter)).setText(numberOfComments + "");
        }

//        public void setLocationInViewer(String location) {
//            ((TextView) view.findViewById(R.id.location_text)).setText(location);
//        }
    }
}
