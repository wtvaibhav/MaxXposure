package com.maxxposure.app.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.maxxposure.app.R;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.view.drawer.SNavigationDrawer;

public class ScrSplash extends AppCompatActivity {

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
        setContentView(R.layout.activity_scr_splash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startNextScreen();
    }

    public void startNextScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UserData.getInstance().getTOKEN() != null) {
                    CustomIntent.startActivity(ScrSplash.this, ScrNavigation2.class);
                    //CustomIntent.startActivity(ScrSplash.this, ScrImageListing.class);
                } else {
                    CustomIntent.startActivity(ScrSplash.this, ScrLogin.class);
                }
            }
        }, 3000);
    }

}