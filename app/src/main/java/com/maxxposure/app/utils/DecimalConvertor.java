package com.maxxposure.app.utils;

import android.util.Log;

import java.text.DecimalFormat;

public class DecimalConvertor {

    public static String BuildDecimal(String data) {
        double d, d2;

        String converted_value = null;
        if (!data.equals(null)) {
            if (!data.equals("N/A") && !data.equals("NA")) {
                d = Double.parseDouble(data);
                converted_value = String.format("%.2f", d);
                d2 = Double.parseDouble(converted_value);
                if ((d2 - (int) d2) != 0) {
                    Log.d("DecimalConverter", "BuildDecimal: decimal value is there");
                    converted_value = String.valueOf(d2);
                } else {
                    Log.d("DecimalConverter", "BuildDecimal: decimal value is not there");
                    converted_value = String.format("%.2f", d2);
                }

            } else {

                converted_value = data;
            }
            Log.d("DecimalConverter", "" + converted_value);
        }
        return converted_value;
    }

    public static String BuildDecimal2(String data) {
        double d, d2;

        String converted_value = null;
        if (!data.equals(null)) {
            if (!data.equals("N/A") && !data.equals("NA")) {
                d = Double.parseDouble(data);
                converted_value = String.format("%.2f", d);
                d2 = Double.parseDouble(converted_value);
                if ((d2 - (int) d2) != 0) {
                    Log.d("DecimalConverter", "BuildDecimal: decimal value is there");
                    converted_value = String.valueOf(d2);
                } else {
                    Log.d("DecimalConverter", "BuildDecimal: decimal value is not there");
                    // converted_value = String.format("%.0f", d2);
                    DecimalFormat format = new DecimalFormat("0.#");
                    converted_value = format.format(d2);
                }

            } else {

                converted_value = data;
            }
            Log.d("DecimalConverter", "" + converted_value);
        }
        return converted_value;
    }
}
