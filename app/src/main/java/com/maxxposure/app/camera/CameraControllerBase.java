package com.maxxposure.app.camera;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class CameraControllerBase implements IFocusListener {
    public abstract boolean hasCamera();

    public abstract int openCamera();


    public abstract void closeCamera();

    public abstract void setPreviewDisplay(Object holder);

    public abstract void startPreview();

    public abstract void stopPreview();

    public abstract void setOptimalPreviewSize();

    public abstract void setExposureParams();

    public abstract void setAutofocusMode();

    public abstract void setDisplayOrientation();

    public abstract void autofocus();

    public abstract int getPreviewWidth();

    public abstract int getPreviewHeight();

    public abstract int getPreviewFormat();

    public abstract byte[] getCurrentPreviewBuffer();

    public abstract Bitmap getCurrentPreviewBitmap();


    public static final int DEFAULT_PREVIEW_WIDTH = 720;
    public static final int DEFAULT_PREVIEW_HEIGHT = 480;

    /**
     * Boolean indicating whether autofocus is in progress
     */
    protected boolean autofocusing = false;

    /**
     * Boolean indicating whether the camera is focused
     */
    protected boolean isFocused = false;

    /**
     * The last focused timestamp. Used to avoid very frequent autofocus attempts
     */
    protected long lastFocusedTimestamp;

    protected final long CANCEL_AUTOFOCUS_TIMEOUT = 3 * 1000;

    protected byte[] previewBuffer;


    private Timer _cancelAutofocusTimer;
    private CancelAutoFocusTask _cancelAutofocusTask;

    public void setFocusListener() {
        AccelerometerSensorManager.getInstance().setFocusListener(this);
    }

    private class CancelAutoFocusTask extends TimerTask {
        @Override
        public void run() {
            autofocusing = false;
        }
    }

    protected void startCancelAutofocusTimer() {
        cancelAutofocusTimer();
        try {
            _cancelAutofocusTimer = new Timer();
            _cancelAutofocusTask = new CancelAutoFocusTask();
            _cancelAutofocusTimer.schedule(_cancelAutofocusTask, CANCEL_AUTOFOCUS_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to cancel the previously set timer.
     */
    protected void cancelAutofocusTimer() {
        if (_cancelAutofocusTimer != null) {
            try {
                _cancelAutofocusTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _cancelAutofocusTimer = null;
        }

        if (_cancelAutofocusTask != null) {
            try {
                _cancelAutofocusTask.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _cancelAutofocusTask = null;
        }
    }


    @Override
    public void focusChanged() {
        if (!autofocusing) {
            autofocus();
        }
    }

    public ArrayList<Bitmap> bitmaps = new ArrayList<>();

    public boolean startCapture = false;


    public void setStartCapture(boolean start) {
        startCapture = start;
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }


}
