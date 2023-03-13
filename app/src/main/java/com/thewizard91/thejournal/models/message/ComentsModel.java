package com.thewizard91.thejournal.models.message;

public class ComentsModel {
    private String message;
    private String sender;
    private String createArt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getCreateArt() {
        return createArt;
    }

    public void setCreateArt(String createArt) {
        this.createArt = createArt;
    }
}
