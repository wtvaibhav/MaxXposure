package com.maxxposure.app.utils;

public class GstCalculation {

    private static float amount = 0;
    private static int _gst = 0;
    private static String gst_amount = null;

    private static float cgst_sgst = 0;
    private static String _cgst_sgst = null;

    public static String calculateGST(String grossTotal, String gst) {

        try {
            amount = Float.valueOf(grossTotal);
            _gst = Integer.valueOf(gst);

            float gst_ = amount * _gst / 100;
            float total_amount = amount + gst_;
            gst_amount = Float.toString(total_amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return gst_amount;
    }

}
