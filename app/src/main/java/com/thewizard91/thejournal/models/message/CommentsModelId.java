package com.thewizard91.thejournal.models.message;

import com.google.firebase.database.Exclude;

public class CommentsModelId {
    @Exclude
    public String CommentId;

    public <T extends CommentsModelId> T withId (String id) {
        this.CommentId = id;
        return (T) this;
    }
}
