package com.thewizard91.thejournal.models.user;

public class UserModel {
    private String username;
    private String email;
    private String userProfileImageUri;
    public UserModel () {}
    public UserModel(String email, String username, String userProfileImageUri) {
        this.email = email;
        this.username = username;
        this.userProfileImageUri = userProfileImageUri;
    }
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
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() { return this.email; }

}
