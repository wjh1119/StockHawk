package com.udacity.stockhawk;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.udacity.stockhawk.data.Contract;

/**
 * Created by Mr.King on 2017/2/22 0022.
 */

public class DeleteStockTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = getClass().getSimpleName();

    private final Context mContext;
    private Cursor mData;

    public DeleteStockTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... symbol) {

        String selection = Contract.Quote.TABLE_NAME+
                "." + Contract.Quote.COLUMN_SYMBOL + " = ? ";
        String[] selectionArgs = symbol;
        mContext.getContentResolver().delete(Contract.Quote.CONTENT_URI,selection,selectionArgs);
        return null;
    }

}
