package com.maxxposure.app.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;


import com.maxxposure.app.MaxXposureApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings("deprecation")
public class CameraController extends CameraControllerBase implements PreviewCallback {
    private Camera _camera;
    private SurfaceHolder _surfaceHolder;

    /**
     * Boolean indicating whether the app is running in continuous autofocus mode. In which case,
     * the sensor events are simply ignored.
     */
    private boolean _isContinuousAutofocusModeRunning = false;

    /**
     * This flag is required for devices on which Continous_Picture_Mode is supported (found in
     * supported mode list) but is not actually functional. (Samsung galaxy nexus)
     */
    private boolean _doNotUseContinuosPictureMode;

    /**
     * The callback buffer to be given to camera.
     */
    private byte[] _cameraPreviewBuffer;

    private Timer _checkContinuosPictureModeSupportedTimer;
    private CheckContinuosPictureModeSupportedTask _checkContinuosPictureModeSupportedTask;
    private final long CHECK_CONTINUOS_PICTURE_MODE_SUPPORTED_TIMEOUT = 5 * 1000;

    private AutoFocusMoveCallback _autofocusMoveCallback;


    @Override
    public boolean hasCamera() {
        boolean hasCamera = MaxXposureApplication.appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        return hasCamera;
    }

    @Override
    public int openCamera() {
        try {
            _camera = Camera.open(); // attempt to get a Camera instance
            setFocusListener();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void closeCamera() {
        try {
            /**
             * Cancel any pending autofocus cycles
             */
            _isContinuousAutofocusModeRunning = false;
            _camera.cancelAutoFocus();
            AccelerometerSensorManager.getInstance().setFocusListener(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            /**
             * Reset the preview callback so that no new frames will be received.
             */
            _camera.setPreviewCallback(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            /**
             * Stop the preview
             */
            _camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            /**
             * Release the camera
             */
            _camera.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _cancelCheckContinuosPictureModeSupportedTimer();
    }

    @Override
    public void setPreviewDisplay(Object holder) {
        try {
            _surfaceHolder = (SurfaceHolder) holder;
            _camera.setPreviewDisplay(_surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPreview() {
        try {
            _camera.setPreviewDisplay(_surfaceHolder);
            _camera.setPreviewCallbackWithBuffer(CameraController.this);
            _cameraPreviewBuffer = new byte[(int) (DEFAULT_PREVIEW_WIDTH * DEFAULT_PREVIEW_HEIGHT * 1.5)];
            _camera.addCallbackBuffer(_cameraPreviewBuffer);
            _camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPreview() {
        try {
            _camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOptimalPreviewSize() {
        Parameters params = _camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        for (Camera.Size cs : sizes) {
            if (cs.width == DEFAULT_PREVIEW_WIDTH && cs.height == DEFAULT_PREVIEW_HEIGHT || cs.width == DEFAULT_PREVIEW_HEIGHT && cs.height == DEFAULT_PREVIEW_WIDTH) {
                params.setPreviewSize(cs.width, cs.height);
                break;
            }
        }
        _camera.setParameters(params);
    }

    @Override
    public void setExposureParams() {
        Parameters params = _camera.getParameters();
        if (params.isAutoExposureLockSupported()) {
            params.setAutoExposureLock(false);
        }
        // params.setExposureCompensation( 0 );
        _camera.setParameters(params);
    }

    @Override
    public void setAutofocusMode() {
        Parameters params = _camera.getParameters();
        if (params.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_AUTO)) {
            params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        }
        _camera.setParameters(params);
    }

    @Override
    public void setDisplayOrientation() {
        Display display = ((WindowManager) MaxXposureApplication.appContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                _camera.setDisplayOrientation(90);
                break;

            case Surface.ROTATION_90:
            case Surface.ROTATION_180:
                break;

            case Surface.ROTATION_270:
                _camera.setDisplayOrientation(180);
                break;
        }
    }

    @Override
    public synchronized void autofocus() {
        if (_isContinuousAutofocusModeRunning) {
            /**
             * Ignore if continuous autofocus is running.
             */
            return;
        }
        if (!autofocusing) {
            try {
                /**
                 * This call is required to remove any pending autofocus callback events.
                 */
                _camera.cancelAutoFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                autofocusing = true;
                isFocused = false;
                startCancelAutofocusTimer();
                try {
                    /**
                     * Callback to be received when running in continuous autofocus mode.
                     */
                    _autofocusMoveCallback = new AutoFocusMoveCallback() {
                        @Override
                        public void onAutoFocusMoving(boolean start, Camera camera) {
                            _cancelCheckContinuosPictureModeSupportedTimer();
                        }
                    };

                    _camera.setAutoFocusMoveCallback(_autofocusMoveCallback);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                _camera.autoFocus(_autoFocusCallback);
            } catch (Exception e) {
                e.printStackTrace();
                autofocusing = false;
            }
        }
    }

    /**
     * Callback to be received when autofocus is complete
     */
    private AutoFocusCallback _autoFocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean autoFocusSuccess, Camera arg1) {
            autofocusing = false;
            isFocused = autoFocusSuccess;
            _cancelCheckContinuosPictureModeSupportedTimer();

            if (autoFocusSuccess && !_doNotUseContinuosPictureMode) {
                Parameters params = _camera.getParameters();
                if (params.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                _camera.setParameters(params);
                _isContinuousAutofocusModeRunning = true;
                AccelerometerSensorManager.getInstance().stop();
                try {
                    _autofocusMoveCallback = new AutoFocusMoveCallback() {
                        @Override
                        public void onAutoFocusMoving(boolean start, Camera camera) {
                            _cancelCheckContinuosPictureModeSupportedTimer();
                        }
                    };
                    _camera.setAutoFocusMoveCallback(_autofocusMoveCallback);
                } catch (Throwable e) {
                    _doNotUseContinuosPictureMode = true;
                    _isContinuousAutofocusModeRunning = false;
                }
                _startCheckContinuosPictureModeSupportedTimer();
            }
        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        previewBuffer = data;
        if (startCapture) {
            bitmaps.add(getCurrentPreviewBitmap());
        }
        _camera.addCallbackBuffer(_cameraPreviewBuffer);
    }

    @Override
    public int getPreviewWidth() {
        Parameters parameters = _camera.getParameters();
        return parameters.getPreviewSize().width;
    }

    @Override
    public int getPreviewHeight() {
        Parameters parameters = _camera.getParameters();
        return parameters.getPreviewSize().height;
    }

    @Override
    public int getPreviewFormat() {
        Parameters parameters = _camera.getParameters();
        return parameters.getPreviewFormat();
    }

    @Override
    public byte[] getCurrentPreviewBuffer() {
        byte data[] = new byte[previewBuffer.length];
        System.arraycopy(previewBuffer, 0, data, 0, previewBuffer.length);
        return data;
    }

    @Override
    public Bitmap getCurrentPreviewBitmap() {
        Bitmap bitmap = null;
        int previewFormat = getPreviewFormat();
        int width = getPreviewWidth();
        int height = getPreviewHeight();
        byte[] imageData = getCurrentPreviewBuffer();
        bitmap = ImageConverter.convertByteArrayToBitmap(imageData, previewFormat, width, height);
        return bitmap;
    }

    /**
     * This class extends {@link TimerTask} and is used to check if continuous autofocus mode is
     * actually supported on device. Since on some devices even if we get the continuous autofocus
     * mode in list of supported modes, it is not functional.
     *
     * @author Sayyad.abid
     */
    private class CheckContinuosPictureModeSupportedTask extends TimerTask {
        @Override
        public void run() {
            _doNotUseContinuosPictureMode = true;
            _isContinuousAutofocusModeRunning = false;
            setAutofocusMode();
            AccelerometerSensorManager.getInstance().start();
        }
    }

    /**
     * Method to start a timer to check if continuous autofocus mode is actually supported.
     */
    private void _startCheckContinuosPictureModeSupportedTimer() {
        _cancelCheckContinuosPictureModeSupportedTimer();
        try {
            _checkContinuosPictureModeSupportedTimer = new Timer();
            _checkContinuosPictureModeSupportedTask = new CheckContinuosPictureModeSupportedTask();
            _checkContinuosPictureModeSupportedTimer.schedule(_checkContinuosPictureModeSupportedTask, CHECK_CONTINUOS_PICTURE_MODE_SUPPORTED_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to cancel a previously set timer to check if continuous autofocus mode is actually
     * supported.
     */
    private void _cancelCheckContinuosPictureModeSupportedTimer() {
        if (_checkContinuosPictureModeSupportedTimer != null) {
            try {
                _checkContinuosPictureModeSupportedTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _checkContinuosPictureModeSupportedTimer = null;
        }

        if (_checkContinuosPictureModeSupportedTask != null) {
            try {
                _checkContinuosPictureModeSupportedTask.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            _checkContinuosPictureModeSupportedTask = null;
        }
    }
}
