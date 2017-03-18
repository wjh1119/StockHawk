package com.udacity.stockhawk.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.bean.DataParse;
import com.udacity.stockhawk.bean.KLineBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr.King on 2017/3/18 0018.
 */

public class DetailFragment extends Fragment{

    @BindView(R.id.lineChart)
    LineChart lineChart;

    static final String DETAIL_URI = "URI";

    private DataParse mData;
    private ArrayList<KLineBean> kLineDatas;
    XAxis xAxisBar, xAxisK;
    YAxis axisLeftBar, axisLeftK;
    YAxis axisRightBar, axisRightK;

    private View mView;

    private Uri mUri;

    private String mHistroy;

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
        initChart();
//        setData();
        return mView;
    }

    private void initChart() {
        lineChart.setDrawBorders(true);
        lineChart.setBorderWidth(1);
        lineChart.setBorderColor(getResources().getColor(R.color.minute_grayLine));
//        lineChart.setDescription("");
        lineChart.setDragEnabled(true);
        lineChart.setScaleYEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        Legend lineChartLegend = lineChart.getLegend();
        lineChartLegend.setEnabled(false);
        //bar x y轴
        xAxisBar = lineChart.getXAxis();
        xAxisBar.setDrawLabels(true);
        xAxisBar.setDrawGridLines(false);
        xAxisBar.setDrawAxisLine(false);
        xAxisBar.setTextColor(getResources().getColor(R.color.minute_axis_text));
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setGridColor(getResources().getColor(R.color.minute_grayLine));

        axisLeftBar = lineChart.getAxisLeft();
        axisLeftBar.setAxisMinValue(0);
        axisLeftBar.setDrawGridLines(false);
        axisLeftBar.setDrawAxisLine(false);
        axisLeftBar.setTextColor(getResources().getColor(R.color.minute_axis_text));
        axisLeftBar.setDrawLabels(true);
//        axisLeftBar.setShowOnlyMinMax(true);
        axisRightBar = lineChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);
        axisRightBar.setDrawAxisLine(false);
    }

    private void setData() {

        ArrayList<String> xValues = new ArrayList<String>();
        int count = 10;
        int range = 10;
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xValues.add("" + i);
        }

        // y轴的数据
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float value = (float) (Math.random() * range) + 3;
            yValues.add(new Entry(value, i));
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet yLineDataSet = new LineDataSet(yValues, "测试折线图" /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        yLineDataSet.setLineWidth(1.75f); // 线宽
        yLineDataSet.setCircleSize(3f);// 显示的圆形大小
        yLineDataSet.setColor(Color.WHITE);// 显示颜色
        yLineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        yLineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(yLineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(yLineDataSet);

        lineChart.setData(lineData);
    }
}
