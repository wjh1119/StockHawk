/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;

/**
 * IntentService which handles updating all Today widgets with the latest data
 */
public class StockWidgetIntentService extends IntentService {

    @BindView(R.id.widget_symbol)
    TextView symbol;

    @BindView(R.id.widget_price)
    TextView price;

    @BindView(R.id.widget_change)
    TextView change;

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
    };
    // these indices must match the projection
    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_PRICE = 1;
    private static final int INDEX_ABSOLUTE_CHANGE = 2;
    private static final int INDEX_PERCENTAGE_CHANGE = 3;

    public StockWidgetIntentService() {
        super(StockWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today com.udacity.stockhawk.widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

        // Get stock's data from the ContentProvider
        Set<String> stockPref = PrefUtils.getStocks(this);
        String[] stockArray = stockPref.toArray(new String[stockPref.size()]);
        Uri firstStockUri = Contract.Quote.makeUriForStock(
                stockArray[0]);
        Cursor firstStock = getContentResolver().query(firstStockUri, STOCK_COLUMNS, null,
                null, null);

        if (!firstStock.moveToFirst()) {
            firstStock.close();
            return;
        }

        // Extract the stock data from the Cursor
        String symbol = firstStock.getString(INDEX_SYMBOL);
        float price = firstStock.getFloat(INDEX_PRICE);
        float absoluteChange= firstStock.getFloat(INDEX_ABSOLUTE_CHANGE);
        float percentageChange = firstStock.getFloat(INDEX_PERCENTAGE_CHANGE);

        final DecimalFormat dollarFormatWithPlus;
        final DecimalFormat dollarFormat;
        final DecimalFormat percentageFormat;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        String absoluteChangeAfterFormat = dollarFormatWithPlus.format(absoluteChange);
        String percentageChangeAfterFormat = percentageFormat.format(percentageChange / 100);

        firstStock.close();

        // Perform this loop procedure for each Today com.udacity.stockhawk.widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the com.udacity.stockhawk.widget's width
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_stock_default_width);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_stock_large_width);
            int layoutId;
            if (widgetWidth >= largeWidth) {
                layoutId = R.layout.widget_stock_large;
            } else if (widgetWidth >= defaultWidth) {
                layoutId = R.layout.widget_stock;
            } else {
                layoutId = R.layout.widget_stock_small;
            }
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews

            views.setTextViewText(R.id.widget_symbol, symbol);
            views.setTextViewText(R.id.widget_price, Float.toString(price));

            if (PrefUtils.getDisplayMode(this)
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

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app com.udacity.stockhawk.widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_stock_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_stock_default_width);
    }
}
