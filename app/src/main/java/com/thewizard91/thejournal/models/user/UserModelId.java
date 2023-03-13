package com.thewizard91.thejournal.models.user;

import com.google.firebase.database.Exclude;
import com.thewizard91.thejournal.models.post.PostId;

public class UserModelId {
    @Exclude
    public String UserModelId;

    public <T extends com.thewizard91.thejournal.models.user.UserModelId> T withId (String id) {
        this.UserModelId = id;
        return (T) this;
    }
}
