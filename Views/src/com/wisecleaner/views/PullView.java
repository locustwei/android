/**
 * 
 */
package com.wisecleaner.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class PullView extends LinearLayout {
	private static final int SCROLL_DURATION = 10;
	
	PullVilewListener pullListener;
	
	private Handler mScrollerHandler;
    public View layout1, layout2;

	private long durationMillis = 500;
	public boolean pullRuning;
    
	public PullView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PullView(Context context) {
		super(context);
		initView(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		setOrientation(LinearLayout.VERTICAL);
		layout1 = getChildAt(0);
		layout2 = getChildAt(1);
	}
	private void initView(Context context) {
		mScrollerHandler = new Handler();
	}
	
	public void closeView() {
		if(pullRuning)
			return;
		
		pullRuning = true;
		
		int step = Math.round(layout2.getHeight() / (durationMillis / SCROLL_DURATION));
		
		if(pullListener!=null)
			pullListener.onBeginPull(this);
		mScrollerHandler.postDelayed(new PullAnimation(-step, layout2.getHeight(), 0), SCROLL_DURATION);
	}
	
	public void openView() {
		if(pullRuning)
			return;
		
		pullRuning = true;
		
		layout2.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int target =layout2.getMeasuredHeight();
		layout2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0));
		int step = Math.round(target / (durationMillis / SCROLL_DURATION));
		
		if(pullListener!=null)
			pullListener.onBeginPull(this);
		mScrollerHandler.postDelayed(new PullAnimation(step, 0, target), SCROLL_DURATION);
	}
	
	public long getDurationMillis() {
		return durationMillis;
	}
	
	public void setDurationMillis(long durationMillis) {
		this.durationMillis = durationMillis;
	}
	
	public boolean isPullRuning() {
		return pullRuning;
	}

	public PullVilewListener getPullListener() {
		return pullListener;
	}
	
	public void setPullListener(PullVilewListener pullListener) {
		this.pullListener = pullListener;
	}
	
	@SuppressLint("NewApi")
	private class PullAnimation implements Runnable{

		private int cur, target;
		private int step;
		private boolean tobe = true;

		public PullAnimation(int step, int cur, int target) {
			this.step = step;
			this.cur = cur;
			this.target = target;
		}

		@Override
		public void run() {        	
            if (!tobe ) {
    			pullRuning = false;						
    			
    			if(pullListener!=null)
    				pullListener.onEndPull(PullView.this);
            } else {
            	
    			int pos = step;
    			if(pos>0?cur+pos>target:cur+pos<target){
    				pos = target - cur;
    				cur = target;
    			}else
    				cur += pos;

            	layout1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getHeight()-cur));
//            	layout2.layout(0, 0, getWidth(), getHeight() - cur);

//            	FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams)layout1.getLayoutParams();
//            	params1.setMargins(0, getHeight()- cur, 0, 0);
//            	layout1.setLayoutParams(params1);
//            	layout1.layout(0, getHeight()- cur, getWidth(), getHeight());
            	layout2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,  cur));
            	
            	postInvalidate();
        		
        		if(pullListener!=null)
    				pullListener.onPullRepeat(PullView.this);
        		
                mScrollerHandler.postDelayed(this, SCROLL_DURATION);
            }

            tobe = cur!=target; 
		}
		
	}
		
//	private void layoutChild(ViewGroup viewGroup){
//		for (int i = 0; i < viewGroup.getChildCount(); i++)
//        {
//            View childView = viewGroup.getChildAt(i);
//            childView.setLayoutParams(childView.getLayoutParams());
//         }
//	}
	
	public static interface PullVilewListener {
		public void onBeginPull(PullView sender);
		public void onPullRepeat(PullView sender);
		public void onEndPull(PullView sender);
	}
}
