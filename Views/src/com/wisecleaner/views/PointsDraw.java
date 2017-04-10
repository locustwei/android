/**
 * 用于标识当前也。
 */
package com.wisecleaner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PointsDraw extends View {

	private static final int POINT_WIDTH = 20;
	private int pointCount=2;
	private RectF rectF = new RectF();
	private Paint paint;
	private int pointColor=0x44FFFFFF;
	private int selectColor = 0xFF47c853;
	private int selecIndex = 0;
	
	public PointsDraw(Context context) {
		super(context);
		initPaint();
	}

	public PointsDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPaint();
	}

	public PointsDraw(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPaint();
	}

	private void initPaint(){
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(pointColor);
		paint.setAntiAlias(true); // 消除锯齿  
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(getWidth()<=0 || getHeight()<=0)
			return;
		
		int x = (getWidth() - (POINT_WIDTH+POINT_WIDTH)*pointCount + POINT_WIDTH) / 2;
		int y = (getHeight() - POINT_WIDTH) / 2;
		
		for(int i=0; i<pointCount; i++){
			rectF.set(x, y, x+POINT_WIDTH, y+POINT_WIDTH);
			x += POINT_WIDTH + POINT_WIDTH;
			if(i==selecIndex)
				paint.setColor(selectColor);
			else 
				paint.setColor(pointColor);
			canvas.drawArc(rectF, 0, 360, true, paint);
		}
	}
	
	public int getPointCount() {
		return pointCount;
	}
	
	public void setPointCount(int pointCount) {
		this.pointCount = pointCount;
		invalidate();
	}
	
	public int getPointColor() {
		return pointColor;
	}
	
	public void setPointColor(int pointColor) {
		this.pointColor = pointColor;
		invalidate();
	}
	
	public int getSelecIndex() {
		return selecIndex;
	}
	
	public void setSelecIndex(int selecIndex) {
		this.selecIndex = selecIndex;
		invalidate();
	}
	
	public int getSelectColor() {
		return selectColor;
	}
	
	public void setSelectColor(int selectColor) {
		this.selectColor = selectColor;
		invalidate();
	}
}
