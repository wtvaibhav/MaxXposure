package com.maxxposure.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.maxxposure.app.MaxXposureApplication;
import com.maxxposure.app.R;
import com.maxxposure.app.view.ScrRegNo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MyDetector extends Detector {
    private Detector mDelegate;
    private int rect[];
    private int boxWidth;
    private int boxHeight;


    public MyDetector(Detector delegate, int rect[]) {
        this.mDelegate = delegate;
        this.rect = rect;
    }

    public void setBoxSize(int boxWidth, int boxHeight) {
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
    }

    public SparseArray detect(Frame frame) {
        int width = frame.getMetadata().getWidth();
        int height = frame.getMetadata().getHeight();
        int left = (int) (rect[1] * ScrRegNo.scaleY) - (int) ((boxHeight * ScrRegNo.scaleX) / 2f);
        int top = (int) (rect[0] * ScrRegNo.scaleY);
        int right = (int) (rect[1] * ScrRegNo.scaleY) + (int) (boxHeight * ScrRegNo.scaleX) - (int) ((boxHeight * ScrRegNo.scaleX) / 2f);
        int bottom = (int) (boxWidth * ScrRegNo.scaleX);
        if (boxWidth == 0 || boxHeight == 0) {
            return null;
        }

        YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(left, top, right, bottom), 100, byteArrayOutputStream);
        byte[] jpegArray = byteArrayOutputStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);

        Frame croppedFrame =
                new Frame.Builder()
                        .setBitmap(bitmap)
                        .setRotation(frame.getMetadata().getRotation())
                        .build();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Maxy" + File.separator + System.currentTimeMillis() + ".jpg";
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mDelegate.detect(croppedFrame);
    }

    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }
}