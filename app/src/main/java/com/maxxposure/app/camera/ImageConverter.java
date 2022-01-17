package com.maxxposure.app.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;

public class ImageConverter {

    public static Bitmap convertByteArrayToBitmap(byte[] imageData, int imageFormat, int width, int height) {
        Bitmap bitmap = null;
        YuvImage yuv = new YuvImage(imageData, imageFormat, width, height, null);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
            byte[] bytes = out.toByteArray();
            out.close();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
