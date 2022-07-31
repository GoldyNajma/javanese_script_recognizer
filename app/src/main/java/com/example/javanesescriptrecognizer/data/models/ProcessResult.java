package com.example.javanesescriptrecognizer.data.models;

import android.graphics.Bitmap;

import com.example.javanesescriptrecognizer.base.BaseModel;

import java.io.Serializable;

public class ProcessResult extends BaseModel implements Serializable {
    final private int id;
    final private SerializableBitmap image;
    final private String title;
    final private String unicode;

    public ProcessResult(int id, Bitmap image, String title) {
        this(id, image, title, "");
    }

    public ProcessResult(int id, Bitmap image, String title, String unicode) {
        this.id = id;
        this.image = new SerializableBitmap(image);
        this.title = title;
        this.unicode = unicode;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image.getBitmap();
    }

    public String getUnicode() {
        return unicode;
    }
}
