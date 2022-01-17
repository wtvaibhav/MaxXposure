package com.maxxposure.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maxxposure.app.MaxXposureApplication;
import com.maxxposure.app.R;
import com.maxxposure.app.communication.ApiInterface;
import com.maxxposure.app.communication.Constants;
import com.maxxposure.app.communication.RetrofitBase;
import com.maxxposure.app.databinding.ActivityScrImageListBinding;
import com.maxxposure.app.model.ImageTypeStill;
import com.maxxposure.app.model.PostVehicleData;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.sharedpref.VehicleFormData;
import com.maxxposure.app.utils.CustomDialog;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.utils.FileUtils;
import com.maxxposure.app.utils.GIFView;
import com.maxxposure.app.utils.aws.AWSKeys;
import com.maxxposure.app.utils.aws.S3Uploader;
import com.maxxposure.app.view.adapter.BrandAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrImageListing extends AppCompatActivity implements View.OnClickListener, S3Uploader.S3UploadInterface {

    private String TAG = ScrImageListing.class.getName();

    private ApiInterface apiInterface;

    private ActivityScrImageListBinding binding;
    //private UiBackButtonBinding backButtonBinding;
    private BrandAdapter adapter;


    public static final int INTERIOR_FIRST = 7;
    public static final int INTERIOR_SECOND = 8;
    public static final int INTERIOR_THIRD = 9;
    public static final int INTERIOR_FOURTH = 10;


    public static Bitmap selectedBitmap = null;

    public static int selectedOption = -1;
    private ArrayList<GIFView> interiors = new ArrayList<>();

    private ArrayList<String> s3Urls = new ArrayList<>();
    private LayoutInflater inflater;

    //s3 uploader
    private S3Uploader s3Uploader;

    private int count = 0;
    private boolean isGifStarted = false;
    private int totalCount = 0;
    String files[] = null;
    private String baseFolderPath = FileUtils.getAppFolderPath();
    private LinearLayout llStill;

    private CardView cvSelectedCard;

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
        apiInterface = RetrofitBase.getInstance().create(ApiInterface.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_image_list);
        llStill = binding.llStill;
        inflater = getLayoutInflater();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        MaxXposureApplication.SC_WIDTH = metrics.widthPixels;
        MaxXposureApplication.SC_HEIGHT = metrics.heightPixels;
        s3Uploader = new S3Uploader(ScrImageListing.this);
        s3Uploader.s3UploadInterface = this;


        interiors.add(binding.ivInterior1);
        interiors.add(binding.ivInterior2);
        interiors.add(binding.ivInterior3);
        interiors.add(binding.ivInterior4);
        initUI();
        try {
            initStillImages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initStillImages() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ImageTypeStill> imageTypeStills = VehicleFormData.getInstance().getImageTypeStills(VehicleFormData.getInstance().getBRAND_NAME_ID());
                if (imageTypeStills == null || imageTypeStills.size() == 0) {
                    return;
                }
                int size = imageTypeStills.size();
                int rows = size / 2;
                if (size % 2 > 0) {
                    rows = rows + 1;
                }
                llStill.removeAllViews();
                for (int i = 0; i < rows; i++) {
                    int pos = i * 2;
                    View view = inflater.inflate(R.layout.still_image_row, null);
                    if (pos < size) {
                        ImageTypeStill imageTypeStill = imageTypeStills.get(pos);
                        imageTypeStill.setType(pos + 1);
                        CardView cardFirst = view.findViewById(R.id.cv_first);
                        cardFirst.setVisibility(View.VISIBLE);
                        cardFirst.setTag(imageTypeStill);
                        int finalPos = pos;
                        selectedOption = finalPos + 1;
                        String path = baseFolderPath + selectedOption + ".png";
                        ImageView ivImage = cardFirst.findViewById(R.id.iv_slide_first);
                        LinearLayout ll_remove = cardFirst.findViewById(R.id.ll_remove);
                        LinearLayout ll_add = cardFirst.findViewById(R.id.ll_add);
                        CardView cv_remove = cardFirst.findViewById(R.id.cv_remove);
                        ImageView ivSide = cardFirst.findViewById(R.id.iv_add_side_view);
                        cv_remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectedBitmap = null;
                                ll_remove.setVisibility(View.GONE);
                                ll_add.setVisibility(View.VISIBLE);
                                ivImage.setImageResource(R.drawable.ic_side_view);
                                ImageView ivSide = cardFirst.findViewById(R.id.iv_add_side_view);
                                File file = new File(path);
                                if (file.exists()) {
                                    file.delete();
                                    ivSide.setImageResource(R.drawable.ic_plus_big);
                                    cardFirst.invalidate();
                                }

                            }
                        });
                        File f = new File(path);
                        if (f.exists()) {
                            ivImage.setImageBitmap(FileUtils.getBitmap(path));
                            ll_remove.setVisibility(View.VISIBLE);
                            ll_add.setVisibility(View.GONE);
                        } else {
                            ll_remove.setVisibility(View.GONE);
                            ll_add.setVisibility(View.VISIBLE);
                            ivSide.setImageResource(R.drawable.ic_plus_big);
                        }
                        cardFirst.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectedOption = finalPos + 1;
                                cvSelectedCard = (CardView) view;
                                String path = baseFolderPath + selectedOption + ".png";
                                File f = new File(path);
                                if (f.exists()) {
                                    Intent intent = new Intent(ScrImageListing.this,
                                            ScrImagePreview.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(ScrImageListing.this,
                                            ScrMultiOptionCamera.class);
                                    startActivityForResult(intent, selectedOption);
                                }
                            }
                        });
                    }
                    llStill.addView(view);
                    pos = pos + 1;
                    if (pos < size) {
                        ImageTypeStill imageTypeStill = imageTypeStills.get(pos);
                        imageTypeStill.setType(pos + 1);
                        CardView cardSecond = view.findViewById(R.id.cv_second);
                        cardSecond.setVisibility(View.VISIBLE);
                        cardSecond.setTag(imageTypeStill);
                        int finalPos1 = pos;
                        selectedOption = finalPos1 + 1;
                        String path = baseFolderPath + selectedOption + ".png";
                        ImageView ivImage = cardSecond.findViewById(R.id.iv_slide_second);
                        LinearLayout ll_remove = cardSecond.findViewById(R.id.ll_remove_second);
                        LinearLayout ll_add = cardSecond.findViewById(R.id.ll_add_second);
                        CardView cv_remove = cardSecond.findViewById(R.id.cv_remove_second);
                        ImageView ivSide = cardSecond.findViewById(R.id.iv_add_side_view);
                        cv_remove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cv_remove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ll_remove.setVisibility(View.GONE);
                                        ll_add.setVisibility(View.VISIBLE);
                                        ivImage.setImageResource(R.drawable.ic_side_view);
                                        ImageView ivSide = cardSecond.findViewById(R.id.iv_add_side_view);
                                        File file = new File(path);
                                        if (file.exists()) {
                                            file.delete();
                                            ivSide.setImageResource(R.drawable.ic_plus_big);
                                            cardSecond.invalidate();
                                        }

                                    }
                                });
                            }
                        });
                        File f = new File(path);
                        if (f.exists()) {
                            ivImage.setImageBitmap(FileUtils.getBitmap(path));
                            ll_remove.setVisibility(View.VISIBLE);
                            ll_add.setVisibility(View.GONE);
                        } else {
                            ll_remove.setVisibility(View.GONE);
                            ll_add.setVisibility(View.VISIBLE);
                            ivSide.setImageResource(R.drawable.ic_plus_big);
                        }
                        cardSecond.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cvSelectedCard = (CardView) view;
                                selectedOption = finalPos1 + 1;
                                String path = baseFolderPath + selectedOption + ".png";
                                File f = new File(path);
                                if (f.exists()) {
                                    Intent intent = new Intent(ScrImageListing.this,
                                            ScrImagePreview.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(ScrImageListing.this,
                                            ScrMultiOptionCamera.class);
                                    startActivityForResult(intent, selectedOption);
                                }
                            }
                        });
                    }


                }


            }
        });

    }

    public void initUI() {
        binding.cvBack.setOnClickListener(this);
        binding.cvProceed.setOnClickListener(this);
        binding.cvInterior1.setOnClickListener(this);
        binding.cvInterior2.setOnClickListener(this);
        binding.cvInterior3.setOnClickListener(this);
        binding.cvInterior4.setOnClickListener(this);

        binding.btSave.setOnClickListener(this);


    }

    private void initSlides() {
        int iNo = 0;
        for (int i = 7; i <= 10; i++) {
            String path = FileUtils.getAppFolderPath() + i + ".gif";
            File file = new File(path);
            if (file.exists()) {
                interiors.get(iNo).loadGIFAsset(this, path);
            }
            iNo++;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initSlides();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.cv_back:
                finish();
                break;
            case R.id.cv_proceed:
                File file = new File(FileUtils.getAppFolderPath());
                files = file.list();
                totalCount = files.length;
                if (totalCount > 0) {
                    for (int i = 0; i < totalCount; i++) {
                        String path = AWSKeys.IMG_BASE + "" + files[i];
                        s3Urls.add(path);
                    }

                    CustomDialog.showDialog(ScrImageListing.this, Constants.PROGRESS);
                    String path = FileUtils.getAppFolderPath() + files[count];
                    s3Uploader.initUpload(path);

                } else {
                    CustomToast.showToast(ScrImageListing.this, "Please Select Images");
                }
                break;


            case R.id.cv_interior1:
                selectedOption = INTERIOR_FIRST;
                intent = new Intent(this,
                        ScrMultiOptionCamera.class);
                startActivityForResult(intent, INTERIOR_FIRST);
                break;

            case R.id.cv_interior2:
                selectedOption = INTERIOR_SECOND;
                intent = new Intent(this,
                        ScrMultiOptionCamera.class);
                startActivityForResult(intent, INTERIOR_SECOND);
                break;

            case R.id.cv_interior3:
                selectedOption = INTERIOR_THIRD;
                intent = new Intent(this,
                        ScrMultiOptionCamera.class);
                startActivityForResult(intent, INTERIOR_THIRD);
                break;

            case R.id.cv_interior4:
                selectedOption = INTERIOR_FOURTH;
                intent = new Intent(this,
                        ScrMultiOptionCamera.class);
                startActivityForResult(intent, INTERIOR_FOURTH);
                break;

            case R.id.bt_save:
                UserData.getInstance().saveImageData(true);
                CustomToast.showToast(this, "Data saved");
                break;
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView ivImage = cvSelectedCard.findViewById(R.id.iv_slide_first);
        LinearLayout ll_remove = cvSelectedCard.findViewById(R.id.ll_remove);
        LinearLayout ll_add = cvSelectedCard.findViewById(R.id.ll_add);
        if (ivImage == null) {
            ivImage = cvSelectedCard.findViewById(R.id.iv_slide_second);
            ll_remove = cvSelectedCard.findViewById(R.id.ll_remove_second);
            ll_add = cvSelectedCard.findViewById(R.id.ll_add_second);
        }
        if (selectedBitmap != null) {
            ivImage.setImageBitmap(selectedBitmap);
            ll_remove.setVisibility(View.VISIBLE);
            ll_add.setVisibility(View.GONE);
        } else {
            ll_remove.setVisibility(View.GONE);
            ll_add.setVisibility(View.VISIBLE);
            ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_side_view));
        }
        cvSelectedCard.invalidate();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!UserData.getInstance().isSavedImageData()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // FileUtils.clearFolder();
                }
            }).start();
        }

    }

    @Override
    public void onUploadSuccess(String response) {
        Log.d(TAG, "onUploadSuccess: " + response);
        count++;
        if (count < totalCount) {
            String path = FileUtils.getAppFolderPath() + files[count];
            s3Uploader.initUpload(path);
        } else if (count == totalCount) {
            PostVehicleData data = new PostVehicleData();
            ArrayList<PostVehicleData.UserSpinImage> userSpinImages = new ArrayList<>();
            ArrayList<PostVehicleData.UserStillImage> userStillImages = new ArrayList<>();
            for (int i = 0; i < s3Urls.size(); i++) {
                String url = s3Urls.get(i);
                String name = url.substring(url.lastIndexOf("/") + 1);

                if (url.toLowerCase().contains("png")||url.toLowerCase().contains("png")) {
                    PostVehicleData.UserStillImage userStillImage = new PostVehicleData.UserStillImage();
                    userStillImage.setImageUrl(url);
                    userStillImages.add(userStillImage);

                } else {
                    PostVehicleData.UserSpinImage userSpinImage = new PostVehicleData.UserSpinImage();
                    userSpinImage.setImageUrl(url);
                    userSpinImages.add(userSpinImage);
                }
            }
            data.setUserSpinImage(userSpinImages);
            data.setUserStillImage(userStillImages);
            data.setVehicleRegNumber(VehicleFormData.getInstance().getREG_NO());
            data.setVehicleStatus("In-Progress");
            data.setVehicleVINNumber(VehicleFormData.getInstance().getVIN_NO());
            data.setVehicleWorkFlowImageUrl(VehicleFormData.getInstance().getBRAND_NAME_IMAGE());

            String post = FileUtils.getJsonFromObject(data);
            call_PostVehicleAPi(post);
        }

    }

    @Override
    public void onUploadError(String response) {
        Log.d(TAG, "onUploadError: " + response);
    }


    public void call_PostVehicleAPi(String body) {
        CustomDialog.showDialog(ScrImageListing.this, Constants.PROGRESS);

        JSONObject object = null;
        JsonObject gsonObject = null;
        try {
            object = new JSONObject(body);
            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "call_PostVehicleAPi: body : " + gsonObject.toString());

        Call<String> call = apiInterface.registerVehicle("Bearer " + UserData.getInstance().getTOKEN(), gsonObject, UserData.getInstance().getUSER_ID());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                CustomDialog.closeDialog(ScrImageListing.this);

                try {
                    Log.d(TAG, "onResponse: call_PostVehicleAPi code : " + response.code());
                    Log.d(TAG, "onResponse: call_PostVehicleAPi : " + response.body());
                    Log.d(TAG, "onResponse: call_PostVehicleAPi url : " + call.request().url().toString());

                    JSONObject object;
                    switch (response.code()) {
                        case 200:
                            if (!response.isSuccessful()) {
                                return;
                            }
                            object = new JSONObject(response.body().toString());
                            String message = object.getString("message");
                            CustomToast.showToast(ScrImageListing.this, message);
                            CustomIntent.startActivity(ScrImageListing.this, ScrNavigation2.class);
                            break;

                        case 400:
                            CustomToast.showToast(ScrImageListing.this, "Something went wrong");
                            break;

                        default:
                            CustomToast.showToast(ScrImageListing.this, "Something went wrong please try again later.");
                            break;
                    }

                } catch (JSONException exception) {
                    exception.getMessage();
                    Log.d(TAG, "onResponse: JSON Exception : " + exception.getMessage());
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                CustomDialog.closeDialog(ScrImageListing.this);
            }
        });
    }
}