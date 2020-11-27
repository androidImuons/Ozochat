package com.ozonetech.ozochat.model;

public class SelectionImageList {
    String imagePath;
    boolean isSelected;

    public SelectionImageList(String imagePath) {
        this.imagePath = imagePath;
        this.isSelected = false;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
