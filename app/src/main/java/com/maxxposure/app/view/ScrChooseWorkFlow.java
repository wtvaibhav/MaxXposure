package com.maxxposure.app.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.maxxposure.app.R;
import com.maxxposure.app.communication.ApiInterface;
import com.maxxposure.app.communication.RetrofitBase;
import com.maxxposure.app.databinding.ActivityScrChooseWorkflowBinding;
import com.maxxposure.app.model.ImageTypeSpin;
import com.maxxposure.app.model.ImageTypeStill;
import com.maxxposure.app.model.WorkFlowData;
import com.maxxposure.app.model.WorkflowDetails;
import com.maxxposure.app.model.WorkflowDetails_List;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.sharedpref.VehicleFormData;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.utils.FileUtils;
import com.maxxposure.app.view.adapter.BrandAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrChooseWorkFlow extends AppCompatActivity implements View.OnClickListener, BrandAdapter.brandClickHandler {

    private String TAG = ScrChooseWorkFlow.class.getName();
    private ActivityScrChooseWorkflowBinding binding;
    private ApiInterface apiInterface;
    private BrandAdapter adapter;
    private String brand_image_url = null;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_choose_workflow);
        initUI();
    }

    public void initUI() {
        apiInterface = RetrofitBase.getInstance().create(ApiInterface.class);
        binding.cvBack.setOnClickListener(this);

        adapter = new BrandAdapter(this);
        adapter.initBrandClickHandler(this);
        binding.rvBrand.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvBrand.setItemAnimator(new DefaultItemAnimator());
        binding.rvBrand.setAdapter(adapter);
        binding.cvProceed.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        call_getBrand();
    }

    public void call_getBrand() {
        Call<String> call = apiInterface.getVehicleLogoDetails("Bearer " + UserData.getInstance().getTOKEN(), UserData.getInstance().getADMIN_ID());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    Log.d(TAG, "onResponse: call_getBrand code : " + response.code());
                    Log.d(TAG, "onResponse: call_getBrand : " + response.body());
                    Log.d(TAG, "onResponse: call_getBrand url : " + call.request().url().toString());

                    WorkflowDetails_List.getInstance().clearList();
                    JSONObject object;
                    switch (response.code()) {
                        case 200:
                            if (!response.isSuccessful()) {
                                return;
                            }
                            String body = response.body();
                            WorkFlowData workFlowData = (WorkFlowData) FileUtils.jsonToObject(body, WorkFlowData.class);
                            VehicleFormData.getInstance().setWorkFlowData(workFlowData);

                            brand_image_url = VehicleFormData.getInstance().getBRAND_NAME_IMAGE();
                            adapter.setList(workFlowData.getAllWorkflowDetails(), VehicleFormData.getInstance().getBRAND_NAME_ID());
                            adapter.notifyDataSetChanged();

                            break;

                        case 404:
                            CustomToast.showToast(ScrChooseWorkFlow.this, "WorkflowDetails Not Found");
                            break;

                        case 401:
                            CustomToast.showToast(ScrChooseWorkFlow.this, "Unauthorised");
                            break;

                        default:
                            CustomToast.showToast(ScrChooseWorkFlow.this, "Something went wrong please try again later.");
                            break;
                    }


                } catch (Exception exception) {
                    exception.getMessage();
                    Log.d(TAG, "onResponse: JSON Exception : " + exception.getMessage());
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showEmptyLayout();
            }
        });
    }

    public void showEmptyLayout() {
       /* if (adapter.getItemCount() > 0) {
            binding.llNoData.setVisibility(View.GONE);
        } else {
            binding.llNoData.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                finish();
                break;

            case R.id.cv_proceed:
                try {
                    if (BrandAdapter.selected_id != -1) {
                        VehicleFormData.getInstance().setBRAND_NAME_ID(BrandAdapter.selected_id);
                        VehicleFormData.getInstance().setBRAND_NAME_IMAGE(brand_image_url);
                        CustomIntent.startActivity(ScrChooseWorkFlow.this, ScrImageListing.class, false);
                    } else {
                        CustomToast.showToast(ScrChooseWorkFlow.this, "Please choose workflow");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void OnBrandClickListner(int brand_id, String brand_image_url) {
        this.brand_image_url = brand_image_url;
        Log.d(TAG, "OnBrandClickListner: brand id : " + brand_id);
        VehicleFormData.getInstance().setBRAND_NAME_ID(BrandAdapter.selected_id);
        VehicleFormData.getInstance().setBRAND_NAME_IMAGE(brand_image_url);
    }
}