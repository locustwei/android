/**
 * Ê÷ÐÎView
 */
package com.wisecleaner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TreeListView extends ListView {

	private TreeViewAdapter mAdapter;
	private OnItemClickListener mClickListener = null;
	private ViewHandler drawHandler;
	private Drawable expendDrawable, collapseDrawable;

    public TreeListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        syncAdapter();
    }

    public TreeListView(final Context context) {
        this(context, null);
        syncAdapter();
    }

    public TreeListView(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        syncAdapter();
    }

    public ViewHandler getDrawHandler() {
		return drawHandler;
	}
    
    public void setDrawHandler(ViewHandler drawHandler) {
		this.drawHandler = drawHandler;
	}
    
    @Override
    public void setAdapter(final ListAdapter adapter) {
        mAdapter = (TreeViewAdapter) adapter;
        super.setAdapter(adapter);
    }

    private void syncAdapter() {
        super.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView< ? > parent,
                    final View view, final int position, final long id) {
            	mAdapter.handleItemClick(view, view.getTag());
            	if(mClickListener!=null)
            		mClickListener.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void setOnItemClickListener(
    		android.widget.AdapterView.OnItemClickListener listener) {
    	mClickListener = listener;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	if(drawHandler!=null)
    		drawHandler.drawView(this, canvas);
    }
    
    public Drawable getExpendDrawable() {
		return expendDrawable;
	}
    
    public void setExpendDrawable(Drawable expendDrawable) {
		this.expendDrawable = expendDrawable;
	}
    
    public Drawable getCollapseDrawable() {
		return collapseDrawable;
	}
    
    public void setCollapseDrawable(Drawable collapseDrawable) {
		this.collapseDrawable = collapseDrawable;
	}
}
