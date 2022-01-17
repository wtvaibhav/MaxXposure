package com.maxxposure.app.utils;

import android.app.Activity;
import android.app.ProgressDialog;

import com.maxxposure.app.R;


public class CustomDialog {

    private static ProgressDialog dlg = null;


    public static void showDialog(final Activity activity, final String message) {

        if (activity == null) {
            return;
        }
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dlg != null) {
                            closeDialog(activity);
                        }
//                        dlg = new ProgressDialog(activity, R.style.progress_style);
                        dlg = new ProgressDialog(activity);
                        dlg.setMessage(message);
                        dlg.setCanceledOnTouchOutside(false);
                        dlg.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void closeDialog(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (dlg != null) {
                            dlg.dismiss();
                            dlg = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
