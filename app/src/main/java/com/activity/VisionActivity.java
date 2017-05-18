package com.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amastigote.darker.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.utils.MyDBHelper;
import com.view.MyMarkerView;

import java.util.ArrayList;

/**
 * Created by fy on 2017/5/17.
 */

public class VisionActivity extends AppCompatActivity implements
        OnChartGestureListener,OnChartValueSelectedListener {

    private static final String TAG = "VisionActivity";

    //左右眼视力统计表
    private LineChart mChartLeft;
    private LineChart mChartRight;

    private Button bn_insert;
    private TextView tv_today;
    private TextView tv_today_vision;
    private EditText et_left;
    private EditText et_right;

    private MyDBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    Time t;
    int d;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChartLeft = (LineChart) findViewById(R.id.line_chart_left);
        mChartRight = (LineChart) findViewById(R.id.line_chart_right);
        bn_insert = (Button) findViewById(R.id.bn_insert);
        tv_today = (TextView) findViewById(R.id.tv_today);
        tv_today_vision = (TextView) findViewById(R.id.tv_today_vision);
        et_left = (EditText) findViewById(R.id.left_edit);
        et_right = (EditText) findViewById(R.id.right_edit);

        t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        d = t.monthDay;

        dbHelper = new MyDBHelper(this, "myvision.db3", 1);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from vision where date=?",new String[]{d+""});
        if(c.moveToFirst()) {
            String todayLeftVision = c.getString(c.getColumnIndex("leftVision"));
            String todayRightVision = c.getString(c.getColumnIndex("rightVision"));
            Log.d(TAG, "onCreate: "+ todayLeftVision+"  "+ todayRightVision);
            tv_today_vision.setText("今日视力情况，左眼："+todayLeftVision+",右眼："+todayRightVision+"，请勿重新输入");
        }


        //初始化数据，伪造数据，从10号到18号，第一次安装的时候使用，之后注释掉，主要是为了向数据库中添加几个原始数据
//        for(int i = 10; i < 18;i ++){
//            String date = ""+i;
//            String left = Math.random() * 6+"";
//            String right = Math.random() * 6+"";
//            insertData(dbHelper.getReadableDatabase(), date, left, right);
//        }

        chartSet(mChartLeft,false);
        chartSet(mChartRight, true);

        bn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+d);
                String date = new Integer(d).toString();
                String left = et_left.getText().toString();
                String right= et_right.getText().toString();
                insertData(db, date, left, right);
                Toast.makeText(VisionActivity.this, "添加视力信息成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertData(SQLiteDatabase readableDatabase, String date, String left, String right) {
        readableDatabase.execSQL("insert into vision values(null, ?, ?, ?)", new String[]{date, left, right});
    }

    @Override
    protected void onDestroy() {
        if(dbHelper != null){
            dbHelper.close();
        }
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }

    /**
     * 设置折线图属性
     * @param mChart
     */
    private void chartSet(LineChart mChart, boolean isRight){
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(7f);
        leftAxis.setAxisMinimum(-1f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(mChart, isRight);

        mChart.animateX(2500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    /**
     * 设置折线图数据
     * @param mChart
     */
    private void setData(LineChart mChart, boolean isRight) {

        ArrayList<Entry> values = getValues(isRight);
        Log.d(TAG, "setData: "+values.size());
        if (values.size() == 0) {
            return;
        }
//        for (int i = 0; i < 45; i++) {
//
//            float val = (float) (Math.random() * 6);
//            values.add(new Entry(i, val));
//        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            String str;
            if (!isRight){
                str = "左眼视力统计";
            } else {
                str = "右眼视力统计";
            }
            set1 = new LineDataSet(values, str);

//            set1.setDrawIcons(false);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
//            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public ArrayList<Entry> getValues(boolean isRight) {
        ArrayList<Entry> values = new ArrayList<>();
        cursor = db.query("vision",
                new String[]{"date","leftVision","rightVision"},
                null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                values.add(parseOrder(cursor, isRight));
            }
        }
        return values;
    }

    private Entry parseOrder(Cursor cursor,boolean isRight) {

        String date = cursor.getString(cursor.getColumnIndex("date"));
        String left = cursor.getString(cursor.getColumnIndex("leftVision"));
        String right = cursor.getString(cursor.getColumnIndex("rightVision"));
        Entry entry;
        if (!isRight){
            entry = new Entry(Integer.parseInt(date), Float.parseFloat(left));
        } else {
            entry = new Entry(Integer.parseInt(date), Float.parseFloat(right));
        }

        Log.d(TAG, "parseOrder: "+Float.parseFloat(date));
        return entry;
    }
}
