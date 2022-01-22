package com.maxxposure.app.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.maxxposure.app.R;
import com.maxxposure.app.databinding.ActivityScrVinNoBinding;
import com.maxxposure.app.sharedpref.VehicleFormData;
import com.maxxposure.app.utils.BitMapRotation;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.utils.FileUtils;
import com.maxxposure.app.view.custom.SupportBottomSheetDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScrVinNo extends AppCompatActivity implements View.OnClickListener, SupportBottomSheetDialog.BottomSheetListener {

    private String TAG = ScrVinNo.class.getName();
    private ActivityScrVinNoBinding binding;

    String[] permissionArrays = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final int RequestPermissionCode = 1;
    public static final int RequestPermissionCode2 = 2;
    public static final int REQUEST_IMAGE = 100;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());

    private String x;
    private Bitmap bitmap_user = null;
    private Uri imageUri_user;
    private String filePath;
    private String url;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_vin_no);
        initUI();
    }

    private void initUI() {
        binding.etVinNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String vin_no = s.toString();
                VehicleFormData.getInstance().setVIN_NO(vin_no);
            }
        });

        binding.cvBack.setOnClickListener(this);
        binding.cvProceed.setOnClickListener(this);

        binding.llAddVinNo.setOnClickListener(this);
        binding.ivVin.setOnClickListener(this);
        getSavedData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");

    }

    public void getSavedData() {
        Log.d(TAG, "getSavedData: called");
        if (!TextUtils.isEmpty(VehicleFormData.getInstance().getVIN_IMAGE_PATH())) {
            String image_path = VehicleFormData.getInstance().getVIN_IMAGE_PATH();
            filePath = image_path;
            File imgFile = new File(image_path);

            if (imgFile.exists()) {
                bitmap_user = BitmapFactory.decodeFile(imgFile.getAbsoluteFile().getPath());
                binding.llAddVinNo.setVisibility(View.GONE);
                binding.ivVin.setVisibility(View.VISIBLE);
                binding.ivVin.setImageBitmap(bitmap_user);
            } else {
                binding.llAddVinNo.setVisibility(View.VISIBLE);
                binding.ivVin.setVisibility(View.GONE);
            }
        } else {
            binding.llAddVinNo.setVisibility(View.VISIBLE);
            binding.ivVin.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(VehicleFormData.getInstance().getVIN_NO())) {
            String vin_no = VehicleFormData.getInstance().getVIN_NO();
            binding.etVinNo.setText(vin_no);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_proceed:
                String vin_no = binding.etVinNo.getText().toString();
                if (bitmap_user == null) {
                    CustomToast.showToast(ScrVinNo.this, "Please select vin image");
                    return;
                } else if (TextUtils.isEmpty(vin_no)) {
                    CustomToast.showToast(ScrVinNo.this, "Please enter vin number");
                    return;
                }
                CustomIntent.startActivity(ScrVinNo.this, ScrChooseWorkFlow.class, false);
                break;


            case R.id.cv_back:
                finish();
                break;

            case R.id.iv_vin:
            case R.id.ll_add_vin_no:
                SupportBottomSheetDialog bottomSheet1 = new SupportBottomSheetDialog(this);
                bottomSheet1.show(getSupportFragmentManager(), "user_profile");
                break;
        }
    }

    @Override
    public void onButtonClicked(int button_type) {
        switch (button_type) {
            case 1:
                x = "1";
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissionArrays, RequestPermissionCode);
                        Log.d("TAG", "onClick: camera permission called");
                    }
                } else {
                    launchCameraIntent();
                }
                break;

            case 2:
                x = "2";
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(permissionArrays, RequestPermissionCode2);
                        Log.d("TAG", "onClick: stoage permission called");
                    }
                } else {
                    launchGalleryIntent();
                }
                break;
        }
    }

    private void launchCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: on start of onactivityresult");
        Log.d("TAG", "onActivityResult: data : " + data);
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            assert data != null;
            Log.d("TAG", "onActivityResult: x = " + x);
            assert x != null;
            switch (x) {
                case "1":
                    Log.d("TAG", "onActivityResult: inside case 1 ");
                    String url1 = null;
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    bitmap_user = photo;
                    filePath = FileUtils.getAppFolderPath()+currentDateandTime+ "vin.jpeg";
                    try {
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    VehicleFormData.getInstance().setVIN_IMAGE_PATH(filePath);
                    binding.llAddVinNo.setVisibility(View.GONE);
                    binding.ivVin.setVisibility(View.VISIBLE);
                    binding.ivVin.setImageBitmap(photo);
                    break;

                case "2":
                    Log.d("TAG", "onActivityResult: inside case 2 ");
                    final Uri imageUri3 = data.getData();
                    final InputStream imageStream3;
                    try {
                        photo = null;
                        imageStream3 = this.getContentResolver().openInputStream(imageUri3);
                        bitmap_user = BitmapFactory.decodeStream(imageStream3);
                        filePath = FileUtils.getAppFolderPath()+currentDateandTime+ "vin.jpg";
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                int orientation3 = FileUtils.getOrientation(imageUri3);
                                bitmap_user = BitMapRotation.rotateBitmap(bitmap_user, orientation3);
                                bitmap_user.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
                                Log.d("TAG", "onActivityResult: gallery licence front N+");
                            } else {
                                Log.d("TAG", "onActivityResult: gallery licence front N-");
                            }
                            VehicleFormData.getInstance().setVIN_IMAGE_PATH(filePath);
                            binding.llAddVinNo.setVisibility(View.GONE);
                            binding.ivVin.setVisibility(View.VISIBLE);
                            binding.ivVin.setImageBitmap(bitmap_user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

            }
            Log.d("TAG", "onActivityResult: file_path : " + filePath);
        }
        Log.d("TAG", "onActivityResult: on end of onactivityresult");
    }


}