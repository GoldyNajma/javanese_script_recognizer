package com.example.javanesescriptrecognizer.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class IOUtil {
    public static void saveText(String data, String folderName, String prefix) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/DCIM/JavaneseScriptRecognizer/" + folderName);
        myDir.mkdirs();

        String fileName = prefix + "-"+ System.currentTimeMillis() +".txt";

        File file = new File (myDir, fileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    https://stackoverflow.com/a/41309074
    public static void saveImage(Bitmap finalBitmap, String folderName, String prefix) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/DCIM/JavaneseScriptRecognizer/" + folderName);
        myDir.mkdirs();

        String fileName = prefix + "-"+ System.currentTimeMillis() +".png";

        File file = new File (myDir, fileName);

        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
