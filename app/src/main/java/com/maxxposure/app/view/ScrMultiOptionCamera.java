package com.maxxposure.app.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.databinding.DataBindingUtil;

import com.maxxposure.app.R;
import com.maxxposure.app.databinding.ActivityScrMultiOptionCameraBinding;
import com.maxxposure.app.utils.FileUtils;
import com.maxxposure.app.utils.MovementDetector;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.otaliastudios.cameraview.controls.Grid;
import com.otaliastudios.cameraview.size.Size;
import com.otaliastudios.cameraview.size.SizeSelector;
import com.otaliastudios.cameraview.size.SizeSelectors;
import com.waynejo.androidndkgif.GifEncoder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScrMultiOptionCamera extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private ProgressDialog dlg = null;
    private Vibrator vibe;
    private File file;
    private CameraView camera;
    private static final String TAG = "CaptureWithVideo";
    private ActivityScrMultiOptionCameraBinding binding;
    private ArrayList<Bitmap> captures = new ArrayList<>();
    private int captureCount = 0;
    private static final int MAX_CAPTURE_COUNT = 6;
    public static final int BR_AUTO = 50;

    public static final int MAX_WIDTH = 1280;

    public Timer timer;
    private boolean isStabilizationEnabled = false;
    private boolean isVibrated = false;
    private AppCompatSeekBar sbBr;
    private CamListener camListener;
    private float minCompensationRange;
    private float maxCompensationRange;

    private final int BITMAP_WIDTH = 1920;
    private final int BITMAP_HEIGHT = 1080;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_multi_option_camera);
        camera = binding.cameraPreview;
        camListener = new CamListener();
        camera.addCameraListener(camListener);
        sbBr = binding.customSeekBar;
        binding.llBr.setOnClickListener(this);
        binding.llGridClick.setOnClickListener(this);
        sbBr.setProgress(BR_AUTO);
        sbBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int pr = seekBar.getProgress();
                setBrightness(pr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        initUI();
        initSensor();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);// vibrator initialization
        binding.cvBack.setOnClickListener(this);
        binding.ivFlash.setOnClickListener(this);
        binding.ivStabilization.setOnClickListener(this);


        binding.cvCapture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        binding.cvCapture.setCardBackgroundColor(getResources().getColor(R.color.capturePrimaryRed));
                        return true;

                    case MotionEvent.ACTION_UP:
                        String path = FileUtils.getAppFolderPath() + ScrImageListing.selectedOption;
                        file = new File(path);
                        binding.cvCapture.setCardBackgroundColor(getResources().getColor(R.color.capturePrimaryLight));
                        if (ScrImageListing.selectedOption <= 6) {
                            camera.takePicture();
                            dlg = new ProgressDialog(ScrMultiOptionCamera.this);
                            dlg.setMessage("capturing...");
                            dlg.show();
                        } else {
                            dlg = new ProgressDialog(ScrMultiOptionCamera.this);
                            dlg.setMessage("preparing...");
                            camera.takePicture();
                        }

                        return true;
                }
                return false;
            }
        });
    }

    public void initSensor() {
        MovementDetector.getInstance().addListener(new MovementDetector.Listener() {

            @SuppressLint("SwitchIntDef")
            @Override
            public void onMotionDetected(SensorEvent event, float acceleration) {

                Log.d(TAG, "onMotionDetected: Acceleration: [" + String.format("%.3f", event.values[0]) + "," + String.format("%.3f", event.values[1]) + "," + String.format("%.3f", event.values[2]) + "] " + String.format("%.3f", acceleration));
              /*  if (acceleration > SettingsHelper.getInstance().getMotionDetectionThreshold()){
                    mMotionDetectionTextView.setTextColor(Color.RED);
                } else {
                    mMotionDetectionTextView.setTextColor(Color.WHITE);
                }*/
                double x = event.values[0];
                double y = event.values[1];
                double angle = Math.atan2(x, y) / (Math.PI / 180);

                double ang = Math.abs(angle);

                if ((x <= 6 && x >= -6) && y > 5) {
                    // isLandscape = false;
                    binding.cvStabilizationLine.setCardBackgroundColor(getResources().getColor(R.color.red));
                    binding.cvStabilizationLine2.setCardBackgroundColor(getResources().getColor(R.color.red));
                    isVibrated = false;
                } else if (x >= 6 || x <= -6) {
                    // isLandscape = true;
                    binding.cvStabilizationLine.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    binding.cvStabilizationLine2.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    if (!isVibrated) {
                        isVibrated = true;
                        vibe.vibrate(100);
                    }
                }


                Log.d(TAG, "onMotionDetected: angle : " + angle);
            }
        });
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            Log.d("TIMER", "stopTimer: timer stopped.");
        }
        Log.d("TIMER", "stopTimer: end line");
    }


    @Override
    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                finish();
                break;

            case R.id.iv_flash:
                try {
                    turnOnFlashLight();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onClick: Flash Exception : " + e.getMessage());
                }
                break;

            case R.id.iv_stabilization:
                if (!isStabilizationEnabled) {
                    isStabilizationEnabled = true;
                    MovementDetector.getInstance().start();
                    binding.cvStabilizationLine.setVisibility(View.VISIBLE);
                    binding.cvStabilizationLine2.setVisibility(View.VISIBLE);

                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(1000); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    binding.cvStabilizationLine.startAnimation(anim);
                    binding.cvStabilizationLine2.startAnimation(anim);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivStabilization.setImageDrawable(getResources().getDrawable(R.drawable.ic_chaand_border_primary, getApplicationContext().getTheme()));
                    } else {
                        binding.ivStabilization.setImageDrawable(getResources().getDrawable(R.drawable.ic_chaand_border_primary));
                    }

                } else {
                    isStabilizationEnabled = false;
                    MovementDetector.getInstance().stop();
                    binding.cvStabilizationLine.clearAnimation();
                    binding.cvStabilizationLine.setVisibility(View.GONE);
                    binding.cvStabilizationLine2.clearAnimation();
                    binding.cvStabilizationLine2.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.ivStabilization.setImageDrawable(getResources().getDrawable(R.drawable.ic_chaand_border, getApplicationContext().getTheme()));
                    } else {
                        binding.ivStabilization.setImageDrawable(getResources().getDrawable(R.drawable.ic_chaand_border));
                    }
                }
                break;

            case R.id.ll_br:
                if (binding.rlBrigthness.getVisibility() == View.VISIBLE) {
                    binding.ivBr.setImageResource(R.drawable.ic_slider_border);
                    binding.rlBrigthness.setVisibility(View.GONE);
                } else {
                    binding.ivBr.setImageResource(R.drawable.ic_br_selected);
                    binding.rlBrigthness.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.ll_grid_click:
                if (camera.getGrid() == Grid.DRAW_4X4) {
                    camera.setGrid(Grid.OFF);
                    binding.ivGrid.setImageDrawable(getDrawable(R.drawable.ic_grid_menu_border));
                } else {
                    camera.setGrid(Grid.DRAW_4X4);
                    binding.ivGrid.setImageDrawable(getDrawable(R.drawable.ic_grid_green));
                }


                break;
        }
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }


    @Override
    protected void onPause() {
        Log.e("tag", "onPause");
        //closeCamera();
        if (isStabilizationEnabled) {
            MovementDetector.getInstance().stop();
        }
        camera.close();
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            camera.setPreviewStreamSize(SizeSelectors.and(SizeSelectors.maxWidth(MAX_WIDTH), SizeSelectors.biggest()));
            camera.open();
        } catch (Exception e) {
            FileUtils.saveLogs(e.getMessage());
        }
    }

    public void setBrightness(int value) {
        try {
            minCompensationRange = camera.getCameraOptions().getExposureCorrectionMinValue();
            maxCompensationRange = camera.getCameraOptions().getExposureCorrectionMaxValue();
            int brightness = (int) (minCompensationRange + (maxCompensationRange - minCompensationRange) * (value / 100f));
            camera.setExposureCorrection(brightness);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOnFlashLight() {
        try {
            Flash flash = camera.getFlash();
            if (flash == Flash.OFF) {
                camera.setFlash(Flash.TORCH);
            } else {
                camera.setFlash(Flash.OFF);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CamListener extends CameraListener {

        @Override
        public void onPictureTaken(@NonNull @NotNull PictureResult result) {
            super.onPictureTaken(result);
            if (result != null) {
                result.toBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, new BitmapCallback() {
                    @Override
                    public void onBitmapReady(@Nullable @org.jetbrains.annotations.Nullable Bitmap bitmap) {
                        if (ScrImageListing.selectedOption <= 6) {
                            dlg.dismiss();
                            ScrImageListing.selectedBitmap = bitmap;
                            FileUtils.saveBitmap(file.getName() + ".jpeg", bitmap);
                            finish();
                            Intent intent = new Intent(ScrMultiOptionCamera.this, ScrImagePreview.class);
                            intent.putExtra("bWidth", bitmap.getWidth());
                            intent.putExtra("bHeight", bitmap.getHeight());
                            startActivity(intent);
                        } else {
                            captureCount++;
                            binding.tvCount.setText(String.valueOf(captureCount));
                            //  Bitmap rBitmap = FileUtils.getRoundedBitmap(bitmap, 20);
                            FileUtils.saveBitmap(file.getName(), bitmap);
                            captures.add(bitmap);
                            if (captureCount == MAX_CAPTURE_COUNT) {
                                dlg.show();
                                try {
                                    String path = FileUtils.getAppFolderPath() + String.valueOf(ScrImageListing.selectedOption) + FileUtils.GIF_EXTENSION;
                                    GifEncoder gifEncoder = new GifEncoder();
                                    try {
                                        gifEncoder.init(bitmap.getWidth(), bitmap.getHeight(), path, GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                                        for (Bitmap bitmap1 : captures) {
                                            if (bitmap1 != null)
                                                gifEncoder.encodeFrame(bitmap1, 300);
                                        }
                                        gifEncoder.close();
                                        try {
                                            String imgPath = FileUtils.getAppFolderPath() + String.valueOf(ScrImageListing.selectedOption);
                                            File f = new File(imgPath);
                                            f.delete();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        dlg.dismiss();


                                        Intent intent = getIntent();
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                camera.takePicture();
                            }

                        }
                    }
                });
            }

        }

        @Override
        public void onCameraOpened(@NonNull @NotNull CameraOptions options) {
            super.onCameraOpened(options);
            sbBr.setMax(100);
            setBrightness(BR_AUTO);
        }

        @Override
        public void onCameraError(@NonNull @NotNull CameraException exception) {
            super.onCameraError(exception);
            FileUtils.saveLogs(exception.getMessage());
        }
    }
}