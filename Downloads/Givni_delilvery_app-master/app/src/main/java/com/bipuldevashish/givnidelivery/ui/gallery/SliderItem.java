package com.bipuldevashish.givnidelivery.ui.gallery;

public class SliderItem {

    private String description1,description2;
    private int imageUrl,circularImageView;

    public SliderItem(String description1, String description2, int imageUrl, int circularImageView) {
        this.description1 = description1;
        this.description2 = description2;
        this.imageUrl = imageUrl;
        this.circularImageView = circularImageView;
    }


    public String getDescription1() {
        return description1;
    }

    public void setDescription1(String description1) {
        this.description1 = description1;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCircularImageView() {
        return circularImageView;
    }

    public void setCircularImageView(int circularImageView) {
        this.circularImageView = circularImageView;
    }
}
