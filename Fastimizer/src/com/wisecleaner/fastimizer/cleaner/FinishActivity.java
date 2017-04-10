package com.wisecleaner.fastimizer.cleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wisecleaner.compont.ActivityBase;
import com.wisecleaner.compont.Rotate3dAnimation;
import com.wisecleaner.fastimizer.R;
import com.wisecleaner.things.FileMem;

public class FinishActivity extends ActivityBase {
	private static final int DURATION = 500;
	public static final String JUNKSIZE = "size";
	
	private ViewGroup mContainer;  
    private ImageView mImageView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cleanfinish);
		
		mContainer = (LinearLayout)findViewById(R.id.linear_f_center);
		mImageView = (ImageView)findViewById(R.id.image_f_center);
		
		boolean isMemory = getIntent().getBooleanExtra(CleanActivity.ISMEMORY, false);
		if(isMemory){
			mImageView.setImageResource(R.drawable.rocket);
		}
		TextView textView = (TextView)findViewById(R.id.text_f_size);
		long size = getIntent().getLongExtra(JUNKSIZE, 0);
		if(size==0)
			textView.setText("");
		else{
			String s;
			if(isMemory)
				s = getResources().getString(R.string.cleanmemorysize);
			else 
				s = getResources().getString(R.string.cleanmjunkfile);
			
			textView.setText(String.format(s, FileMem.formatMemory(size)));
		}
		
		mContainer.postDelayed(new Runnable() {
			@Override
			public void run() {
				applyRotation(0,  90);
			}
		}, 1000);
	}
	
	public void onClick(View v) {
		finish();
	}

	private void applyRotation(float start, float end) {  
        // 计算中心点  
        final float centerX = mContainer.getWidth() / 2.0f;  
        final float centerY = mContainer.getHeight() / 2.0f;  
 
        // Create a new 3D rotation with the supplied parameter  
        // The animation listener is used to trigger the next animation  
        final Rotate3dAnimation rotation =  
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);  
        rotation.setDuration(DURATION);  
        rotation.setFillAfter(true);  
        rotation.setInterpolator(new AccelerateInterpolator());  
        //设置监听  
        rotation.setAnimationListener(new DisplayNextView());  
 
        mContainer.startAnimation(rotation);  
    }  
	
	private final class DisplayNextView implements Animation.AnimationListener {  
        private DisplayNextView() {  
        }  
 
        public void onAnimationStart(Animation animation) {  
        }  
        //动画结束  
        public void onAnimationEnd(Animation animation) {  
            mContainer.post(new SwapViews());  
        }  
 
        public void onAnimationRepeat(Animation animation) {  
        }  
    }  
	
	private final class SwapViews implements Runnable {  
 
        public SwapViews() {  
        }  
 
        public void run() {  
            final float centerX = mContainer.getWidth() / 2.0f;  
            final float centerY = mContainer.getHeight() / 2.0f;  
            Rotate3dAnimation rotation;  
              
        	Bitmap bmp1, bmp2;
			bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.hook);
			Matrix matrix = new Matrix();  
		    matrix.setScale(-1, 1); 
		    
		    bmp2 =  Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight(), matrix, true);
			mImageView.setImageBitmap(bmp2);

            rotation = new Rotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);  
 
            rotation.setDuration(DURATION);  
            rotation.setFillAfter(true);  
            rotation.setInterpolator(new DecelerateInterpolator());  
            //开始动画  
            mContainer.startAnimation(rotation);  
        }  
    }  

}
