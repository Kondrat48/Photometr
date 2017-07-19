package com.example.artem.photometr;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by artem on 06.07.2017.
 */

public class TabGraphFragment extends Fragment implements OnChartGestureListener {


    private int itemcount = 19;

    public static TabGraphFragment newInstance(int page, String title) {
        TabGraphFragment tabGraphFragment = new TabGraphFragment();
        Bundle args = new Bundle();
        tabGraphFragment.setArguments(args);
        return tabGraphFragment;
    }

    private CombinedChart mChart;
    private String[] elements;
    ArrayList<String> dates;
    private Random random;
    private ArrayList<BarEntry> barEntries;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph,container,false);

        mChart = new CombinedChart(getActivity());


        mChart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.LINE
        });

        FrameLayout frame = (FrameLayout) view.findViewById(R.id.chart);
        frame.addView(mChart);

        createGraph("2016/05/05", "2016/06/01");

        return view;
    }

    public void createGraph(String Date1, String Date2){
        elements = new String[]{"K1", "N", "P", "KS", "KCl", "K2", "Ca", "Mg", "B", "Cu", "K3", "Zn", "Mn", "Fe", "K4", "Mo", "Co", "J", "K5"};

        mChart.getDescription().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return elements[(int) value % elements.length];
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());
        mChart.setData(data);
    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int i = 0; i < itemcount; i++) {
            entries1.add(new BarEntry(i, i));
        }
        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        BarData d = new BarData(set1);
        return d;
    }

    private LineData generateLineData() {

        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0;i<itemcount;i++){
            entries.add(new Entry(i,i));
        }
        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        d.addDataSet(set);
        return d;
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }
    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
