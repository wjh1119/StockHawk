package com.udacity.stockhawk.bean;

import android.util.SparseArray;

import java.util.ArrayList;

public class DataParse {
    private ArrayList<KLineBean> kDatas = new ArrayList<>();
    private SparseArray<String> xValuesLabel=new SparseArray<>();

    public void parseKLine(String histroy) {
        ArrayList<KLineBean> kLineBeans = new ArrayList<>();
        if (histroy != null) {
            int count = histroy.length();
            for (int i = 0; i < count; i++) {

                KLineBean kLineData = new KLineBean();
                kLineBeans.add(kLineData);
//                kLineData.date
//                kLineData.price
                xValuesLabel.put(i, kLineData.date);
            }
        }
        kDatas.addAll(kLineBeans);
    }

    public ArrayList<KLineBean> getKLineDatas() {
        return kDatas;
    }
}
