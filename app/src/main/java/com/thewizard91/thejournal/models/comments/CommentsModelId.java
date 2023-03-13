package com.thewizard91.thejournal.models.comments;

import com.google.firebase.database.Exclude;

class CommentsModelId {
    @Exclude
    public String CommentsModelId;

    public <T extends CommentsModelId> T withId(String id) {
        this.CommentsModelId = id;
        return (T) this;
    }
}