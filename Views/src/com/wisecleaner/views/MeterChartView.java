/**
 * �Ǳ���View
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

	private static final int ZeroAngle = 0;         //��ʼ�Ƕ�
	private static final int FullAngle = 360;       //�����Ƕ�
	private static final int ZeroColor = 0x00FFFFFF;
	private static final int BackColor = 0x40FFFFFF;
	private static final int ValueColor = 0xFFFFFFFF;
	
	private ViewHandler drawHandler;
	
	private Paint paint1 = null,   //��Ȧ����
			paint2 = null,                 //��Ȧ��ֵ����
			paint3 = null;                 //��������
	
	private RectF mRect;                        //ͼ����������
	
	private int timerAngle = 0;             //������ʼ�Ƕ�
	private WinTimer angleTimer=null;          //��ת������ʱ����
	
	private int breathing = 0;             //�� | ��
	private WinTimer brTimer=null;          //�����ƶ�����ʱ����
	private float[] breath = new float[]{0.9f, 0.91f, 0.92f, 0.93f, 0.94f, 0.95f, 0.96f, 0.97f, 0.98f, 0.99f, 1f, 0.99f, 0.98f, 0.97f, 0.96f, 0.95f, 0.94f,0.93f,  0.92f,0.91f };  //��������֡
	
	private int valueT =0;
	private WinTimer valueTimer;

	private int mValue = 0;                 //ֵ��0��100����ValueColor�������
	private int tickCount = 100;
	
	private float x, y,  r; //Բ�ġ��뾶
	
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
	 * ��ʼ��
	 */
	private void initPant(){
		paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setColor(BackColor);
		paint1.setAntiAlias(true); // �������  
		
		paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(BackColor);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setAntiAlias(true); // �������
		
		paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint3.setAntiAlias(true);
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setStrokeCap(Paint.Cap.SQUARE);
		paint3.setStrokeCap(Paint.Cap.ROUND);
		paint3.setAntiAlias(true); // �������
		
//        paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
//		paint4.setStyle(Paint.Style.FILL_AND_STROKE);
//		paint4.setAntiAlias(true); // �������
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

		//����Ȧ����ɫ
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
	 *���̶���
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
	 * ������
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
    	if(endpoint>1){  //��0��ʱ����ɫ��Ϊ���Ρ�    
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
	 * ����Ȧ����ɫ
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
	 * ��ʼ��ת����
	 */
	public void startAnimate() {
		angleTimer = new WinTimer(this);
		angleTimer.setInterVal(1000/60);
		timerAngle = 0;
		angleTimer.start(true);
	}

	/**
	 * ��תֹͣ����
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
	 * ��ʼ��������
	 */
	public void startBreathing() {
		brTimer = new WinTimer(this);
		brTimer.setInterVal(100);
		brTimer.start(true);
	}
	
	/**
	 * ֹͣ��������
	 */
	public void stopBreathing() {
		if(brTimer!=null){
			brTimer.cancel();
			brTimer = null;
		}
		invalidate();
	}
	
	/**
	 * ��ʱ���ص�
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
		else {  //����ԲȦ��Χ�ڵĲ�����
			return false;
		}
	}

	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}
}
