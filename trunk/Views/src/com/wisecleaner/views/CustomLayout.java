/**
 * 扩展RelativeLayout，增加边框线
 */
package com.wisecleaner.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomLayout extends RelativeLayout {

	private ViewHandler viewHandler = null;
	private int border=0;
	private float borderwidth=0;
	private int bordercolor=0;
	
	public CustomLayout(Context context) {
		super(context);
		setWillNotDraw(false);
	}

	public CustomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable._controls_CustomLayout);
		if(a!=null){
			border = a.getInt(R.styleable._controls_CustomLayout_borders, 0);
			borderwidth = a.getDimensionPixelSize(R.styleable._controls_CustomLayout_borderwidth, 0);
			bordercolor = a.getInt(R.styleable._controls_CustomLayout_bordercolor, 0);
	        a.recycle();
		}
		setWillNotDraw(false);
	}

	public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(border!=0 && borderwidth!=0){
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(bordercolor);
			paint.setStrokeWidth(borderwidth);
			 int left = getPaddingLeft();
		    int top = getPaddingTop();
		    int right = getWidth() - getPaddingRight();
		    int bottom = getHeight() - getPaddingBottom();
			if((1 & border) == 1){  //left
				canvas.drawLine(left, top, left, bottom, paint);
			}
			if((2 & border) == 2){ //top
				canvas.drawLine(left, top, right, top, paint);
			}
			if((4 & border) == 4){ //right
				canvas.drawLine(right, top, right, bottom, paint);
			}
			if((8 & border) == 8){//bootom
				canvas.drawLine(left, bottom, right, bottom, paint);
			}
		}
		
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
	
	public ViewHandler getViewHandler() {
		return viewHandler;
	}
	
	public void setViewHandler(ViewHandler viewHandler) {
		this.viewHandler = viewHandler;
	}
	
	public void setBorder(int border) {
		this.border = border;
	}
	
	public int getBorder() {
		return border;
	}
	
	public void setBordercolor(int bordercolor) {
		this.bordercolor = bordercolor;
	}
	
	public int getBordercolor() {
		return bordercolor;
	}
	
	public float getBorderwidth() {
		return borderwidth;
	}
	
	public void setBorderwidth(int borderwidth) {
		this.borderwidth = borderwidth;
	}
}
