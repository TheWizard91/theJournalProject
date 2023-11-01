package com.thewizard91.thejournal.models.chosenGallery;

import com.google.firebase.database.Exclude;

public class SingleImageModelId {
    @Exclude
    public String SingleImageModelId;

    public <T extends SingleImageModelId> T withId(String id) {
        this.SingleImageModelId = id;
        return (T) this;
    }
}