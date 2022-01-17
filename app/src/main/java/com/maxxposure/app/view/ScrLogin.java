package com.maxxposure.app.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.maxxposure.app.R;
import com.maxxposure.app.communication.ApiInterface;
import com.maxxposure.app.communication.Constants;
import com.maxxposure.app.communication.RetrofitBase;
import com.maxxposure.app.databinding.ActivityScrLoginBinding;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.utils.CustomDialog;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScrLogin extends AppCompatActivity implements View.OnClickListener {

    private String TAG = ScrLogin.class.getName();

    private ActivityScrLoginBinding binding;
    private ApiInterface apiInterface;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_login);
        apiInterface = RetrofitBase.getInstance().create(ApiInterface.class);
        registerClicks();
    }

    public void registerClicks() {
        binding.cvProceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_proceed:
                doValidate();
                break;
        }
    }

    public void doValidate() {
        String email = binding.etUserName.getText().toString();
        String password = binding.etPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            CustomToast.showToast(ScrLogin.this, "Enter username");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CustomToast.showToast(ScrLogin.this, "Enter password");
            return;
        }

        call_LoginAPi(email, password);
    }

    public void call_LoginAPi(String email, String password) {
        CustomDialog.showDialog(ScrLogin.this, Constants.PROGRESS);
        JsonObject object = new JsonObject();
        object.addProperty("userEmail", email);
        object.addProperty("userPassword", password);
        Call<String> call = apiInterface.login(object);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                CustomDialog.closeDialog(ScrLogin.this);

                try {
                    Log.d(TAG, "onResponse: call_LoginAPi code : " + response.code());
                    Log.d(TAG, "onResponse: call_LoginAPi : " + response.body());
                    Log.d(TAG, "onResponse: call_LoginAPi url : " + call.request().url().toString());

                    JSONObject object;
                    switch (response.code()) {
                        case 200:
                            if (!response.isSuccessful()) {
                                return;
                            }
                            object = new JSONObject(response.body().toString());
                            String message = object.getString("message");
                            String userImageUrl = object.getString("userImageUrl");
                            String userFullName = object.getString("userFullName");
                            String userId = object.getString("userId");
                            String adminId = object.getString("adminId");
                            String token = object.getString("token");

                            UserData.getInstance().setUSER_ID(userId);
                            UserData.getInstance().setADMIN_ID(adminId);
                            UserData.getInstance().setTOKEN(token);
                            UserData.getInstance().setFullName(userFullName);
                            UserData.getInstance().setImage(userImageUrl);

                            CustomIntent.startActivity(ScrLogin.this, ScrNavigation2.class);
                            break;

                        case 400:
                      /*Log.d(TAG, "onResponse: call_LoginAPi error : " + response.errorBody().string());
                            object = new JSONObject(response.errorBody().string());
                            String message = object.getString("message");*/
                            CustomToast.showToast(ScrLogin.this, "Wrong Credentials");
                            break;

                        default:
                            //CustomIntent.startActivity(ScrLogin.this, ScrNavigation2.class);
                            CustomToast.showToast(ScrLogin.this, "Something went wrong please try again later.");
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
                CustomDialog.closeDialog(ScrLogin.this);
            }
        });
    }
}