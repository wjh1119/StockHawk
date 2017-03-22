package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.DetailActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
    };
    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_SYMBOL = 1;
    private static final int INDEX_PRICE = 2;
    private static final int INDEX_ABSOLUTE_CHANGE = 3;
    private static final int INDEX_PERCENTAGE_CHANGE = 4;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.CONTENT_URI,
                        STOCK_COLUMNS, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);



                // Extract the stock data from the Cursor
                String symbol = data.getString(INDEX_SYMBOL);
                float price = data.getFloat(INDEX_PRICE);
                float absoluteChange= data.getFloat(INDEX_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(INDEX_PERCENTAGE_CHANGE);

                final DecimalFormat dollarFormatWithPlus;
                final DecimalFormat percentageFormat;

                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

                String absoluteChangeAfterFormat = dollarFormatWithPlus.format(absoluteChange);
                String percentageChangeAfterFormat = percentageFormat.format(percentageChange / 100);

                views.setTextViewText(R.id.widget_symbol, symbol);
                views.setTextViewText(R.id.widget_price, Float.toString(price));

                if (PrefUtils.getDisplayMode(DetailWidgetRemoteViewsService.this)
                        .equals(getString(R.string.pref_display_mode_absolute_key))){
                    views.setTextViewText(R.id.widget_change, absoluteChangeAfterFormat);
                }else{
                    views.setTextViewText(R.id.widget_change, percentageChangeAfterFormat);
                }

                if (absoluteChange > 0){
                    views.setTextColor(R.id.widget_change,getResources().getColor(R.color.material_green_700));
                    views.setTextColor(R.id.widget_price,getResources().getColor(R.color.material_green_700));
                }else if(absoluteChange < 0){
                    views.setTextColor(R.id.widget_change,getResources().getColor(R.color.material_red_700));
                    views.setTextColor(R.id.widget_price,getResources().getColor(R.color.material_red_700));
                }else{
                    views.setTextColor(R.id.widget_change,getResources().getColor(R.color.white));
                    views.setTextColor(R.id.widget_price,getResources().getColor(R.color.white));
                }

                Uri contentUri = Contract.Quote.makeUriForStock(symbol);
                Intent fillInIntent = new Intent(getBaseContext(), DetailActivity.class)
                        .setData(contentUri);
                startActivity(fillInIntent);

                fillInIntent.setData(Contract.Quote.CONTENT_URI);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
