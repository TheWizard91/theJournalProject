package com.thewizard91.thejournal.models.comments;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentsModel extends CommentsModelId {
    private String postId;
    private String commentReplyImageURI;
    private String commentText;
    private String commentsCount;
    private String imageURI;
    private String likesCount;
    private String thumbsImageURI;
    private Date timestamp;
    private FieldValue time;
    private String userId;
    private String username;
    private String userProfileImageURI;

    public CommentsModel() {}
    public CommentsModel(String postId, String commentReplyImageURI,
                         String commentText, String commentsCount,
                         String imageURI, String likesCount,
                         String thumbsImageURI, FieldValue time,
                         String userId, String username, String userProfileImageURI) {

        this.postId = postId;
        this.commentReplyImageURI= commentReplyImageURI;
        this.commentText = commentText;
        this.commentsCount = commentsCount;
        this.imageURI = imageURI;
        this.likesCount = likesCount;
        this.thumbsImageURI = thumbsImageURI;
        this.time = time;
        this.userId = userId;
        this.username = username;
        this.userProfileImageURI = userProfileImageURI;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUri() {
        return imageURI;
    }

    public void setImageUri(String imageURI) {
        this.imageURI = imageURI;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getLikesCount() {
        return this.likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getThumbsUpImageUri() {
        return thumbsImageURI;
    }

    public void setThumbsUpImageUri(String thumbsImageURI) {
        this.thumbsImageURI = thumbsImageURI;
    }

    public String getCommentReplyImageUri() {
        return commentReplyImageURI;
    }

    public void setCommentReplyImageUri(String commentReplyImageURI) {
        this.commentReplyImageURI = commentReplyImageURI;
    }

    public String getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfileImageUri() {
        return userProfileImageURI;
    }

    public void setUserProfileImageUri(String userProfileImageURI) {
        this.userProfileImageURI = userProfileImageURI;
    }
    public Map<String,Object> firebaseDatabaseMap() {
        Map<String,Object> commentsMap = new HashMap<>();
        commentsMap.put("postId", postId);
        commentsMap.put("commentReplyImageUri", commentReplyImageURI);
        commentsMap.put("commentText", commentText);
        commentsMap.put("commentsCount", commentsCount);
        commentsMap.put("imageURI", imageURI);
        commentsMap.put("likesCount", likesCount);
        commentsMap.put("thumbsImageURI", thumbsImageURI);
        commentsMap.put("timestamp", time);
        commentsMap.put("userId", userId);
        commentsMap.put("username", username);
        commentsMap.put("userProfileImageURI", userProfileImageURI);

        return commentsMap;
    }
}