package com.maxxposure.app.utils;


import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by tabish on 3/4/18.
 */

public class CustomIntent {

    public static void startActivity(final Activity activity, final Class name, final boolean isFinish) {
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, name);
                if (isFinish) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                activity.startActivity(intent);
                //activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    public static void startActivity(final Activity activity, final Class name) {
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, name);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        });
    }


    public static void startFragment(final AppCompatActivity activity, final Fragment fragment) {
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                // fragmentTransaction.replace(R.id.fl_container, fragment).commit();
            }
        });
    }
}
