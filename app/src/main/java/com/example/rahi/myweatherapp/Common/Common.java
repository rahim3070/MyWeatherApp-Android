package com.example.rahi.myweatherapp.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String APP_ID = "Your API Key";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);
        // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToDay(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
