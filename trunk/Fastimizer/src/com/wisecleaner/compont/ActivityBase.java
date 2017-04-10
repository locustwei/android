package com.wisecleaner.compont;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.wisecleaner.fastimizer.FastimizerApp;

public class ActivityBase extends Activity implements android.view.GestureDetector.OnGestureListener{

	public static ActivityBase CurrentActivity;
	
	private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this, this);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(getClass().getName(), "onPause");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(getClass().getName(), "onRestart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(CurrentActivity==null){
			onApplicationResume();
		}
		CurrentActivity = this;
		Log.i(getClass().getName(), "onResume");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(CurrentActivity==this){
			CurrentActivity = null;
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(CurrentActivity==null)
						onApplicationPause();
				}
			}, 10);
		}
		Log.i(getClass().getName(), "onStop");
	}
	
	protected void onStart() {
		super.onStart();
		Log.i(getClass().getName(), "onStart");
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(getClass().getName(), "onDestroy");
	}
	
	protected void onApplicationResume() {
		((FastimizerApp)getApplication()).onApplicationResume();
		Log.i(getClass().getName(), "onApplicationResume");
	}
	

	protected void onApplicationPause() {
		((FastimizerApp)getApplication()).onApplicationPause();
		Log.i(getClass().getName(), "onApplicationPause");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);	
	}
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {		if(e1==null || e2==null)
		if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) 
			return false;
		 
//		final int MAX = 130, MIN = 50;     
		 
		//if(Math.abs(velocityX) > MAX && Math.abs(velocityY) > MAX) //不确定怎么划
			//return false;
		
		if(Math.abs(velocityX) >  Math.abs(velocityY) ){ //水平方向
			if(velocityX>0)
				return onFlingRight();
			else
				return onFlingLeft();   
		}
	
		 if(Math.abs(velocityY) > Math.abs(velocityX) ){ //垂直方向
			 if(velocityY>0)
				    return onFlingDown();
			 else
				    return onFlingUp();   
		 }
	
		return false;

	}

	protected boolean onFlingRight() {
		return false;
	}

	protected boolean onFlingLeft() {
		return false;
	}

	protected boolean onFlingDown() {
		return false;
	}

	protected boolean onFlingUp() {
		return false;
	}

}
