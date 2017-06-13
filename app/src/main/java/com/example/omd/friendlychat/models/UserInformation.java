package com.example.omd.friendlychat.models;

import java.io.Serializable;

/**
 * Created by Delta on 29/04/2017.
 */

public class UserInformation implements Serializable {
    private String userId;
    private String userImageUri;
    private String userName;
    private String userPhone;
    private String userEmail;

    public UserInformation() {
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserInformation(String userId, String userImageUri, String userName, String userPhone, String userEmail) {
        this.userId = userId;
        this.userImageUri = userImageUri;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;

    }

    public String getUserId() {
        return userId;
    }

    public String getUserImageUri() {
        return userImageUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
