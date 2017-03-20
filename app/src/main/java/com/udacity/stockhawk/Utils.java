package com.udacity.stockhawk;

import android.util.Log;

import com.udacity.stockhawk.bean.KLineBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Mr.King on 2017/3/19 0019.
 */

public class Utils {
    public static String formatMillisToDate(Long Millis){
        java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String date = f.format(new Date(Millis));
        return date;
    }
    public static ArrayList<KLineBean> dataParse(String history){
        ArrayList<KLineBean> data = new ArrayList<>();
        try{
            final String OWM_LIST = "history";
            final String OWM_DATE = "date";
            final String OWM_OPEN = "open";
            final String OWM_CLOSE = "close";
            final String OWM_HIGH = "high";
            final String OWM_LOW = "low";
            final String OWM_VOL = "vol";

            JSONObject jsonObject = new JSONObject(history);
            JSONArray historyJsonArray = jsonObject.getJSONArray(OWM_LIST);

            int numberOfDates = historyJsonArray.length();
            Log.d("dataParse","numberOfDates is "+numberOfDates);

            for(int i = 0; i < numberOfDates; i++) {
                KLineBean kLineData = new KLineBean();
                JSONObject detail = historyJsonArray.getJSONObject(numberOfDates-i-1);
                kLineData.date = detail.getLong(OWM_DATE);
                kLineData.open = Float.valueOf(detail.getString(OWM_OPEN));
                kLineData.close = Float.valueOf(detail.getString(OWM_CLOSE));
                kLineData.high = Float.valueOf(detail.getString(OWM_HIGH));
                kLineData.low = Float.valueOf(detail.getString(OWM_LOW));
                kLineData.vol = Long.parseLong(detail.getString(OWM_VOL));
                data.add(kLineData);
            }
        }catch (JSONException exception) {
            Timber.e(exception, "Error fetching stock quotes from Json");
        }

        return data;
    }
}
