package com.thewizard91.thejournal.models.listOne;

import android.graphics.drawable.Drawable;

public class ListenItemInListOne {
    private Drawable image;
    private String description;
    private String numberOfElementsInSection;

    public ListenItemInListOne() {}

    public void setImage(Drawable image) { this.image = image; }
    public Drawable getImage() { return image; }
    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }
    public void setNumberOfElementsInSection(String numberOfElementsInSection) { this.numberOfElementsInSection = numberOfElementsInSection; }
    public String getNumberOfElementsInSection() { return numberOfElementsInSection; }
}
