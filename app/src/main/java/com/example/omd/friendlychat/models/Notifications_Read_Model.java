package com.example.omd.friendlychat.models;

/**
 * Created by Delta on 12/06/2017.
 */

public class Notifications_Read_Model {
    String userId;
    boolean read;

    public Notifications_Read_Model(String userId, boolean read) {
        this.userId = userId;
        this.read = read;
    }

    public Notifications_Read_Model() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
