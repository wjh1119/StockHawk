package com.udacity.stockhawk;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.udacity.stockhawk.data.Contract;

/**
 * Created by Mr.King on 2017/2/22 0022.
 */

public class FetchHistoryTask extends AsyncTask<Cursor, Void, String> {
    private final String LOG_TAG = getClass().getSimpleName();

    private final Context mContext;
    private Cursor mData;

    public FetchHistoryTask(Context context) {
        mContext = context;
    }

    //数据监听器
    FetchHistoryTask.OnDataFinishedListener onDataFinishedListener;

    public void setOnDataFinishedListener(
            FetchHistoryTask.OnDataFinishedListener onDataFinishedListener) {
        this.onDataFinishedListener = onDataFinishedListener;
    }

    @Override
    protected String doInBackground(Cursor... params) {

        mData = params[0];
        String history = null;
        try {

            history = mData.getString(mData.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }

    @Override
    protected void onPostExecute(String history) {
        if(history!=null){
            onDataFinishedListener.onDataSuccessfully(history);
        }else{
            onDataFinishedListener.onDataFailed();
        }
    }

    public interface OnDataFinishedListener {

        void onDataSuccessfully(String data);
        void onDataFailed();

    }
}
