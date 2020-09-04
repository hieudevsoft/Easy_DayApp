package com.example.easyday.ENTITY;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ImageNote implements Serializable {
    private transient Bitmap imageNote;
    private String descriptionImage;

    public ImageNote(Bitmap imageNote, String descriptionImage) {
        this.imageNote = imageNote;
        this.descriptionImage = descriptionImage;
    }

    public ImageNote() {
    }

    public Bitmap getImageNote() {
        return imageNote;
    }

    public void setImageNote(Bitmap imageNote) {
        this.imageNote = imageNote;
    }

    public String getDescriptionImage() {
        return descriptionImage;
    }

    public void setDescriptionImage(String descriptionImage) {
        this.descriptionImage = descriptionImage;
    }
}
