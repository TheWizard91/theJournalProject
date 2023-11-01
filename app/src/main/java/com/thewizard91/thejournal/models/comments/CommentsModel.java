package com.thewizard91.thejournal.models.comments;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentsModel extends CommentsModelId {
    private String post_id;
    private String comment_reply_image_uri;
    private String comment_text;
    private String comments_count;
    private String image_uri;
    private String likes_count;
    private String thumbs_image_uri;
    private Date timestamp;
    private FieldValue time;
    private String user_id;
    private String username;
    private String user_profile_image_uri;

    public CommentsModel() {}
    public CommentsModel(String post_id, String comment_reply_image_uri,
                         String comment_text, String comments_count,
                         String image_uri, String likes_count,
                         String thumbs_image_uri, FieldValue time,
                         String user_id, String username, String user_profile_image_uri) {

        this.post_id = post_id;
        this.comment_reply_image_uri = comment_reply_image_uri;
        this.comment_text = comment_text;
        this.comments_count = comments_count;
        this.image_uri = image_uri;
        this.likes_count = likes_count;
        this.thumbs_image_uri = thumbs_image_uri;
        this.time = time;
        this.user_id = user_id;
        this.username = username;
        this.user_profile_image_uri = user_profile_image_uri;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUri() {
        return image_uri;
    }

    public void setImageUri(String image_uri) {
        this.image_uri = image_uri;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentText() {
        return comment_text;
    }

    public void setCommentText(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getLikesCount() {
        return this.likes_count;
    }

    public void setLikesCount(String likes_count) {
        this.likes_count = likes_count;
    }

    public String getThumbsUpImageUri() {
        return thumbs_image_uri;
    }

    public void setThumbsUpImageUri(String thumbs_image_uri) {
        this.thumbs_image_uri = thumbs_image_uri;
    }

    public String getCommentReplyImageUri() {
        return comment_reply_image_uri;
    }

    public void setCommentReplyImageUri(String comment_reply_image_uri) {
        this.comment_reply_image_uri = comment_reply_image_uri;
    }

    public String getCommentsCount() {
        return this.comments_count;
    }

    public void setCommentsCount(String comments_count) {
        this.comments_count = comments_count;
    }

    public String getUserId() {
        return this.user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserProfileImageUri() {
        return user_profile_image_uri;
    }

    public void setUserProfileImageUri(String user_profile_image_uri) {
        this.user_profile_image_uri = user_profile_image_uri;
    }
    public Map<String,Object> firebaseDatabaseMap() {
        Map<String,Object> commentsMap = new HashMap<>();
        commentsMap.put("postId",post_id);
        commentsMap.put("commentReplyImageUri",comment_reply_image_uri);
        commentsMap.put("commentText",comment_text);
        commentsMap.put("commentsCount",comments_count);
        commentsMap.put("imageURI",image_uri);
        commentsMap.put("likesCount",likes_count);
        commentsMap.put("thumbsImageURI",thumbs_image_uri);
        commentsMap.put("timestamp",time);
        commentsMap.put("userId",user_id);
        commentsMap.put("username",username);
        commentsMap.put("userProfileImageURI",user_profile_image_uri);

        return commentsMap;
    }
}