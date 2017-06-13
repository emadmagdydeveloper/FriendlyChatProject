package com.example.omd.friendlychat.models;

/**
 * Created by Delta on 11/06/2017.
 */

public class Notifications_Model {

    public String userid;
    public String notificationtext;


    public Notifications_Model() {
    }

    public Notifications_Model(String userid, String notificationtext) {
        this.userid = userid;
        this.notificationtext = notificationtext;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNotificationtext() {
        return notificationtext;
    }

    public void setNotificationtext(String notificationtext) {
        this.notificationtext = notificationtext;
    }

}
