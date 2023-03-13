package com.thewizard91.thejournal.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.Date;
import com.stfalcon.frescoimageviewer.ImageViewer;
//import com.thewizard91.thealbumproject.C2521RC2521R;
//import com.thewizard91.thealbumproject.fragments.maps.MapsFragment;
import com.thewizard91.thejournal.R;
import com.thewizard91.thejournal.fragments.HomeFragment;
import com.thewizard91.thejournal.fragments.MapsFragment;
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
    public FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    public interface CallBackMethodHelperForSetCurrentHolderImageURICallback {
        void onCallback(String str);
    }

    public PostAdapter(List<PostModel> blogPostImageModels) {
        this.listOfPosts = blogPostImageModels;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.posts_row, parent, false);
        myView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        this.context = parent.getContext();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        return new ViewHolderOfBlogPost(myView);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ViewHolderOfBlogPost holderOfBlogPost = (ViewHolderOfBlogPost) holder;
        String blogPostId = listOfPosts.get(position).PostId;
        String currentUserId = ((FirebaseUser) Objects.requireNonNull(firebaseAuth.getCurrentUser())).getUid();
        String postsUserId = listOfPosts.get(position).getUserId();
        String time = String.valueOf(listOfPosts.get(position).getTimestamp());
        String imageURI = listOfPosts.get(position).getImageURI();
//        String thumbnailURI = listOfPosts.get(position).getThumbnailURI();
        String description = listOfPosts.get(position).getDescription();
        String username = listOfPosts.get(position).getUsername();
        String userProfileImageUri = listOfPosts.get(position).getUserProfileImageURI();

//        holderOfBlogPost.setUserProfileImageURI(userImageUri);
        holderOfBlogPost.setTime(time);
        holderOfBlogPost.setUsername(username);
        holderOfBlogPost.setDescription(description);
        holderOfBlogPost.setImageURI(imageURI);
        holderOfBlogPost.setUserProfileImageURI(userProfileImageUri);
//        setUsernameAndImage(holderOfBlogPost, postsUserId);
//        holderOfBlogPost.setImageURIAndThumbnailURI(imageURI, thumbnailURI);
//        Log.d("BFlistOfPosts", String.valueOf(listOfPosts.get(position).getTimestamp()));
        Log.d("BFimageuri", imageURI);
        Log.d("BFblogpostId", blogPostId);
        Log.d("BFtime", time);
        Log.d("BFpostUsderId", postsUserId);
        Log.d("BFusername", username);
        Log.d("BFuserImageUri",userProfileImageUri);
        if (listOfPosts.get(position).getTimestamp() != null && !listOfPosts.isEmpty()) {
            holderOfBlogPost.setTime(time);//listOfPosts.get(position).getTimestamp() String.valueOf(DateFormat.format("MM/dd/yyyy", new Date(listOfPosts.get(position).getTimestamp().getTime())))
        } else {
            holderOfBlogPost.setTime("set time");
        }
        setLikesCount(holderOfBlogPost, blogPostId);
        setLikes(holderOfBlogPost, blogPostId, currentUserId);
        addOrDeleteLikes(holderOfBlogPost, blogPostId, currentUserId, description);
        clickOnCommentsImage(holderOfBlogPost, blogPostId, currentUserId);
//        clickOnLocationImage(holderOfBlogPost, blogPostId);
//        setLocation(holderOfBlogPost, blogPostId);
//        clickOnDeletePostImage(holderOfBlogPost, blogPostId);
        setCommentsCount(holderOfBlogPost, blogPostId);
        holderOfBlogPost.zoom(position);
    }

//    private void setLocation(final ViewHolderOfBlogPost holderOfBlogPost, String blogPostId) {
//        this.firebaseFirestore.collection("Posts").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
//        holderOfBlogPost.blogLocationButtonView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                firebaseFirestore.collection("Post").document(blogPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    private void setCommentsCount(final ViewHolderOfBlogPost holderOfBlogPost, String blogPostId) {
        this.firebaseFirestore.collection("Posts").document(blogPostId).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots == null) {
                    throw new AssertionError();
                } else if (!queryDocumentSnapshots.isEmpty()) {
                    holderOfBlogPost.updateCommentsCount(queryDocumentSnapshots.size());
                } else {
                    holderOfBlogPost.updateCommentsCount(0);
                }
            }
        });
    }

//    private void clickOnDeletePostImage(ViewHolderOfBlogPost holderOfBlogPost, final String blogPostId) {
//        holderOfBlogPost.deletePostView.setOnClickListener(new View.OnClickListener() {
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

    /* access modifiers changed from: private */
    public void deleteImageFromFirebaseFirestone(String blogPostId) {
        firebaseFirestore.collection("Posts").document(blogPostId).delete();
    }

    /* access modifiers changed from: private */
    public void deleteImageFromFirebaseStoreForUserAccessOnly(String postImageURI) {
        storageReference.child("storage_of: t/").child("post_images").child(postImageURI).delete();
    }

    /* access modifiers changed from: private */
    public void deleteImageFromFirebaseStorageForCommonPosts(String postImageURI) {
        storageReference.child("post_images_for_everyone_to_see/").child(postImageURI).delete();
    }

    public void setCurrentHolderImageURI(String blogPostId, final CallBackMethodHelperForSetCurrentHolderImageURICallback callBackMethodHelperForSetCurrentHolderImageURICallback) {
        firebaseFirestore.collection("Posts")
                .document(blogPostId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    public void onComplete(Task<DocumentSnapshot> task) {
                        Map<String, Object> map;
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists() && (map = documentSnapshot.getData()) != null) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if ("imageURI".equals(entry.getKey())) {
                                    String currentHolderImageURI = entry.getValue().toString();
                                    String imageIdJPG = currentHolderImageURI.substring(currentHolderImageURI.indexOf("%2Fpost_images%2F"));
                                    callBackMethodHelperForSetCurrentHolderImageURICallback.onCallback(imageIdJPG.substring(17, imageIdJPG.indexOf("?")));
                                }
                            }
                        }
                    }
        });
    }

//    public void setCurrentHolderProfileImageURI (String blogPID, final CallBackMethodHelperForSetCurrentHolderImageURICallback callBackMethodHelperForSetCurrentHolderImageURICallback) {
//        firebaseFirestore.collection("Post")
//                .document()
//    }

    /* access modifiers changed from: private */
//    public void refreshAdapter(List<PostModel> listOfPosts2) {
//        new PostAdapter(listOfPosts2).notifyDataSetChanged();
//    }

    public void refreshAdapter(List<PostModel> listPM) {
        new PostAdapter(listPM).notifyDataSetChanged();
    }

    private void clickOnCommentsImage(ViewHolderOfBlogPost holderOfBlogPost, final String blogPostId, final String currentUserId) {
        holderOfBlogPost.blogCommentButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendToCommentsFragment(view, blogPostId, currentUserId);
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendToCommentsFragment(View view, String blogPostId, String currentUserId) {
        new HomeFragment().switchFragment(view, blogPostId, currentUserId);
    }

    private void addOrDeleteLikes(ViewHolderOfBlogPost holderOfBlogPost, final String blogPostId, final String currentUserId, final String description) {
        holderOfBlogPost.blogLikeButtonView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> likestMap = new HashMap<>();
                            likestMap.put("timestamp", FieldValue.serverTimestamp().toString());
                            firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).set(likestMap);
                            Toast.makeText(context, "Added a like to " + description, Toast.LENGTH_LONG).show();
                            return;
                        }
                        firebaseFirestore.collection("Posts").document(blogPostId).collection("Likes").document(currentUserId).delete();
                        Toast.makeText(context, "Removed a like to " + description, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void setLikes(final ViewHolderOfBlogPost h, String blogPostId, String currentUserId) {
        firebaseFirestore.collection("Posts")
                .document(blogPostId)
                .collection("Likes")
                .document(currentUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {

            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    throw new AssertionError();
                } else if (documentSnapshot.exists()) {
                    h.blogLikeButtonView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_full));
                } else {
                    h.blogLikeButtonView.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border));
                }
            }
        });
    }

    private void setLikesCount(final ViewHolderOfBlogPost h, String blogPostId) {
        firebaseFirestore.collection("Posts")
                .document(blogPostId)
                .collection("Likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (queryDocumentSnapshots == null) {
                    throw new AssertionError();
                } else if (!queryDocumentSnapshots.isEmpty()) {
                    h.updateLikesCount(queryDocumentSnapshots.size());
                } else {
                    h.updateLikesCount(0);
                }
            }
        });
    }

//    private void setUsernameAndImage(final ViewHolderOfBlogPost h, String postsUserId) {
//        this.firebaseFirestore.collection("Users").document(String.valueOf(postsUserId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    h.setUsernameAndUserProfileImage(task.getResult().getString("profile_name_of"), task.getResult().getString("profile_image"));
//                }
//            }
//        });
//    }

    public int getItemCount() {
        return this.listOfPosts.size();
    }

    private class ViewHolderOfBlogPost extends RecyclerView.ViewHolder {

        public ImageView blogCommentButtonView;
        public ImageView blogLikeButtonView;
        public ImageView blogLocationButtonView;
        public CircleImageView deletePostView;
        public CircleImageView upi;
        private View view;

        public ViewHolderOfBlogPost(View v) {
            super(v);
            view = v;
//            Log.d("vIs:",view.toString());
            blogCommentButtonView = view.findViewById(R.id.new_post_comment);
//            blogLocationButtonView = view.findViewById(R.id.new_post_location_thumbnail);
//            deletePostView = view.findViewById(R.id.post_clear);
            blogLikeButtonView = view.findViewById(R.id.new_post_likes_thumb);
            upi = view.findViewById(R.id.new_post_user_image);
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

        public void setImageURI(String image_uri) {
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.image_placeholder);
            Log.d("image_uri",image_uri);
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
            ((ImageView) view.findViewById(R.id.new_post_image)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    List<String> uris = new ArrayList<>();
                    for (int i = 0; i < listOfPosts.size(); i++) {
                        uris.add(((PostModel) listOfPosts.get(i)).getImageURI());
                    }
                    new ImageViewer.Builder(context, uris).setStartPosition(position).hideStatusBar(false).allowZooming(true).allowSwipeToDismiss(true).show();
                }
            });
        }

        public void sendUserToMapFragment(final String location) {
            blogLocationButtonView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MapsFragment(location)).commit();
                }
            });
        }

        public void updateCommentsCount(int numberOfComments) {
            ((TextView) view.findViewById(R.id.post_blog_comments_counter)).setText(numberOfComments + "");
        }

//        public void setLocationInViewer(String location) {
//            ((TextView) view.findViewById(R.id.location_text)).setText(location);
//        }
    }
}