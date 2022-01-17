package com.maxxposure.app.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateToTimeStamp {
    public static Date date;
    private static SimpleDateFormat simpleDateFormat;

    private static String date2 = "dd-M-yyyy";
    // private static String date1="yyyy/M/d";
    public static String date1 = "d/M/yyyy";
    public static String onlineDate = "yyyy-MM-dd HH:m";

    public static long getMillis(String eventdate) {
        try {
            simpleDateFormat = new SimpleDateFormat(date1);
            date = simpleDateFormat.parse(eventdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }

    public static long getMillisOnlineTime(String eventdate) {
        try {
            simpleDateFormat = new SimpleDateFormat(date1);
            date = simpleDateFormat.parse(eventdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }


    public static Date convertStringToDate(String _date) {
        try {
            simpleDateFormat = new SimpleDateFormat(date1);
            date = simpleDateFormat.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static String getDeviceDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_date_time = sdf.format(new Date().getTimezoneOffset());
        return current_date_time;
    }

    public static String getDeviceDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String current_date_time = sdf.format(new Date().getTimezoneOffset());
        return current_date_time;
    }

    public static long getTimeInMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long timeinmillis = 0;
        try {
            Date date = sdf.parse(time);
            Log.d("TAG", "getTimeInMillis: timein millis : " + date.getTime());
            timeinmillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeinmillis;
    }

    public static long getDateInMillis(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long timeinmillis = 0;
        try {
            Date date = sdf.parse(time);
            Log.d("TAG", "getTimeInMillis: timein millis : " + date.getTime());
            timeinmillis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TAG", "getDateInMillis: " + e.getMessage());
        }
        return timeinmillis;
    }

    public static String getDeviceTime() {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getDate(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = date2;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getConversionTime(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "d MMM yyyy h:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getTimeInHHMMSS(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String changeDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String changeDateTime(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MM-yyyy HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static  boolean checkDateAfterOrNot(String date, String dateafter) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date convertedDate = new Date();
        Date convertedDate2 = new Date();
        try {
            convertedDate = dateFormat.parse(date);
            convertedDate2 = dateFormat.parse(dateafter);
            if (convertedDate2.after(convertedDate)) {
                //txtView.setText("true");
                return true;
            } else {
                //txtView.setText("false");
                return false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
}
