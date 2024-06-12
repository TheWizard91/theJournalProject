package com.thewizard91.thejournal.models.user;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String username;
    private String email;
    private String userProfileImageURI;
    private String userPhoneNumber;
    private String userDescription;
    private String userAddress;
    private String userGender;
    private String userAge;
    private String numberOfPosts;
    private String numberOfLikes;
    private String numberOfComments;

    public UserModel () {}
    public UserModel(String email,
                     String username,
                     String userProfileImageURI,
                     String userPhoneNumber,
                     String userDescription,
                     String userAddress,
                     String userGender,
                     String userAge,
                     String numberOfPosts,
                     String numberOfLikes,
                     String numberOfComments) {
        this.email = email;
        this.username = username;
        this.userProfileImageURI = userProfileImageURI;
        this.userPhoneNumber = userPhoneNumber;
        this.userDescription = userDescription;
        this.userAddress = userAddress;
        this.userGender = userGender;
        this.userAge = userAge;
        this.numberOfPosts = numberOfPosts;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
    }

    public UserModel(String numberOfPosts,String numberOfLikes,String numberOfComments) {
        this.numberOfPosts = numberOfPosts;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserProfileImageURI() {
        return userProfileImageURI;
    }
    public void setUserProfileImageURI(String userProfileImageURI) {
        this.userProfileImageURI = userProfileImageURI;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() { return this.email; }
    public void setUserPhoneNumber(String userPhoneNumber) {this.userPhoneNumber = userPhoneNumber;}
    public String getUserPhoneNumber() {return userPhoneNumber;}
    public void setUserDescription(String userDescription) {this.userDescription = userDescription;}
    public String getUserDescription() {return this.userDescription;}
    public void setUserAddress(String userAddress) {this.userAddress = userAddress;}
    public String getUserAddress() {return userAddress;}
    public void setUserAge(String userAge) {this.userAge = userAge;}
    public String getUserAge() {return userAge;}
    public void setNumberOfPosts(String numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }
    public String getNumberOfPosts() {return numberOfPosts;}
    public void setNumberOfLikes(String numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }
    public String getNumberOfLikes() {return numberOfLikes;}
    public void setNumberOfComments(String numberComments) {
        this.numberOfComments = numberComments;
    }
    public String getNumberOfComments() {return this.numberOfComments = numberOfComments;}
    public Map<String,Object> userDatabase() {
        HashMap<String,Object> userData = new HashMap<>();
        userData.put("userProfileImageURI", userProfileImageURI);
        userData.put("username", username);
        userData.put("description", userDescription);
        userData.put("phoneNumber", userPhoneNumber);
        userData.put("address", userAddress);
        userData.put("gender", userGender);
        userData.put("age", userAge);
        userData.put("numberOfPosts",numberOfPosts);
        userData.put("numberOfLikes",numberOfLikes);
        userData.put("numberOfComments",numberOfComments);
        return userData;
    }
    public Map<String, Object> updatedUserDatabase(String updateField) {
        HashMap<String, Object> updatedData = new HashMap<>();
        switch (updateField) {
            case "numberOfPosts":
                updatedData.put("numberOfPosts",numberOfPosts);
                break;
            case "numberOfLikes":
                updatedData.put("numberOfLikes",numberOfLikes);
                break;
            case "numberOfComments":
                updatedData.put("numberOfComments",numberOfComments);
                break;
            default:
                break;
        }
        return updatedData;
    }

}
