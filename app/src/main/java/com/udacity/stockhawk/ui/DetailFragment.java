package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.udacity.stockhawk.FetchHistoryTask;
import com.udacity.stockhawk.LineChartManager;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ToastUtil;
import com.udacity.stockhawk.bean.DataParse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr.King on 2017/3/18 0018.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.lineChart)
    LineChart mLineChart;

    static final String DETAIL_URI = "URI";

    XAxis xAxisBar, xAxisK;
    YAxis axisLeftBar, axisLeftK;
    YAxis axisRightBar, axisRightK;

    private View mView;
    private Uri mUri;
    private String mHistory;
    private Cursor mCursor;
    private DataParse mData = new DataParse();
    private static final int DETAIL_LOADER = 0;
    private static final Description mCharDescription= new Description();


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
        mCharDescription.setText("\"全省移网\"");
        mLineChart.setDescription(mCharDescription);
        //设置x轴的数据
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            xValues.add("" + i);
        }
        //设置y轴的数据
        ArrayList<Entry> yValue = new ArrayList<>();
        yValue.add(new Entry(13, 1));
        yValue.add(new Entry(6, 2));
        yValue.add(new Entry(3, 3));
        yValue.add(new Entry(7, 4));
        yValue.add(new Entry(2, 5));
        yValue.add(new Entry(5, 6));
        yValue.add(new Entry(12, 7));
        //设置折线的名称
        LineChartManager.setLineName("当月值");
        //创建一条折线的图表
        //LineChartManager.initSingleLineChart(context,mLineChart,xValues,yValue);
        //设置第二条折线y轴的数据
        ArrayList<Entry> yValue1 = new ArrayList<>();
        yValue1.add(new Entry(17, 1));
        yValue1.add(new Entry(3, 2));
        yValue1.add(new Entry(5, 3));
        yValue1.add(new Entry(4, 4));
        yValue1.add(new Entry(3, 5));
        yValue1.add(new Entry(7, 6));
        yValue1.add(new Entry(10, 7));
        LineChartManager.setLineName1("上月值");
        //创建两条折线的图表
        LineChartManager.initDoubleLineChart(getContext(),mLineChart,xValues,yValue,yValue1);
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) { return; }

        mCursor = data;

        FetchHistoryTask fetchHistoryTask = new FetchHistoryTask(getContext());
        fetchHistoryTask.setOnDataFinishedListener(new FetchHistoryTask.OnDataFinishedListener(){
            @Override
            public void onDataSuccessfully(String data) {
                mHistory = data;
                mData.parseKLine(mHistory);
                mData.getKLineDatas();
                ToastUtil.show(getContext(),"获取评论数据成功");
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
