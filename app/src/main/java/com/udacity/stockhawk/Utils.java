package com.udacity.stockhawk;

import java.util.Date;

/**
 * Created by Mr.King on 2017/3/19 0019.
 */

public class Utils {
    public static String formatMillisToDate(Long Millis){
        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String date = f.format(new Date(Millis));
        return date;
    }
}
