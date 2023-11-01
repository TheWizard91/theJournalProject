package com.thewizard91.thejournal.models.listTwo;

import android.graphics.drawable.Drawable;

public class ListenItemInListTwo {
    private String description;
    private Drawable imageUri;
    private Drawable forwardArrow;

    public ListenItemInListTwo() {}

    public ListenItemInListTwo(String description, Drawable imageUri, Drawable forwardArrow) {
        this.description = description;
        this.imageUri = imageUri;
        this.forwardArrow = forwardArrow;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setImageUri(Drawable imageUri) { this.imageUri = imageUri; }

    public Drawable getImageUri() { return imageUri; }

    public void setForwardArrowImage(Drawable forwardArrow) {
        this.forwardArrow = forwardArrow;
    }

    public Drawable getForwardArrow() {
        return forwardArrow;
    }
}
