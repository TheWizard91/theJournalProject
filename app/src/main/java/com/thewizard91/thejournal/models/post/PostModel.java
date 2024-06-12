package com.thewizard91.thejournal.models.post;

import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostModel extends PostId implements Serializable {
    private String title;
    private String description;
    private String image;
    private String comments;
    private String deleteThumbnailURI;
    private String imageURI;
    private String likes;
    private String location;
    private String thumbnailURI;
//    @ServerTimestamp
    private String timestamp;
    private String userId;
    private String userProfileImageURI;
    private String username;

    public PostModel() {}
    public PostModel(String imageURI,
                     String userId,
                     String timestamp,
                     String username,
                     String title,
                     String description,
                     String userProfileImageURI) {

        this.imageURI = imageURI;
        this.userId = userId;
        this.timestamp = timestamp;
        this.username = username;
        this.title = title;
        this.description = description;
        this.userProfileImageURI = userProfileImageURI;
        Log.d("in_model_imageURI", this.imageURI);
    }
//    public String getImage () { return image; }
//    public void setImage (String image) { this.image = image; }
    public String getTitle () {
        return title;
    }
    public void setTitle (String title) {
        this.title = title;
    }
    public String getDescription () {
        return description;
    }
    public void setDescription (String description) {
        this.description = description;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getDeletePostThumbnailUri() {
        return deleteThumbnailURI;
    }
    public void setDeletePostThumbnailUri(String deleteThumbnailURI) { this.deleteThumbnailURI = deleteThumbnailURI; }
    public String getImageURI() {
//        Log.d("im_uri",imageURI);
        return imageURI; }
    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
    public String getLikes() {
        return likes;
    }
    public void setLikes(String likes) {
        this.likes = likes;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getThumbnailURI() {
        return thumbnailURI;
    }
    public void setThumbnailURI(String thumbnailURI) {
        this.thumbnailURI = thumbnailURI;
    }
    public String getTimestamp() { return String.valueOf(timestamp); }
//    public void setTimestamp(FieldValue timestamp) { this.timestamp = timestamp; }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserProfileImageURI() {
        return userProfileImageURI;
    }
    public void setUserProfileImageURI(String userProfileImageURI) {
        this.userProfileImageURI = userProfileImageURI;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Map<String, Object> postFirebaseDatabaseMap() {

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("imageURI", imageURI);
        postMap.put("likes", "0");
        postMap.put("thumbnailURI", "download_thumb_uri");
        postMap.put("userId", userId);
        postMap.put("timestamp", timestamp);
        postMap.put("username", username);
        postMap.put("title", title);
        postMap.put("description", description);
        postMap.put("location", FirebaseAnalytics.Param.LOCATION);
        postMap.put("userProfileImageURI", userProfileImageURI);

        return postMap;

    }
}
