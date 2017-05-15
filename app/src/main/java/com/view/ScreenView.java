package com.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


import com.model.Circle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/**
 * 屏保界面
 * @author rex.lei
 *
 */
public class ScreenView extends View {
	
	private static final String TAG = "ScreenView";

	private float mRadius = 30; 
	private float mLocationX = 0;
	private float mLocationY = 0;
	private float mCx1, mCy1, mCx2, mCy2;
	
	private int mScreenWidth;
	private int mScreenHeight;
	private int mGridWidth ;
	private int mGridHeight;
	
	private Paint mPaint;
	
	private long mScreenSaveTime = 0;
	private boolean mBgChangeStyle = false;
	
	private static final int ROW_NUM = 5;   //行数
	private static final int COLUMN_NUM = 3;//列数
	
	private List<Circle> mCircles;
	private Circle mCurCircle;
	private Random mRandom;
	
	private Thread mThread;
	private boolean bSaved;
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
	
	public ScreenView(Context context) {
		this(context, null);
	}
	
	public ScreenView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	

	public ScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		//宽度间隔，高度间隔
		mGridWidth = (int) ((mScreenWidth - 2 * mRadius) / 4);
		mGridHeight = (int) ((mScreenHeight - 2 * mRadius) / 2);
		
		mCircles = new ArrayList<Circle>();
		for (int k = 0; k < ROW_NUM ; k++) {
			for (int i = 0; i < COLUMN_NUM; i++) {
				for (int j = 0; j < ROW_NUM - k; j++) {
					
					int locationX = mGridWidth * j;
					int locationY = mGridHeight * i;
					Log.i(TAG, "[" + locationX + "," + locationY + "]"+ "modek:" + k);
					
					Circle circle = new Circle();
					circle.setLocationX(locationX);
					circle.setLocationY(locationY);
					circle.setMode(k);
					mCircles.add(circle);
				}
			}
		}
		
//		for (int k = 0; k < ROW_NUM ; k++){
//			for (int i = 0; i < COLUMN_NUM; i++) {
//				for (int j = 0; j < 2; j++){
//					int locationX = mGridWidth * j;
//					if (j == 0) {
//						locationX = mGridWidth * 2;
//					} else {
//						locationX = 0;
//					}
//					int locationY = mGridHeight * i;
//					Log.i(TAG, "[" + locationX + "," + locationY + "]" + "mode:6");
//
//					Circle circle = new Circle();
//					circle.setLocationX(locationX);
//					circle.setLocationY(locationY);
//					circle.setMode(6);
//					mCircles.add(circle);
//				}
//			}
//		}
		
		mRandom = new Random();
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		mPaint = new Paint();
		mPaint.setTextSize(32);
		mThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				timeCalculate();
			}
		});
	}
	
	private void timeCalculate(){
		try {
			while (bSaved) {
				Thread.sleep(1000);
				
				mScreenSaveTime ++;
				mBgChangeStyle = !mBgChangeStyle;
				postInvalidate();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		
		//生成随机颜色
		int r = mRandom.nextInt(256);
        int g = mRandom.nextInt(256);
        int b = mRandom.nextInt(256);
		int k = 0;
		mPaint.setColor(Color.rgb(r, g, b));
		
		mCurCircle = mCircles.get((int) (mScreenSaveTime % mCircles.size()));
		k = mCurCircle.getMode() + 1;
		mLocationX = mCurCircle.getLocationX();
		mLocationY = mCurCircle.getLocationY();
		//起点的坐标
		mCx1 = mLocationX + mRadius;
		mCy1 = mLocationY + mRadius;
		//绘制从起点到终点的所有点
		for(int i = 0;i < k;i ++) {
			canvas.drawCircle(mCx1 + i * mGridWidth, mCy1, mRadius, mPaint);
		}
//		mCy2 = mCy1;
//		if (mCurCircle.getMode() == 6){
//			if (mCx1 == mRadius) {
//				mCx2 = mGridWidth * 4 + mRadius;
//			} else {
//				mCx2 = mCx1;
//			}
//		} else {
//			mCx2 = mCx1 + mGridWidth * mCurCircle.getMode() ;
//		}
//
//		//绘制第一个圆
//		canvas.drawCircle(mCx1, mCy1, mRadius, mPaint);
//		//绘制第二个圆
//		canvas.drawCircle(mCx2, mCy2, mRadius, mPaint);
		
		//刷新时间及背景
		if (mBgChangeStyle) {
			setBackgroundColor(Color.BLACK);
			mPaint.setColor(Color.WHITE);
		} else {
			setBackgroundColor(Color.WHITE);
			mPaint.setColor(Color.BLACK);
		}
		
		String time = mDateFormat.format(new Date(mScreenSaveTime * 1000));
		canvas.drawText(time, 
				(float)(mGridWidth * 1.5), 
				(float)mGridHeight + mRadius , 
				mPaint);
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		// TODO Auto-generated method stub
		if (VISIBLE == visibility){
			//可见
			bSaved = true;
			mThread.start();
		} else if (GONE == visibility){
			//不可见
			bSaved = false;
		}
	}

}
