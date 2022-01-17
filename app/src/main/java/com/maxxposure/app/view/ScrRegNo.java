package com.maxxposure.app.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.maxxposure.app.R;
import com.maxxposure.app.databinding.ActivityScrRegNoBinding;
import com.maxxposure.app.sharedpref.VehicleFormData;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.utils.FileUtils;
import com.maxxposure.app.utils.MyDetector;

import java.io.IOException;

public class ScrRegNo extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private String TAG = ScrRegNo.class.getName();
    private ActivityScrRegNoBinding binding;
    private CameraSource mCameraSource;
    private static final int requestPermissionID = 101;
    private boolean isScanning = false;
    private int screenWidth = 0;
    private int screenHeight = 0;
    public static double scaleX = 0.0;
    public static double scaleY = 0.0;
    int rect[] = new int[4];
    MyDetector detector = null;


    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().setStatusBarColor(Color.WHITE);
            }
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_reg_no);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        binding.cvProceed.setOnClickListener(this);
        binding.cvBack.setOnClickListener(this);
        binding.cvStart.setOnClickListener(this);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        scaleX = 720f / screenWidth;
        scaleY = 1280f / screenHeight;


    }

    private void initBoxes() {
        binding.ivCamCorners.post(new Runnable() {
            @Override
            public void run() {

                binding.ivCamCorners.getLocationOnScreen(rect);
                rect[2] = screenWidth;
                rect[3] = rect[1] + binding.ivCamCorners.getMeasuredHeight();
                detector.setBoxSize(binding.ivCamCorners.getMeasuredWidth(), binding.ivCamCorners.getMeasuredHeight());
            }
        });

    }


    private void startCameraSource() {
        mCameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 720)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build();
        /**
         * Add call back to SurfaceView and check if camera permission is granted.
         * If permission is granted we can start our cameraSource and pass it to surfaceView
         */

        binding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {

                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ScrRegNo.this,
                                new String[]{Manifest.permission.CAMERA},
                                requestPermissionID);
                        return;
                    }
                    mCameraSource.start(binding.surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                    FileUtils.saveLogs(e.getMessage());
                    Log.d(TAG, "surfaceCreated: Exception : " + e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });

    }


    private void startDetector() {
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        detector = new MyDetector(textRecognizer, rect);
        if (!detector.isOperational()) {
            Log.d(TAG, "startCameraSource: ");
            Log.d(TAG, "Detector dependencies not loaded yet");
            return;
        }
        detector.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> items = detections.getDetectedItems();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock item = items.valueAt(i);
                    if (item != null && item.getValue() != null) {
                        stringBuilder.append(item.getValue() + " ");
                    }
                }

                final String fullText = stringBuilder.toString();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (isScanning) {
                            binding.etRecognizedText.setText(fullText);
                        }
                    }
                });

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                finish();
                break;

            case R.id.cv_proceed:
                String reg_no = binding.etRecognizedText.getText().toString();
                if (TextUtils.isEmpty(reg_no)) {
                    CustomToast.showToast(ScrRegNo.this, "Please scan reg. no.");
                } else {
                    mCameraSource.stop();
                    showDialogForConfirmation();
                }
                break;

            case R.id.cv_start:
                if (!isScanning) {
                    isScanning = true;
                    binding.tvScanning.setText("Stop Scanning");
                } else {
                    isScanning = false;
                    binding.tvScanning.setText("Start Scanning");
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(VehicleFormData.getInstance().getREG_NO())) {
            binding.etRecognizedText.setText(VehicleFormData.getInstance().getREG_NO());
            showDialogForConfirmation();
        }
        initBoxes();
        try {
            startDetector();
            startCameraSource();
        } catch (Exception e) {
            FileUtils.saveLogs(e.getMessage());
        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mCameraSource.stop();
            mCameraSource.release();
            detector.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialogForConfirmation() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dlg_confirmation_vehicle_number);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView tv_change = dialog.findViewById(R.id.tv_change);
        TextView tv_agree_check = dialog.findViewById(R.id.tv_agree_check);
        CheckBox cb_terms = dialog.findViewById(R.id.cb_terms);
        CardView cv_proceed = dialog.findViewById(R.id.cv_proceed);
        TextView tv_reg_no = dialog.findViewById(R.id.tv_reg_no);
        if (!TextUtils.isEmpty(VehicleFormData.getInstance().getREG_NO())) {
            tv_reg_no.setText(VehicleFormData.getInstance().getREG_NO());
            cb_terms.setChecked(true);
        } else {
            tv_reg_no.setText(binding.etRecognizedText.getText().toString() + "");
        }

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehicleFormData.getInstance().setREG_NO(null);
                dialog.dismiss();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        tv_agree_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_terms.isChecked()) {
                    cb_terms.setChecked(false);
                } else {
                    cb_terms.setChecked(true);
                }
            }
        });

        cv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_terms.isChecked()) {
                    dialog.dismiss();
                    VehicleFormData.getInstance().setREG_NO(binding.etRecognizedText.getText().toString() + "");
                    CustomIntent.startActivity(ScrRegNo.this, ScrVinNo.class, false);
                } else {
                    CustomToast.showToast(ScrRegNo.this, "Please agree terms and condition.");
                }
            }
        });


        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == 123) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(ScrRegNo.this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}