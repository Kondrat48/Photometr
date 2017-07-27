package com.example.artem.photometr;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by User on 2/28/2017.
 */

public class TabWatchFragment extends Fragment implements OnChartGestureListener {




    private static final int idMeasuring[]={R.id.cell_changes_k1,R.id.cell_changes_n,R.id.cell_changes_p,R.id.cell_changes_ks,R.id.cell_changes_kcl,R.id.cell_changes_k2,R.id.cell_changes_ca,R.id.cell_changes_mg,R.id.cell_changes_b,R.id.cell_changes_cu,R.id.cell_changes_k3,R.id.cell_changes_zn,R.id.cell_changes_mn,R.id.cell_changes_fe,R.id.cell_changes_k4,R.id.cell_changes_mo,R.id.cell_changes_co,R.id.cell_changes_j,R.id.cell_changes_k5};
    private static final int idHundPerc[]={R.id.cell_100_procent_k1,R.id.cell_100_procent_n,R.id.cell_100_procent_p,R.id.cell_100_procent_ks,R.id.cell_100_procent_kcl,R.id.cell_100_procent_k2,R.id.cell_100_procent_ca,R.id.cell_100_procent_mg,R.id.cell_100_procent_b,R.id.cell_100_procent_cu,R.id.cell_100_procent_k3,R.id.cell_100_procent_zn,R.id.cell_100_procent_mn,R.id.cell_100_procent_fe,R.id.cell_100_procent_k4,R.id.cell_100_procent_mo,R.id.cell_100_procent_co,R.id.cell_100_procent_j,R.id.cell_100_procent_k5};
    private static final int idPercent[]={R.id.cell_procent_k1,R.id.cell_procent_n,R.id.cell_procent_p,R.id.cell_procent_ks,R.id.cell_procent_kcl,R.id.cell_procent_k2,R.id.cell_procent_ca,R.id.cell_procent_mg,R.id.cell_procent_b,R.id.cell_procent_cu,R.id.cell_procent_k3,R.id.cell_procent_zn,R.id.cell_procent_mn,R.id.cell_procent_fe,R.id.cell_procent_k4,R.id.cell_procent_mo,R.id.cell_procent_co,R.id.cell_procent_j,R.id.cell_procent_k5};
    private static final int idKgOnHa[]={R.id.cell_kg_on_ha_k1,R.id.cell_kg_on_ha_n,R.id.cell_kg_on_ha_p,R.id.cell_kg_on_ha_ks,R.id.cell_kg_on_ha_kcl,R.id.cell_kg_on_ha_k2,R.id.cell_kg_on_ha_ca,R.id.cell_kg_on_ha_mg,R.id.cell_kg_on_ha_b,R.id.cell_kg_on_ha_cu,R.id.cell_kg_on_ha_k3,R.id.cell_kg_on_ha_zn,R.id.cell_kg_on_ha_mn,R.id.cell_kg_on_ha_fe,R.id.cell_kg_on_ha_k4,R.id.cell_kg_on_ha_mo,R.id.cell_kg_on_ha_co,R.id.cell_kg_on_ha_j,R.id.cell_kg_on_ha_k5};
    private static final int idDb[]={R.id.cell_dv_k1,R.id.cell_dv_n,R.id.cell_dv_p,R.id.cell_dv_ks,R.id.cell_dv_kcl,R.id.cell_dv_k2,R.id.cell_dv_ca,R.id.cell_dv_mg,R.id.cell_dv_b,R.id.cell_dv_cu,R.id.cell_dv_k3,R.id.cell_dv_zn,R.id.cell_dv_mn,R.id.cell_dv_fe,R.id.cell_dv_k4,R.id.cell_dv_mo,R.id.cell_dv_co,R.id.cell_dv_j,R.id.cell_dv_k5};

    private static TextView
            k1m, nm, pm, ksm, kclm, k2m, cam, mgm, bm, cum, k3m, znm, mnm, fem, k4m, mom, com, jm, k5m,
            k1100pr, n100pr, p100pr, ks100pr, kcl100pr, k2100pr, ca100pr, mg100pr, b100pr, cu100pr, k3100pr, zn100pr, mn100pr, fe100pr, k4100pr, mo100pr, co100pr, j100pr, k5100pr,
            k1pr, npr, ppr, kspr, kclpr, k2pr, capr, mgpr, bpr, cupr, k3pr, znpr, mnpr, fepr, k4pr, mopr, copr, jpr, k5pr,
            k1kgha, nkgha, pkgha, kskgha, kclkgha, k2kgha, cakgha, mgkgha, bkgha, cukgha, k3kgha, znkgha, mnkgha, fekgha, k4kgha, mokgha, cokgha, jkgha, k5kgha,
            k1db, ndb, pdb, ksdb, kcldb, k2db, cadb, mgdb, bdb, cudb, k3db, zndb, mndb, fedb, k4db, modb, codb, jdb, k5db;

    private static final TextView measuring[]={k1m, nm, pm, ksm, kclm, k2m, cam, mgm, bm, cum, k3m, znm, mnm, fem, k4m, mom, com, jm, k5m};
    private static final TextView hundPerc[]={k1100pr, n100pr, p100pr, ks100pr, kcl100pr, k2100pr, ca100pr, mg100pr, b100pr, cu100pr, k3100pr, zn100pr, mn100pr, fe100pr, k4100pr, mo100pr, co100pr, j100pr, k5100pr};
    private static final TextView percent[]={k1pr, npr, ppr, kspr, kclpr, k2pr, capr, mgpr, bpr, cupr, k3pr, znpr, mnpr, fepr, k4pr, mopr, copr, jpr, k5pr};
    private static final TextView kgOnHa[]={k1kgha, nkgha, pkgha, kskgha, kclkgha, k2kgha, cakgha, mgkgha, bkgha, cukgha, k3kgha, znkgha, mnkgha, fekgha, k4kgha, mokgha, cokgha, jkgha, k5kgha};
    private static final TextView db[]={k1db, ndb, pdb, ksdb, kcldb, k2db, cadb, mgdb, bdb, cudb, k3db, zndb, mndb, fedb, k4db, modb, codb, jdb, k5db};

    private static final double mHundPerc[]=new double[19];
    private static final double mPercent[]=new double[19];
    private static final double mKgOnHa[]=new double[19];
    private static final double mDb[]=new double[19];
    private static final double a[]=new double[19];
    private static final double b[]=new double[19];
    private static final double kgOnHa100perc[]={0,50,20,50,10,0,40,5,0.01,0.2,0,0.5,0.5,0.5,0,0.5,0.03,0.02,0};
    private static final double db100perc[]={0,23,5.4,22.5,5.2,0,8.8,0.5,11,52,0,115,160,100,0,270,13.6,15.3,0};



    private CombinedChart mChart;
    private String[] elements;
    private int[] mValues;
    private CombinedData data;
    private CombinedChart combinedChart;

    private RelativeLayout table;
    private FrameLayout frame;
    private RelativeLayout frameLayout;
    private boolean isTableVisible = false;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    private Button buttonViewSwitcher;
    private Button buttonWatchSettings;
    private RelativeLayout buttonViewSwitcherLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch,container,false);

        table = view.findViewById(R.id.table);
        frame = view.findViewById(R.id.chart);
        combinedChart = view.findViewById(R.id.chartInvisible);
        frameLayout = view.findViewById(R.id.chartLayout);
        buttonWatchSettings = view.findViewById(R.id.settingsButtonWatch);
        buttonWatchSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO write method!!!
            }
        });
        for(int i = 0; i<= measuring.length-1; i++) measuring[i] = view.findViewById(idMeasuring[i]);
        for(int i = 0; i<= hundPerc.length-1; i++) hundPerc[i] = view.findViewById(idHundPerc[i]);
        for(int i = 0; i<= percent.length-1; i++) percent[i] = view.findViewById(idPercent[i]);
        for(int i = 0; i<= kgOnHa.length-1; i++) kgOnHa[i] = view.findViewById(idKgOnHa[i]);
        for(int i = 0; i<= db.length-1; i++) db[i] = view.findViewById(idDb[i]);

        buttonViewSwitcher = view.findViewById(R.id.button_view_switcher);
        buttonViewSwitcherLayout = view.findViewById(R.id.layout_button_view_switcher);

        if(android.os.Build.VERSION.SDK_INT >= 21){
            buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table,0,0);
        } else {
            buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table_png,0,0);
        }

        buttonViewSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(table.getVisibility()==View.INVISIBLE){

                    table.setVisibility(View.VISIBLE);
                    frame.setVisibility(View.INVISIBLE);
                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars,0,0);
                    } else {
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars_png,0,0);
                    }
                    isTableVisible=true;
                }else {
                    table.setVisibility(View.INVISIBLE);
                    frame.setVisibility(View.VISIBLE);
                    if(mValues!=null)update(mValues,null);
                    animateChart();

                    if(android.os.Build.VERSION.SDK_INT >= 21){
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.table,0,0);
                    } else {
                        buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.table_png, 0, 0);
                    }
                    isTableVisible=false;
                }
            }
        });

        buttonViewSwitcher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return true;
            }
        });

        mChart = new CombinedChart(getActivity());
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });
        frame.addView(mChart);
        createGraph();

        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)rotate(Configuration.ORIENTATION_LANDSCAPE);

        return view;
    }




    public void createGraph(){
        elements = new String[]{"K1", "N", "P", "KS", "KCl", "K2", "Ca", "Mg", "B", "Cu", "K3", "Zn", "Mn", "Fe", "K4", "Mo", "Co", "J", "K5"};

        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setTouchEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return elements[(int) value % elements.length];
            }
        });
        xAxis.setLabelCount(19);
        xAxis.setLabelRotationAngle(-75);

        data = new CombinedData();

        data.setData(generateLineData(new int[]{0}));
        data.setData(generateBarData(new int[]{0}));

        xAxis.setAxisMaximum(18.5f);
        xAxis.setAxisMinimum(-0.5f);

        mChart.setData(data);
        mChart.invalidate();
    }

    private BarData generateBarData(int[] ints) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < ints.length; i++) {
            entries.add(new BarEntry(i, ints[i]));
        }
        BarDataSet set = new BarDataSet(entries, "Bar 1");
        set.setColors(new int[]{R.color.lime}, getContext());
        set.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return new DecimalFormat("###,###,###").format(value);
            }
        });
        set.setValueTextSize(10f);
        return new BarData(set);
    }

    private LineData generateLineData(int[] ints) {

        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0;i<ints.length;i++){
            if(i==0||i==5||i==10||i==14||i==18)entries.add(new Entry(i,ints[i]));
        }
        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColors(new int[]{R.color.rad}, getContext());
        set.setLineWidth(1.5f);
        set.setCircleColors(new int[]{R.color.rad}, getContext());
        set.setCircleRadius(4.5f);
        set.setDrawValues(false);
        d.addDataSet(set);
        return d;
    }

    public void animateChart(){mChart.animateXY(1000,1000);}


    public void update(int[] values, File file){

        if(file!=null) ;

        mValues = values;


        data = new CombinedData();


        data.setData(generateBarData(values));
        data.setData(generateLineData(values));

        mChart.setData(data);
        mChart.invalidate();

        animateChart();


        for(int i = 0; i<=values.length-1;i++) measuring[i].setText(Integer.toString(values[i]));
        count(values);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mValues!=null)update(mValues,null);
        if(isTableVisible){
            table.setVisibility(View.VISIBLE);
            frame.setVisibility(View.INVISIBLE);
            if(android.os.Build.VERSION.SDK_INT >= 21){
                buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars,0,0);
            } else {
                buttonViewSwitcher.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.bars_png,0,0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        animateChart();
    }

    private void count(int[] values){
        String temp = null;
        NumberFormat formatter = new DecimalFormat("#0.##");
        for(int i = 0; i<=values.length-1;i++){
            if(i<=5)a[i]=values[0];else if(i<=10)a[i]=2*values[5]-values[10];else if(i<=14)a[i]=3.5*values[10]-2.5*values[14];else a[i]=4.5*values[14]-3.5*values[18];
            if(i<=5)b[i]=(values[5]-values[0])/5.0;else if(i<=10)b[i]=(values[10]-values[5])/5.0;else if(i<=14)b[i]=(values[14]-values[10])/4.0;else b[i]=(values[18]-values[14])/4.0;
            mHundPerc[i]=i*b[i]+a[i];
            if(values[i]>mHundPerc[i])if((values[i]-mHundPerc[i])*100/ mHundPerc[i] >100)mPercent[i]=100;else mPercent[i]=(values[i]-mHundPerc[i])*100/ mHundPerc[i];else mPercent[i]=0;
            mKgOnHa[i]=mPercent[i]*kgOnHa100perc[i]/100.0;
            mDb[i]=mPercent[i]*db100perc[i]/100.0;
            db[i].setText(formatter.format(mDb[i]));
            kgOnHa[i].setText(formatter.format(mKgOnHa[i]));
            percent[i].setText(formatter.format(mPercent[i]));
            hundPerc[i].setText(formatter.format(mHundPerc[i]));
        }
    }


    public void rotate(int orientation){

        int d = (int) getResources().getDisplayMetrics().density;
        RelativeLayout.LayoutParams paramsFrame = (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
        RelativeLayout.LayoutParams paramsTable = (RelativeLayout.LayoutParams) table.getLayoutParams();
        RelativeLayout.LayoutParams paramsButton = (RelativeLayout.LayoutParams) buttonViewSwitcherLayout.getLayoutParams();

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            paramsFrame.bottomMargin = 20*d;
            paramsFrame.rightMargin = 92*d;

            paramsTable.bottomMargin = 20*d;
            paramsTable.rightMargin = 92*d;

            paramsButton.rightMargin = 15*d;
            paramsButton.bottomMargin = 0;
            paramsButton.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsButton.addRule(RelativeLayout.CENTER_VERTICAL);
            paramsButton.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsButton.addRule(RelativeLayout.ALIGN_PARENT_END);
            animateChart();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            paramsFrame.bottomMargin = 92*d;
            paramsFrame.rightMargin = 20*d;

            paramsTable.bottomMargin = 92*d;
            paramsTable.rightMargin = 20*d;

            paramsButton.rightMargin = 0;
            paramsButton.bottomMargin = 15*d;
            paramsButton.addRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsButton.removeRule(RelativeLayout.CENTER_VERTICAL);
            paramsButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsButton.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsButton.removeRule(RelativeLayout.ALIGN_PARENT_END);
            animateChart();
        }

    }


    public Bitmap getGraph(){
        combinedChart.setData(mChart.getData());
        combinedChart.getDescription().setEnabled(false);
        combinedChart.getLegend().setEnabled(false);
        combinedChart.setHighlightFullBarEnabled(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setTouchEnabled(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return elements[(int) value % elements.length];
            }
        });
        xAxis.setLabelCount(19);

        xAxis.setAxisMaximum(18.5f);
        xAxis.setAxisMinimum(-0.5f);
        return combinedChart.getChartBitmap();
    }

    public String[] getData(){
        String[] data = new String[42];
        int k = 0;
        for (int i = 0;i<57;i++){
            if(i>0&&i<5){data[k]=measuring[i].getText().toString();k++;}else
            if(i>5&&i<10){data[k]=measuring[i].getText().toString();k++;}else
            if(i>10&&i<14){data[k]=measuring[i].getText().toString();k++;}else
            if(i>14&&i<18){data[k]=measuring[i].getText().toString();k++;}
        }        for (int i = 0;i<57;i++){
            if(i>0&&i<5){data[k]=percent[i].getText().toString();k++;}else
            if(i>5&&i<10){data[k]=percent[i].getText().toString();k++;}else
            if(i>10&&i<14){data[k]=percent[i].getText().toString();k++;}else
            if(i>14&&i<18){data[k]=percent[i].getText().toString();k++;}
        }        for (int i = 0;i<57;i++){
            if(i>0&&i<5){data[k]=db[i].getText().toString();k++;}else
            if(i>5&&i<10){data[k]=db[i].getText().toString();k++;}else
            if(i>10&&i<14){data[k]=db[i].getText().toString();k++;}else
            if(i>14&&i<18){data[k]=db[i].getText().toString();k++;}
        }
        return data;
    }

    public void saveGraph(String title){
        mChart.saveToPath(title, "/Photometer");
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