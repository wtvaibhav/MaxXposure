package com.maxxposure.app.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.maxxposure.app.R;
import com.maxxposure.app.cropper.CropImageView;
import com.maxxposure.app.databinding.ActivityScrImagePreviewBinding;
import com.maxxposure.app.model.DamagePoint;
import com.maxxposure.app.model.DamagePointsData;
import com.maxxposure.app.utils.BitMapRotation;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.utils.FileUtils;

import java.util.ArrayList;

public class ScrImagePreview extends AppCompatActivity implements View.OnClickListener {

    private ActivityScrImagePreviewBinding binding;
    String path = FileUtils.getAppFolderPath() + ScrImageListing.selectedOption + ".jpeg";
    Bitmap bitmap = null;
    Bitmap croppedBitmap = null;
    private TextView tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_image_preview);
        initUi();
      /*  String response = "{\n" +
                "\"screenSize\":\"1280X720\",\"previewSize\":\"808X720\",\n" +
                "\"damagepoints\":[{\"damageType\":3,\"description\":\"test\",\"title\":\"test\",\"x\":569,\"y\":391},{\"damageType\":3,\"description\":\"t\",\"title\":\"t\",\"x\":374,\"y\":274},{\"damageType\":2,\"description\":\"hi\",\"title\":\"hi\",\"x\":528,\"y\":262},{\"damageType\":3,\"description\":\"test\",\"title\":\"test\",\"x\":613,\"y\":352},{\"damageType\":2,\"description\":\"fhfg\",\"title\":\"xgfffxf\",\"x\":599,\"y\":187},{\"damageType\":2,\"description\":\"ty\",\"title\":\"ty\",\"x\":206,\"y\":283},{\"damageType\":3,\"description\":\"ppp\",\"title\":\"ppp\",\"x\":633,\"y\":197}]\n" +
                "}\n";
        CropImageView.data = (DamagePointsData) FileUtils.jsonToObject(response, DamagePointsData.class);*/
    }


    public void showDescription(float x, float y, String desc) {
        if (tvDesc.getVisibility() == View.VISIBLE) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setX(x - 25);
            tvDesc.setY(y + 25);
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(desc);
        }
    }

    public void initUi() {
        tvDesc = binding.tvDesc;
        binding.cvBack.setOnClickListener(this);
        binding.cvCross.setOnClickListener(this);
        binding.llNext.setOnClickListener(this);
        binding.cvDamage.setOnClickListener(this);
        binding.cvFeature.setOnClickListener(this);

        //color buttons
        binding.llDamageYellow.setOnClickListener(this);
        binding.cvUndo.setOnClickListener(this);
        binding.llDamageOrange.setOnClickListener(this);
        binding.llDamageRed.setOnClickListener(this);
        binding.llFeatureSkyBlue.setOnClickListener(this);

        bitmap = FileUtils.getBitmap(path);
        binding.ivPreview.setActivity(this);
        binding.ivPreview.setImageBitmap(bitmap);
        binding.ivPreview.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int width = binding.ivPreview.getMeasuredWidth();
                    int height = binding.ivPreview.getMeasuredHeight();
                    bitmap = BitMapRotation.getResizedBitmap(bitmap, height, width);
                    binding.ivPreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

      /*  int bWidth = getIntent().getIntExtra("bWidth", 1280);
        int bHeight = getIntent().getIntExtra("bHeight", 720);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivPreview.getLayoutParams();
        params.height = bHeight;
        params.width = bWidth;
        binding.ivPreview.setLayoutParams(params);*/


        binding.ivPreview.setCropEnabled(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                finish();
                break;

            case R.id.cv_cross:
                binding.ivPreview.setCropOnTouch(false);
                binding.tvNext.setText(" Next ");
                if (binding.llDefault.getVisibility() == View.VISIBLE) {
                    binding.llDamage.setVisibility(View.GONE);
                    binding.llOptions.setVisibility(View.VISIBLE);
                    binding.llDefault.setVisibility(View.GONE);
                    binding.llFeature.setVisibility(View.GONE);
                    binding.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_dialog_close_dark));
                } else {
                    CropImageView.selectedTouch = -1;
                    binding.llDamage.setVisibility(View.GONE);
                    binding.llOptions.setVisibility(View.GONE);
                    binding.llDefault.setVisibility(View.VISIBLE);
                    binding.llFeature.setVisibility(View.GONE);
                    binding.ivPreview.setCropEnabled(false);
                    binding.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_24));
                }
                break;

            case R.id.cv_damage:
                setCircleBackGround(0);
                if (binding.llOptions.getVisibility() == View.VISIBLE) {
                    binding.llDamage.setVisibility(View.VISIBLE);
                    binding.llOptions.setVisibility(View.GONE);
                    binding.llDefault.setVisibility(View.GONE);
                    binding.llFeature.setVisibility(View.GONE);
                }
                break;

            case R.id.cv_feature:
                setCircleBackGround(0);
                if (binding.llOptions.getVisibility() == View.VISIBLE) {
                    binding.llDamage.setVisibility(View.GONE);
                    binding.llOptions.setVisibility(View.GONE);
                    binding.llDefault.setVisibility(View.GONE);
                    binding.llFeature.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.ll_damage_yellow:
                setCircleBackGround(1);
                CropImageView.selectedTouch = CropImageView.YELLOW_TOUCH;
                CustomToast.showToast(ScrImagePreview.this, "Yellow");
                break;

            case R.id.ll_damage_orange:
                setCircleBackGround(2);
                CropImageView.selectedTouch = CropImageView.ORANGE_TOUCH;
                CustomToast.showToast(ScrImagePreview.this, "Orange");
                break;

            case R.id.ll_damage_red:
                setCircleBackGround(3);
                CropImageView.selectedTouch = CropImageView.RED_TOUCH;
                CustomToast.showToast(ScrImagePreview.this, "Red");
                break;

            case R.id.ll_feature_sky_blue:
                setCircleBackGround(4);
                binding.ivPreview.setCropOnTouch(true);
                binding.tvNext.setText(" Apply ");
                break;

            case R.id.cv_undo:
                CropImageView.selectedTouch = -1;
                croppedBitmap = null;
                binding.tvNext.setText(" Apply ");
                binding.cvUndo.setVisibility(View.GONE);
                binding.ivPreview.setImageBitmap(bitmap);
                binding.ivPreview.setCropEnabled(false);
                binding.ivPreview.setCropOnTouch(false);
                break;

            case R.id.llNext:
                if (binding.tvNext.getText().toString().trim().equalsIgnoreCase("apply")) {
                    croppedBitmap = binding.ivPreview.getCroppedBitmap();
                    binding.ivPreview.setImageBitmap(croppedBitmap);
                    binding.tvNext.setText(" Next ");
                    binding.cvUndo.setVisibility(View.VISIBLE);
                    binding.cvUndo.setOnClickListener(this);
                } else {
                    if (croppedBitmap != null) {
                        FileUtils.saveBitmap(String.valueOf(ScrImageListing.selectedOption + ".jpeg"), croppedBitmap);
                    } else {
                        Bitmap bitmap = binding.ivPreview.getNewBitmap();
                        FileUtils.saveBitmap(String.valueOf(ScrImageListing.selectedOption + ".jpeg"), bitmap);
                    }
                    finish();

                }
                break;
        }
    }

    public void showDialog(DamagePoint point) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dlg_describe_issue);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();

        TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
        CardView cv_proceed = dialog.findViewById(R.id.cv_proceed);

        EditText etTitle = dialog.findViewById(R.id.et_title);
        EditText etDesc = dialog.findViewById(R.id.et_desc);
        if (point != null) {
            etTitle.setText(point.getTitle());
            etDesc.setText(point.getDescription());

        }

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        cv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String title = etTitle.getText().toString();
                String desc = etDesc.getText().toString();
                if (point == null) {
                    binding.ivPreview.mergeBitmap(CropImageView.selectedTouch, title, desc);
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }


    public void setCircleBackGround(int type) {
        switch (type) {

            case 0:
                binding.cvDamageYellow.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvDamageOrange.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvDamageRed.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvFeatureSkyBlue.setCardBackgroundColor(getResources().getColor(R.color.black));
                break;

            case 1:
                binding.cvDamageYellow.setCardBackgroundColor(getResources().getColor(R.color.text_black3));
                binding.cvDamageOrange.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvDamageRed.setCardBackgroundColor(getResources().getColor(R.color.black));
                break;

            case 2:
                binding.cvDamageYellow.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvDamageOrange.setCardBackgroundColor(getResources().getColor(R.color.text_black3));
                binding.cvDamageRed.setCardBackgroundColor(getResources().getColor(R.color.black));
                break;

            case 3:
                binding.cvDamageYellow.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvDamageOrange.setCardBackgroundColor(getResources().getColor(R.color.black));
                binding.cvDamageRed.setCardBackgroundColor(getResources().getColor(R.color.text_black3));
                break;

            case 4:
                binding.cvFeatureSkyBlue.setCardBackgroundColor(getResources().getColor(R.color.text_black3));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,
                ScrMultiOptionCamera.class);
        startActivity(intent);
        finish();
    }
}