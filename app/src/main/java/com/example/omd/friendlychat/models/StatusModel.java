package com.example.omd.friendlychat.models;

/**
 * Created by Delta on 18/05/2017.
 */

public class StatusModel {
    private String Status;

    public StatusModel(String status) {
        Status = status;
    }

    public StatusModel() {
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}

