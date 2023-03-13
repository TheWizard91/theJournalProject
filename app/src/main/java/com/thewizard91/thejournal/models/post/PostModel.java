package com.thewizard91.thejournal.models.post;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostModel extends PostId {
    private String title;
    private String description;
    private String image;
    private String comments;
    private String deletePostThumbnailUri;
    private String imageURI;
    private String likes;
    private String location;
    private String thumbnailURI;
    private Date timestamp;
    private FieldValue serverTimestamp;
    private String userId;
    private String userProfileImageUri;
    private String username;

    public PostModel() {}
    public PostModel(String imageURI,
                     String userId,
                     FieldValue serverTimestamp,
                     String username,
                     String title,
                     String description,
                     String userProfileImageUri) {
        this.imageURI = imageURI;
        this.userId = userId;
        this.serverTimestamp = serverTimestamp;
        this.username = username;
        this.title = title;
        this.description = description;
        this.userProfileImageUri = userProfileImageUri;
    }
    public String getImage () { return image; }
    public void setImage (String image) { this.image = image; }
    public String getTitle () {
        return title;
    }
    public void setTitle (String title) {
        this.title = title;
    }
    public String getDescription () {
        return description;
    }
    public void setDescription () {
        this.description = description;
    }
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public String getDeletePostThumbnailUri() {
        return deletePostThumbnailUri;
    }
    public void setDeletePostThumbnailUri(String deletePostThumbnailUri) { this.deletePostThumbnailUri = deletePostThumbnailUri; }
    public String getImageURI() { return imageURI; }
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
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserProfileImageURI() {
        return userProfileImageUri;
    }
    public void setUserProfileImageURI(String userProfileImageUri) {
        this.userProfileImageUri = userProfileImageUri;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Map<String,Object> firebaseDatabaseMap() {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("imageURI", imageURI);
        postMap.put("likes", "0");
        postMap.put("thumbnailURI", "downloadThumbURI");
        postMap.put("userId", userId);
        postMap.put("timestamp", serverTimestamp);
        postMap.put("username", username);
        postMap.put("title", title);
        postMap.put("description", description);
        postMap.put("location", FirebaseAnalytics.Param.LOCATION);
        postMap.put("userProfileImageUri", userProfileImageUri);

        return postMap;
    }
}
