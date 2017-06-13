package com.example.omd.friendlychat.models;

/**
 * Created by Delta on 15/05/2017.
 */

public class ChatModel {

    private String LastMessage;
    private String time;
    private String sender_Id;
    private String reciver_Id;
    private String title;


    public ChatModel(String lastMessage, String time, String sender_Id, String reciver_Id, String title) {
        LastMessage = lastMessage;
        this.time = time;
        this.sender_Id = sender_Id;
        this.reciver_Id = reciver_Id;
        this.title = title;
    }

    public ChatModel() {
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender_Id() {
        return sender_Id;
    }

    public void setSender_Id(String sender_Id) {
        this.sender_Id = sender_Id;
    }

    public String getReciver_Id() {
        return reciver_Id;
    }

    public void setReciver_Id(String reciver_Id) {
        this.reciver_Id = reciver_Id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}