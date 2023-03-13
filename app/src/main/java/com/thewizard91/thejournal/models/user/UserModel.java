package com.thewizard91.thejournal.models.user;

public class UserModel {
    private String username;
    private String userProfileImageUri;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfileImageUri() {
        return userProfileImageUri;
    }

    public void setUserProfileImageUri(String userProfileImageUri) {
        this.userProfileImageUri = userProfileImageUri;
    }
}
