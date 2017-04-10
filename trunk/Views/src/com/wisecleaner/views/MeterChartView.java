/**
 * 仪表盘View
 */
package com.wisecleaner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wisecleaner.views.WinTimer.WinTimerHander;

public class MeterChartView extends View implements WinTimerHander {

	private static final int ZeroAngle = 0;         //起始角度
	private static final int FullAngle = 360;       //结束角度
	private static final int ZeroColor = 0x00FFFFFF;
	private static final int BackColor = 0x40FFFFFF;
	private static final int ValueColor = 0xFFFFFFFF;
	
	private ViewHandler drawHandler;
	
	private Paint paint1 = null,   //外圈画笔
			paint2 = null,                 //内圈、值画笔
			paint3 = null;                 //动画画笔
	
	private RectF mRect;                        //图形所在区域
	
	private int timerAngle = 0;             //动画起始角度
	private WinTimer angleTimer=null;          //旋转动画定时器；
	
	private int breathing = 0;             //呼 | 吸
	private WinTimer brTimer=null;          //呼吸灯动画定时器；
	private float[] breath = new float[]{0.9f, 0.91f, 0.92f, 0.93f, 0.94f, 0.95f, 0.96f, 0.97f, 0.98f, 0.99f, 1f, 0.99f, 0.98f, 0.97f, 0.96f, 0.95f, 0.94f,0.93f,  0.92f,0.91f };  //呼吸动画帧
	
	private int valueT =0;
	private WinTimer valueTimer;

	private int mValue = 0;                 //值（0～100），ValueColor填充区域
	private int tickCount = 100;
	
	private float x, y,  r; //圆心、半径
	
	public MeterChartView(Context context) {
		super(context);
		initPant();
	}

	public MeterChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPant();
	}

	public MeterChartView(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs);
	}

	/**
	 * 初始化
	 */
	private void initPant(){
		paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setColor(BackColor);
		paint1.setAntiAlias(true); // 消除锯齿  
		
		paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(BackColor);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setAntiAlias(true); // 消除锯齿
		
		paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint3.setAntiAlias(true);
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setStrokeCap(Paint.Cap.SQUARE);
		paint3.setStrokeCap(Paint.Cap.ROUND);
		paint3.setAntiAlias(true); // 消除锯齿
		
//        paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint4.setStyle(Paint.Style.FILL_AND_STROKE);
//		paint4.setAntiAlias(true); // 消除锯齿
//		paint4.setColor(0x00000000);
//		paint4.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		mRect = new RectF();
	}
	
	public int getValue() {
		return mValue;
	}
	
	public void setValue(int value) {
		mValue = value;
		invalidate();
	}
	
	public void setValueTimer(int value, int InterVal) {
		if(valueTimer==null){
			valueTimer = new WinTimer(this);
			valueTimer.setInterVal(InterVal);
		}
		
		if(valueTimer.isStoped()){
			valueT = mValue;
			valueTimer.start(true);
		}		
		mValue = value;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(getWidth()<=0 || getHeight()<=0)
			return;
		
		x = getWidth() / 2;
		y = getHeight() / 2;
		r = (float) (Math.min(getWidth(), getHeight()) / 2 * 0.72f);
			
		if(brTimer!=null){
			r *= breath[breathing];
		}
		
		mRect.set(x - r, y - r , x + r, y + r );

		//画外圈及底色
		drawArc(canvas, x, y, r);
		
		if(angleTimer!=null)
			drawAnimate(canvas);
		drawValue(canvas);
		
//		drawTicks(tmpCanvas, x, y, r*0.98f);
		
		if(drawHandler!=null){
			r = (float) (Math.min(getWidth(), getHeight()) / 2 * 0.78);
			mRect.set(x - r, y - r , x + r, y + r );
			drawHandler.drawView(this, canvas);
		}
//		canvas.drawBitmap(tmpBmp, 0, 0, null);

	}

	/**
	 *画刻度线
	 * @param color 
	 */
	private void drawTicks(Canvas canvas, int color, int count) {
        float angle = 360f/(float)tickCount;
        float width = ((2f*r * 0.8f *(float)Math.PI)/(float)tickCount * 0.4f);
        paint2.setStrokeWidth(width);
        paint2.setColor(color);
        
        double k = (tickCount - count)/2f;
        if(k != (int)k){
        	k++;
        	count--;
        }
        
        for(int i=(int)k; i<(int)k+count; i++){
        //for(float i=0; i<360; i += k){
        	float x1 = (float) (r * 0.94f * Math.cos(Math.PI/180 * (270+i*angle + angle/2)));
        	float y1 = (float) (r * 0.94f * Math.sin(Math.PI/180 * (270+i*angle+ angle/2)));
        	float x2 = (float) (r * 0.8f * Math.cos(Math.PI/180 * (270+i*angle+ angle/2)));
        	float y2 = (float) (r * 0.8f * Math.sin(Math.PI/180 * (270+i*angle+ angle/2)));
//        	canvas.drawLine(x, y, x+x1, y+y1, paint2);
        	canvas.drawLine(x + x1, y + y1, x + x2, y + y2, paint2);
        }
	}
	public void setTickCount(int tickCount) {
		this.tickCount = tickCount;
		invalidate();
	}
	
	/**
	 * 画动画
	 * @param canvas
	 * @param x
	 * @param y
	 * @param r
	 */
	private void drawAnimate(Canvas canvas) {
    	paint3.setShader(null);
    	float[] points;
    	int[] colors;
    	float endpoint = (float)(timerAngle+90f)/360f;
    	if(endpoint>1){  //跨0度时渐变色分为两段。    
    		int sc = 0xFF*(360-(int)timerAngle)/90;
    		sc <<= 24;
    		sc +=ZeroColor;
    		colors = new int[]{sc, ValueColor, ZeroColor, sc};
    		points = new float[]{0, endpoint-1, (float)timerAngle / 360f, 1};
    	}else{
    		colors = new int[]{ZeroColor, ValueColor, ValueColor};
    		points = new float[]{(float)timerAngle / 360f, endpoint, 1};	    		
    	}
    	SweepGradient dd = new SweepGradient(x, y, colors, points);
    	paint3.setShader(dd);
    	paint3.setStrokeWidth(paint1.getStrokeWidth()*2);
    	canvas.drawArc(mRect, timerAngle, 90, false, paint3);
	}

	/**
	 * 
	 * @param canvas
	 * @param x
	 * @param y
	 * @param r
	 */
	private void drawValue(Canvas canvas) {
		int value = mValue;
		if(valueTimer!=null && !valueTimer.isStoped())
			value = valueT;
		
		if(value<=0)
			return;
		int k = (int)Math.ceil(tickCount * value / 100f);
		drawTicks(canvas, ValueColor, k);
	}
	
	/**
	 * 画外圈及底色
	 * @param canvas
	 * @param x
	 * @param y
	 * @param r
	 */
	private void drawArc(Canvas canvas, float x, float y, float r) {
        paint1.setStrokeWidth(r*0.01f);
        canvas.drawArc(mRect, ZeroAngle, FullAngle, true, paint1);
        
        drawTicks(canvas, BackColor, tickCount);
        
	}
	
	/**
	 * 开始旋转动画
	 */
	public void startAnimate() {
		angleTimer = new WinTimer(this);
		angleTimer.setInterVal(1000/60);
		timerAngle = 0;
		angleTimer.start(true);
	}

	/**
	 * 旋转停止动画
	 */
	public void stopAnimate() {
		if(angleTimer!=null){
			angleTimer.cancel();
			angleTimer = null;
			timerAngle = 0;
		}
		invalidate();
	}
	
	/**
	 * 开始呼吸动画
	 */
	public void startBreathing() {
		brTimer = new WinTimer(this);
		brTimer.setInterVal(100);
		brTimer.start(true);
	}
	
	/**
	 * 停止呼吸动画
	 */
	public void stopBreathing() {
		if(brTimer!=null){
			brTimer.cancel();
			brTimer = null;
		}
		invalidate();
	}
	
	/**
	 * 定时器回调
	 */
	@Override
	public void onTimer(WinTimer sender) {
		if(sender.equals(angleTimer)){
			timerAngle += 2;
			if(timerAngle>=FullAngle)
				timerAngle = 2;
			invalidate();
		}else if(sender.equals(brTimer)){
			if((++breathing)==breath.length)
				breathing = 0;
			invalidate();
		}else if (sender.equals(valueTimer)) {
			if(valueT==mValue)
				sender.cancel();
			else  {
				valueT = (valueT>mValue)?valueT-1:valueT+1;
				invalidate();
			};
		}
	}
	
	public ViewHandler getDrawHandler() {
		return drawHandler;
	}
	
	public void setDrawHandler(ViewHandler drawHandler) {
		this.drawHandler = drawHandler;
	}
	
	public RectF getRect() {
		RectF result = new RectF();
		result.set(mRect);
		return result;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mRect.contains(event.getX(), event.getY()))
			return super.onTouchEvent(event);
		else {  //不在圆圈范围内的不处理
			return false;
		}
	}

	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}
}
