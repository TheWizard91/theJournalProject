package com.thewizard91.thejournal.models.image;

import com.google.firebase.database.Exclude;

public class ImageId {
    @Exclude
    public String ImagesId;
    public <T extends ImageId> T withId (String id) {
        this.ImagesId = id;
        return (T) this;
    }
}
