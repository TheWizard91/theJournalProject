package com.thewizard91.thejournal.models.notifications;

public class NotificationId {
    public String NotificationId;

    public <T extends NotificationId> T withId (String id) {
        this.NotificationId = id;
        return (T) this;
    }
}
