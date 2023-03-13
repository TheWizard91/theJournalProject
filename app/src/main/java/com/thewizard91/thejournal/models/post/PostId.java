package com.thewizard91.thejournal.models.post;

import com.google.firebase.database.Exclude;

public class PostId {
    @Exclude
    public String PostId;

    public <T extends PostId> T withId (String id) {
        this.PostId = id;
        return (T) this;
    }
}
