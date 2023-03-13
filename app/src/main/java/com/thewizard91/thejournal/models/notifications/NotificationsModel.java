package com.thewizard91.thejournal.models.notifications;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.FieldValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationsModel extends NotificationId {
    private String username;
    private Date date;
    private FieldValue time;
    private String comment;
    private String userId;
    private String userProfileImageURI;
    private String notificationText;

    public NotificationsModel() {}
    public NotificationsModel(String username,String userId,FieldValue time,
                              String userProfileImageURI,String notificationText) {
        this.username = username;
        this.userId = userId;
        this.time = time;
//        this.comment = comment;
        this.userProfileImageURI = userProfileImageURI;
        this.notificationText = notificationText;
    }
    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return this.username;}
    public void setDate(Date date) {this.date = date;}
    public Date getDate() {return this.date;}
    public void setTime(FieldValue time) {this.time= time;}
    public FieldValue getTime() {return time;}
    public void setComment(String comment) {this.comment = comment;}
    public String getComment() {return this.comment;}
    public void setUserId(String uId) {this.userId = uId;}
    public String getUserId() {return this.userId;}
    public void setUserProfileImageURI(String userProfileImageURI) {this.userProfileImageURI = userProfileImageURI;}
    public String getUserProfileImageURI() {
        return this.userProfileImageURI;
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
        realtimePostMap.put("time",time);
        realtimePostMap.put("comment",comment);
        realtimePostMap.put("userId",userId);
        realtimePostMap.put("userProfileImageURI",userProfileImageURI);
        realtimePostMap.put("notificationText",notificationText);
        return realtimePostMap;
    }
}
