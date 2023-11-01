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
    private String delete_thumbnail_uri;
    private String imageURI;
    private String likes;
    private String location;
    private String thumbnail_uri;
    @ServerTimestamp
    private Date timestamp;
//    private FieldValue timestamp;
    private String user_uri;
    private String userProfileImageURI;
    private String username;

    public PostModel() {}
    public PostModel(String imageURI,
                     String user_uri,
                     FieldValue timestamp,
                     String username,
                     String title,
                     String description,
                     String userProfileImageURI) {

        this.imageURI = imageURI;
        this.user_uri = user_uri;
//        this.timestamp = timestamp;
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
        return delete_thumbnail_uri;
    }
    public void setDeletePostThumbnailUri(String delete_thumbnail_uri) { this.delete_thumbnail_uri = delete_thumbnail_uri; }
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
        return thumbnail_uri;
    }
    public void setThumbnailURI(String thumbnail_uri) {
        this.thumbnail_uri = thumbnail_uri;
    }
    public String getTimestamp() { return String.valueOf(timestamp); }
//    public void setTimestamp(FieldValue timestamp) { this.timestamp = timestamp; }
    public String getUserId() {
        return user_uri;
    }
    public void setUserId(String user_uri) {
        this.user_uri = user_uri;
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
        postMap.put("thumbnail_uri", "download_thumb_uri");
        postMap.put("user_uri", user_uri);
        postMap.put("timestamp", timestamp);
        postMap.put("username", username);
        postMap.put("title", title);
        postMap.put("description", description);
        postMap.put("location", FirebaseAnalytics.Param.LOCATION);
        postMap.put("userProfileImageURI", userProfileImageURI);

        return postMap;

    }
}
