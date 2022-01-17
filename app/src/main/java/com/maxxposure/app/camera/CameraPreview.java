package com.maxxposure.app.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder _surfaceHolder;


    private final CameraControllerBase _cameraController;

    /**
     * Parameterized Constructor.
     *
     * @param context          the Activity context
     * @param cameraController the {@link CameraControllerBase} instance
     */
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, CameraControllerBase cameraController) {
        super(context);

        _cameraController = cameraController;


        /**
         * Add a SurfaceHolder.Callback to get notified when the underlying surface is created,
         * changed and destroyed.
         */
        _surfaceHolder = getHolder();
        _surfaceHolder.addCallback(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            /**
             * deprecated setting, but required on Android versions prior to 3.0
             */
            _surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        /**
         * The Surface has been created, now tell the camera where to draw the preview.
         */
        _cameraController.setPreviewDisplay(_surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        /**
         * If your preview can change or rotate, take care of those events here. Make sure to stop
         * the preview before resizing or reformatting it.
         */
        if (_surfaceHolder.getSurface() == null) {
            /**
             * Preview surface does not exist
             */
            return;
        }

        /**
         * Stop preview before making changes
         */
        _cameraController.stopPreview();

        /**
         * Set preview size and make any resize, rotate or reformatting changes here and start
         * preview with new settings
         */
        _cameraController.setOptimalPreviewSize();

        _cameraController.setExposureParams();

        _cameraController.setAutofocusMode();

        _cameraController.setDisplayOrientation();

        _cameraController.startPreview();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 * So we get the autofocus when starting up - we do this on a delay, as calling it
                 * immediately means the autofocus doesn't seem to work properly sometimes (at least
                 * on Galaxy Nexus)
                 */
                _cameraController.autofocus();
            }
        }, 1000);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}