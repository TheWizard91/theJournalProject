package com.thewizard91.thejournal.models.comments;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentsModel extends CommentsModelId {
    private String blogPostId;
    private String commentReplyImageUri;
    private String commentText;
    private String commentsCount;
    private String imageUri;
    private String likesCount;
    private String thumbsUpImageUri;
    private Date timestamp;
    private FieldValue time;
    private String userId;
    private String username;
    private String userProfileImageUri;

    public CommentsModel() {}
    public CommentsModel(String blogPostId, String commentReplyImageUri,
                         String commentText, String commentsCount,
                         String imageUri, String likesCount,
                         String thumbsUpImageUri, FieldValue time,
                         String userId, String username, String userProfileImageUri) {
        this.blogPostId = blogPostId;
        this.commentReplyImageUri = commentReplyImageUri;
        this.commentText = commentText;
        this.commentsCount = commentsCount;
        this.imageUri = imageUri;
        this.likesCount = likesCount;
        this.thumbsUpImageUri = thumbsUpImageUri;
        this.time = time;
        this.userId = userId;
        this.username = username;
        this.userProfileImageUri = userProfileImageUri;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUri() {
        return this.imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return this.commentText;
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
        return this.thumbsUpImageUri;
    }

    public void setThumbsUpImageUri(String thumbsUpImageUri) {
        this.thumbsUpImageUri = thumbsUpImageUri;
    }

    public String getCommentReplyImageUri() {
        return this.commentReplyImageUri;
    }

    public void setCommentReplyImageUri(String commentReplyImageUri) {
        this.commentReplyImageUri = commentReplyImageUri;
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
        return userProfileImageUri;
    }

    public void setUserProfileImageUri(String userProfileImageUri) {
        this.userProfileImageUri = userProfileImageUri;
    }
    public Map<String,Object> firebaseDatabaseMap() {
        Map<String,Object> commentsMap = new HashMap<>();
        commentsMap.put("blogPostId",blogPostId);
        commentsMap.put("commentReplyImageUri",commentReplyImageUri);
        commentsMap.put("commentText",commentText);
        commentsMap.put("commentsCount",commentsCount);
        commentsMap.put("imageUri",imageUri);
        commentsMap.put("likesCount",likesCount);
        commentsMap.put("thumbsUpImageUri",thumbsUpImageUri);
        commentsMap.put("timestamp",time);
        commentsMap.put("userId",userId);
        commentsMap.put("username",username);
        commentsMap.put("userProfileImageUri",userProfileImageUri);

        return commentsMap;
    }
}