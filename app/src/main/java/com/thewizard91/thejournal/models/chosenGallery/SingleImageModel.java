package com.thewizard91.thejournal.models.chosenGallery;

import android.widget.Toast;

public class SingleImageModel extends SingleImageModelId{
    private String imageUri;
    private int type;

    public SingleImageModel() {
    }

    public SingleImageModel(String imageUri, int type) {
    }

    public String getImageUri() {
        return this.imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
