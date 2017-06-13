package com.example.omd.friendlychat.models;

/**
 * Created by Delta on 15/05/2017.
 */

public class MessageModel {
    private String MessageText;
    private String MessageTime;
    private String MessageType;

    public void setMessageText(String messageText) {
        MessageText = messageText;
    }

    public void setMessageTime(String messageTime) {
        MessageTime = messageTime;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public void setSender_id(String sender_id) {
        Sender_id = sender_id;
    }

    private String Sender_id;

    public MessageModel(String messageText, String messageTime, String messageType, String sender_id) {
        MessageText = messageText;
        MessageTime = messageTime;
        MessageType = messageType;
        Sender_id = sender_id;
    }

    public MessageModel() {
    }

    public String getMessageText() {
        return MessageText;
    }

    public String getMessageTime() {
        return MessageTime;
    }

    public String getMessageType() {
        return MessageType;
    }

    public String getSender_id() {
        return Sender_id;
    }
}
