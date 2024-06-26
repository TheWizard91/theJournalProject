package com.thewizard91.thejournal.models.notifications;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class NotificationsModel extends NotificationId implements Serializable {
    private String username;
    private String date;
//    private String comment;
    private String userId;
    private String userProfileImageURI;
    private String notificationText;
    public NotificationsModel() {}
    public NotificationsModel(String username,
                              String userId,
                              String date,
                              String userProfileImageURI,
                              String notificationText) {
        this.username = username;
        this.userId = userId;
        this.date = date;
//        this.comment = comment;
        this.userProfileImageURI = userProfileImageURI;
        this.notificationText = notificationText;
    }
    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}
    public void setDate(String date) {this.date = date;}
    public String getDate() {return date;}
//    public void setTime(FieldValue time) {this.time= time;}
//    public FieldValue getTime() {return time;}
//    public void setComment(String comment) {this.comment = comment;}
//    public String getComment() {return this.comment;}
    public void setUserId(String uId) {this.userId = uId;}
    public String getUserId() {return userId;}
    public void setUserProfileImageURI(String userProfileImageURI) {this.userProfileImageURI = userProfileImageURI;}
    public String getUserProfileImageURI() {
        return userProfileImageURI;
    }
    public String getNotificationText() {
        return notificationText;
    }
    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
    @Exclude
    public Map<String,Object> realTimeDatabaseMap() {
        HashMap<String,Object> realtimePostMap = new HashMap<>();
        realtimePostMap.put("username",username);
        realtimePostMap.put("date",date);
        realtimePostMap.put("userId",userId);
        realtimePostMap.put("userProfileImageURI",userProfileImageURI);
        realtimePostMap.put("notificationText",notificationText);
        return realtimePostMap;
    }
}
