package com.udacity.stockhawk.bean;

import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import timber.log.Timber;

import static com.udacity.stockhawk.Utils.formatMillisToDate;

public class DataParse {
    private ArrayList<KLineBean> data = new ArrayList<>();
    private SparseArray<String> xValuesLabel=new SparseArray<>();


    public void parseKLine(String history) {
        try{
            final String OWM_LIST = "history";
            final String OWM_DATE = "date";
            final String OWM_OPEN = "open";
            final String OWM_CLOSE = "close";
            final String OWM_HIGH = "high";
            final String OWM_LOW = "low";
            final String OWM_VOL = "vol";

            JSONObject rankJson = new JSONObject(history);
            JSONArray reviewsJsonArray = rankJson.getJSONArray(OWM_LIST);

            int numberOfReviews = reviewsJsonArray.length();

            for(int i = 0; i < numberOfReviews; i++) {
                KLineBean kLineData = new KLineBean();
                JSONObject detail = reviewsJsonArray.getJSONObject(i);
                data.add(kLineData);
                kLineData.date = detail.getLong(OWM_DATE);
                kLineData.open = new BigDecimal(detail.getString(OWM_OPEN));
                kLineData.close = new BigDecimal(detail.getString(OWM_CLOSE));
                kLineData.high =  new BigDecimal(detail.getString(OWM_HIGH));
                kLineData.low = new BigDecimal(detail.getString(OWM_LOW));
                kLineData.vol = Long.parseLong(detail.getString(OWM_VOL));
                xValuesLabel.put(i, formatMillisToDate(kLineData.date));
            }

            data.addAll(data);
        }catch (JSONException exception) {
            Timber.e(exception, "Error fetching stock quotes from Json");
        }

    }

    public ArrayList<KLineBean> getKLineDatas() {
        return data;
    }
}
