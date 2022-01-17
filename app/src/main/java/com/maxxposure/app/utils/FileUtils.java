package com.maxxposure.app.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.maxxposure.app.MaxXposureApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static final String APP_FOLDER = "MaxExposure";
    public static final String GIF_EXTENSION = ".gif";
    public static final String MP4_EXTENSION = ".mp4";
    public static final String LOGS_FILE = "Maxxposure.txt";

    public static String getAppFolderPath() {
        String path = null;
        try {
            String sdcard = MaxXposureApplication.appContext.getApplicationInfo().dataDir;
            path = sdcard + File.separator + APP_FOLDER + File.separator;
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    public static void saveBitmap(String fileName, Bitmap bitmap) {
        String filePath = getAppFolderPath() + fileName;
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveBitmapToSDCard(String fileName, Bitmap bitmap) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void clearFolder() {
        try {
            String path = getAppFolderPath();
            File dir = new File(path);
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(String path) {
        File file = new File(path);
        Bitmap bitmap = null;
        if (file.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(path, bmOptions);
        }
        return bitmap;

    }


    public static String getLogFilePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + LOGS_FILE;
    }

    public synchronized static void saveLogs(String log) {
        try {
            File f = new File(getLogFilePath());
            FileOutputStream fos = new FileOutputStream(f, true);
            fos.write(log.getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getRoundedBitmap(Bitmap srcBitmap, int cornerRadius) {
        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth(), // Width
                srcBitmap.getHeight(), // Height
                Bitmap.Config.ARGB_8888 // Config
        );
        Canvas canvas = new Canvas(dstBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        srcBitmap.recycle();
        return dstBitmap;
    }

    public static Object jsonToObject(String json, Class cls) {
        Gson gson = new Gson();
        Object object = gson.fromJson(json, cls);
        return object;
    }

    public static String getJsonFromObject(Object object) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(object);
        return jsonString;
    }

    public static int getOrientation(Uri photoUri) {
        Cursor cursor = MaxXposureApplication.appContext.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor == null || cursor.getCount() != 1) {
            return 90;  //Assuming it was taken portrait
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }


}
