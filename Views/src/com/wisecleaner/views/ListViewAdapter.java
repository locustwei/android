/**
 * ListViewAdapter”ÎData∞Û∂®
 */
package com.wisecleaner.views;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListViewAdapter<T> extends BaseAdapter {

	protected ListViewAdapterHandler<T> mHandler;
	protected List<T> mData = new ArrayList<T>();
	
	public ListViewAdapter(ListViewAdapterHandler<T> handler){
		mHandler = handler;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override	
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mHandler.getView(position, getItem(position), convertView, parent);
	}
	
	public interface ListViewAdapterHandler<T>{
		public View getView(int position, T data, View convertView, ViewGroup parent);
	}
	
	public void addData(List<T> data) {
		if(data==null)
			return;
		
		mData.addAll(data);
		notifyDataSetChanged();
	}
	
	public void addData(T data, int idx) {
		if(data==null)
			return;
		if(idx==-1)
			mData.add(data);
		else 
			mData.add(idx, data);
		notifyDataSetChanged();
	}
	
	public void clearData(){
		mData.clear();
		notifyDataSetChanged();
	}
	
	public void setData(List<T> data) {
		if(data==null)
			return;
		
		mData.clear();
		mData.addAll(data);
		notifyDataSetChanged();
	}
	
	public List<T> getData() {
		return mData;
	}

	
}
