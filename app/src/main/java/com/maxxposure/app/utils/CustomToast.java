package com.maxxposure.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by admin on 2/2/2018.
 */

public class CustomToast {

    @SuppressLint("RestrictedApi")
    public static void showToast(final Context activity, final String msg) {
        if (activity == null) {
            return;
        }
        try {
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
