package com.example.javanesescriptrecognizer.data.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializableBitmap implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Bitmap bitmap;

    public SerializableBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private void writeObject(@NonNull ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            BitmapDataObject bitmapDataObject = new BitmapDataObject();
            bitmapDataObject.imageByteArray = stream.toByteArray();
            out.writeObject(bitmapDataObject);
        }
    }

    /**
     * Included for serialization - read this object from the supplied input
     * stream.
     */
    private void readObject(@NonNull ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        BitmapDataObject bitmapDataObject = (BitmapDataObject) in.readObject();
        if (bitmapDataObject != null) {
            bitmap = BitmapFactory.decodeByteArray(
                    bitmapDataObject.imageByteArray,
                    0,
                    bitmapDataObject.imageByteArray.length
            );
        }
    }

    protected static class BitmapDataObject implements Serializable {
        private static final long serialVersionUID = 111696345129311948L;
        public byte[] imageByteArray;
    }
}
