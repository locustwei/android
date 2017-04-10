/**
 * 按Window使用习惯扩展Timer
 */

package com.wisecleaner.views;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class WinTimer {

	private WinTimerHander mHander;
	private int mInterVal=0;
	private Handler uiHandler;
	private Object tag;
	private boolean stoped = true;
	private Timer timer;
	
	public WinTimer(WinTimerHander hander) {
		mHander = hander;
		uiHandler = new Handler();
	}

	public void setInterVal(int interVal) {
		this.mInterVal = interVal;
	}
	
	public int getInterVal() {
		return mInterVal;
	}
	
	public WinTimerHander getHander() {
		return mHander;
	}
	
	public void setHander(WinTimerHander hander) {
		this.mHander = hander;
	}
	
	public Object getTag() {
		return tag;
	}
	
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	public void start(final boolean post2Ui) {
		if(mHander==null || mInterVal<=0 || !stoped)
			return;
		if(timer==null)
			timer = new Timer();
		stoped = false;
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(mHander!=null){
					if(post2Ui){
						WinTimer.this.uiHandler.post(new Runnable() {
							
							@Override
							public void run() {
								WinTimer.this.mHander.onTimer(WinTimer.this);
							}
						});
					}else {
						WinTimer.this.mHander.onTimer(WinTimer.this);
					}
				}
			}
		}, mInterVal, mInterVal);
	}
	
	public void cancel() {
		if(timer!=null){
			timer.cancel();
			timer.purge();
			timer = null;
		}
		stoped  = true;
	}
	
	public interface WinTimerHander{
		public void onTimer(WinTimer sender);
	}

	public boolean isStoped() {
		return stoped;
	}
	
}
