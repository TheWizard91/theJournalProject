package com.thewizard91.thejournal.models.chosenGallery;

public class collectionOfImagesOfChosenGallery {
    private String[] collectAllImagesInThisGallery;
    private String numberOfImages;

    public collectionOfImagesOfChosenGallery() {
    }

    public collectionOfImagesOfChosenGallery(String numberOfImages, String[] collectAllImagesInThisGallery) {
        this.numberOfImages = numberOfImages;
        this.collectAllImagesInThisGallery = collectAllImagesInThisGallery;
    }

    public String[] getCollectAllImagesInThisGallery() {
        return this.collectAllImagesInThisGallery;
    }

    public void setCollectAllImagesInThisGallery(String[] collectAllImagesInThisGallery) {
        this.collectAllImagesInThisGallery = collectAllImagesInThisGallery;
    }

    public String getNumberOfImages() {
        return this.numberOfImages;
    }

    public void setNumberOfImages(String numberOfImages) {
        this.numberOfImages = numberOfImages;
    }
}
