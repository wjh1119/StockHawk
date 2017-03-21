package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.udacity.stockhawk.FetchHistoryTask;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ToastUtil;
import com.udacity.stockhawk.bean.KLineBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.R.id.barChart;
import static com.udacity.stockhawk.Utils.dataParse;
import static com.udacity.stockhawk.Utils.formatMillisToDate;

/**
 * Created by Mr.King on 2017/3/18 0018.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(barChart)
    CandleStickChart mCandleStickChart;

    static final String DETAIL_URI = "URI";

    XAxis xAxisK;
    YAxis axisLeftK;
    YAxis axisRightK;

    ArrayList<String> mXVals = new ArrayList<>();

    private View mView;
    private Uri mUri;
    private String mHistory;
    private Cursor mCursor;
    private static final int DETAIL_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_detail, container, false);

        //获取Uri
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        ButterKnife.bind(this, mView);
        //设置图表的描述
        initChart();
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //加载Loader
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    private void initChart(){
        mCandleStickChart.setBackgroundColor(Color.BLACK);

        mCandleStickChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
//        mCandleStickChart.setMaxVisibleValueCount(30);

        // scaling can now only be done on x- and y-axis separately
        mCandleStickChart.setPinchZoom(false);
        mCandleStickChart.setDrawGridBackground(false);
        mCandleStickChart.setDragEnabled(true);
        mCandleStickChart.setScaleYEnabled(false);
        mCandleStickChart.setDoubleTapToZoomEnabled(true);//双击缩放
        mCandleStickChart.setDragDecelerationEnabled(true);
        mCandleStickChart.setDragDecelerationFrictionCoef(0.99f);

        mCandleStickChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ToastUtil.show(getContext(),"date: "+
                        formatMillisToDate(Long.parseLong(mXVals.get((int) h.getX())))+
                        "\nprice: "+h.getY());
                mCandleStickChart.highlightValues(new Highlight[]{h});
            }

            @Override
            public void onNothingSelected() {
                mCandleStickChart.highlightValue(null);
            }
        });

        xAxisK = mCandleStickChart.getXAxis();
        xAxisK.setPosition(XAxisPosition.BOTTOM);
        xAxisK.setDrawLabels(true);
        xAxisK.setDrawGridLines(false);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setLabelCount(4, false);
        xAxisK.setTextColor(getResources().getColor(R.color.minute_axis_text));
        xAxisK.setGridColor(getResources().getColor(R.color.minute_grayLine));

        axisLeftK = mCandleStickChart.getAxisLeft();
        axisLeftK.setDrawGridLines(true);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(true);
        axisLeftK.setLabelCount(7, false);
//        axisLeftK.resetAxisMaximum();
//        axisLeftK.resetAxisMinimum();
        axisLeftK.setTextColor(getResources().getColor(R.color.minute_axis_text));
        axisLeftK.setGridColor(getResources().getColor(R.color.minute_grayLine));
        axisLeftK.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        axisRightK = mCandleStickChart.getAxisRight();
        axisRightK.setDrawLabels(false);
        axisRightK.setDrawGridLines(true);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setGridColor(getResources().getColor(R.color.minute_grayLine));


        mCandleStickChart.getLegend().setEnabled(false);
    }

    private void setData(ArrayList<KLineBean> data){
        mCandleStickChart.resetTracking();

        ArrayList<CandleEntry> yVals = new ArrayList<>();

        ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        int count =  data.size();

        mXVals.clear();
        for (int i = 0; i < count; i++) {

            mXVals.add(data.get(i).date + "");
            candleEntries.add(new CandleEntry(i, data.get(i).high, data.get(i).low, data.get(i).open, data.get(i).close));

            float high = data.get(i).high;
            float low = data.get(i).low;

            float open = data.get(i).open;
            float close = data.get(i).close;

            yVals.add(new CandleEntry(
                    i, high, low, open, close, getResources().getDrawable(R.drawable.star)
            ));
        }

        CandleDataSet candleDataSet = new CandleDataSet(yVals, "Data Set");

        candleDataSet.setValueTextColor(Color.WHITE);
        candleDataSet.setAxisDependency(AxisDependency.LEFT);
        candleDataSet.setShadowColor(Color.DKGRAY);
        candleDataSet.setShadowWidth(0.7f);
        candleDataSet.setDecreasingColor(Color.RED);
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(Color.rgb(122, 242, 84));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.STROKE);
        candleDataSet.setNeutralColor(Color.WHITE);
        candleDataSet.setHighlightLineWidth(1f);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(Color.YELLOW);
        candleDataSet.setDrawValues(true);

        mCandleStickChart.setData(new CandleData(candleDataSet));
        mCandleStickChart.setNoDataText(getResources().getString(R.string.chart_no_data));
        mCandleStickChart.setVisibleXRange(10f,15f);
        mCandleStickChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String Millis = mXVals.get((int) value);

                return formatMillisToDate(Long.parseLong(Millis));
            }

        });
        mCandleStickChart.moveViewToX(data.size() - 1);
        mCandleStickChart.invalidate();
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) { return; }

        mCursor = data;

        FetchHistoryTask fetchHistoryTask = new FetchHistoryTask(getContext());
        fetchHistoryTask.setOnDataFinishedListener(new FetchHistoryTask.OnDataFinishedListener(){
            @Override
            public void onDataSuccessfully(String data) {
                mHistory = data;
//                ToastUtil.show(getContext(),"获取评论数据成功");
                Log.d("onDataSuccessfully",dataParse(mHistory).size()+"");
                setData(dataParse(mHistory));
                mCandleStickChart.notifyDataSetChanged();
            }

            @Override
            public void onDataFailed() {
                ToastUtil.show(getContext(),"获取评论数据失败");
            }
        });
        fetchHistoryTask.execute(mCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
