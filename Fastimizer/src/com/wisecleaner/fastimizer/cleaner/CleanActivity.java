/**
 * 清理项目详细展示窗口
 */
package com.wisecleaner.fastimizer.cleaner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wisecleaner.compont.ActivityBase;
import com.wisecleaner.compont.WaitingWindow;
import com.wisecleaner.fastimizer.FastimizerApp;
import com.wisecleaner.fastimizer.FastimizerConfig;
import com.wisecleaner.fastimizer.FastimizerConfig.CleanerConfig;
import com.wisecleaner.fastimizer.R;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.BaseCleanResult;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.BaseScanResult;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.CleanupItem;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.GarbageItem;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.ScanStateEnum;
import com.wisecleaner.things.Applications;
import com.wisecleaner.things.Applications.AppInfo;
import com.wisecleaner.things.FileMem;
import com.wisecleaner.things.Utils;
import com.wisecleaner.views.TreeListView;
import com.wisecleaner.views.TreeViewAdapter;
import com.wisecleaner.views.TreeViewAdapter.TreeNode;
import com.wisecleaner.views.TreeViewAdapter.TreeViewAdapterLinstener;

public class CleanActivity extends ActivityBase implements TreeViewAdapterLinstener, OnCheckedChangeListener{
	public static final String TITLE_STRING = "title";
	public static final String ISMEMORY = "ismemory";
	
	public static CleanupItem cleanObject;             //参数传递
	
	private TreeViewAdapter mAdapter;
	private TextView mTextTotalSize, mTextSelSize, mTextUnit;
	private long mTotalSize=0;
	private Button mBtnClean;
	private ScanResultHandler scanhandle = null;
	private CleanResultHandler cleanHandler = null;
	
	private View noItem;
	
	private boolean isMemory;
	
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.activity_cache);
        
        mTextTotalSize = (TextView)findViewById(R.id.text_c_totalsize);
        mTextSelSize = (TextView)findViewById(R.id.text_c_selectsize);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Helvetica LT 33 Thin Extended.ttf");
        mTextSelSize.setTypeface(typeface);
        mTextUnit = (TextView)findViewById(R.id.text_c_unit);
        mBtnClean = (Button)findViewById(R.id.btn_c_cleanup);
        
        TreeListView view = (TreeListView)findViewById(R.id.list_c_detail);
        view.setExpendDrawable(getResources().getDrawable(R.drawable.arrow_bottom));
        view.setCollapseDrawable(getResources().getDrawable(R.drawable.arrow_right));
        
        Animation animation = (Animation)AnimationUtils.loadAnimation(this, R.anim.list_item_anim);  
        LayoutAnimationController controller = new LayoutAnimationController(animation);  
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);  
        controller.setDelay(0.5f);//注意这个地方是以秒为单位，是浮点型数据，所以要加f  
        view.setLayoutAnimation(controller);  
        mAdapter = new TreeViewAdapter(this, view);
        
        isMemory = getIntent().getBooleanExtra(ISMEMORY, false);
        String title = getIntent().getStringExtra(TITLE_STRING);
        if(title==null){
        	title = cleanObject.getName();
        	refreshData();
        }else
          	startScaner();
    	((TextView)findViewById(R.id.text_ac_caption)).setText(title);
 	}

	 @Override
	protected void onStop() {
		super.onStop();
		if(noItem!=null)
			WaitingWindow.hideWaiting(this, noItem);
	}
	 
	 /**
	     * 开始扫描
     */
	private void startScaner() {
		WaitingWindow.showWaiting(this, null, R.drawable.big_progress);
		if(scanhandle==null)
			scanhandle = new ScanResultHandler();
		scanhandle.running = true;
		scanhandle.goon = true;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				CleanupActor.scanCleanItem(CleanActivity.this, (CleanupItem)cleanObject, scanhandle);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						scanCompleated();
					}

				});
			}
		});
		thread.start();
				
		setBtnState(0);
	}
	
	private void setBtnState(int i) {
		mBtnClean.setTag(i);
		if(i==0){
			mBtnClean.setText(R.string.cancel);
			mBtnClean.setBackgroundResource(R.drawable.btnselector_white);
			mBtnClean.setTextColor(R.color.silvertext);
		}else{
			mBtnClean.setText(R.string.cleanup_btn);    	
			mBtnClean.setBackgroundResource(R.drawable.btnselector_green);
			mBtnClean.setTextColor(getResources().getColor(R.color.textcolor));

		}

	}

	/**
	 * 执行清理
	 */
	private void startCleanr() {
		if(cleanHandler==null)
			cleanHandler = new CleanResultHandler();
		if(cleanHandler.running)
			return;
		cleanHandler.running = true;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				CleanupActor.cleanCleanItem(CleanActivity.this, (CleanupItem)cleanObject, cleanHandler);
			
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						cleanCompleted();
					}
				});
			}
		});
		thread.start();
		setBtnState(0);
	}

	private void scanCompleated() {
		mBtnClean.setEnabled(true);
		refreshData();
		scanhandle.running = false;
		WaitingWindow.hideWaiting(this, null);
	}
	
	private void cleanCompleted() {
		//清理了，不离开App就不再扫描。（避免清理不了的东西重复出现）
		if(cleanObject==FastimizerApp.cleanerCfg.getFileCollate() || cleanObject==FastimizerApp.cleanerCfg.getAppCache())
			FastimizerApp.cleanerCfg.getAppCache().setState(ScanStateEnum.scaned);
		if(cleanObject==FastimizerApp.cleanerCfg.getMemCollate() || cleanObject==FastimizerApp.cleanerCfg.getBackApp())
			FastimizerApp.cleanerCfg.getBackApp().setState(ScanStateEnum.scaned);
			
		if(cleanObject.getPareant()==null){
			Intent intent = new Intent(this,FinishActivity.class);
			intent.putExtra(FinishActivity.JUNKSIZE, cleanHandler.getSize());
			intent.putExtra(ISMEMORY, isMemory);
	    	startActivity(intent);		
		}
//		refreshData();
    	if(!isMemory){
			CleanerConfig config = FastimizerConfig.getCleanerConfig(this);
			config.setTotalSize(cleanHandler.getSize()+config.getTotalSize());
			config.save();	
    	}
//		cleanHandler.running = false;
//		setBtnState(1);
		finish();
	}
	
	
	private void refreshData() {
		buildTreeNode();
		mTotalSize = cleanObject.getSize();
		
		mTextTotalSize.setText(String.format(getResources().getString(R.string.totalsize), FileMem.formatMemory(mTotalSize)));
		setSelectedText(getSelectedSize(cleanObject));
		
		if(mAdapter.getCount()==0){
			if(noItem == null){
				noItem = Utils.createView(this, R.layout.control_nocleanitem, null);
				if(cleanObject==FastimizerApp.cleanerCfg.getFileCollate() || cleanObject==FastimizerApp.cleanerCfg.getAppCache())
					((TextView)noItem.findViewById(R.id.text_ni_info)).setText(R.string.noitem_file);
				if(cleanObject==FastimizerApp.cleanerCfg.getMemCollate() || cleanObject==FastimizerApp.cleanerCfg.getBackApp())
					((TextView)noItem.findViewById(R.id.text_ni_info)).setText(R.string.noitem_memory);
				noItem.findViewById(R.id.text_ni_info);
				WaitingWindow.showWaiting(this, noItem, R.drawable.big_progress);
			}
			setBtnState(0);
		}else {
			setBtnState(1);
		}

	}
	
	private void setSelectedText(long size) {
		String[] mems = FileMem.getMemoryUnits(size);
		if(mems==null){
			mTextSelSize.setText(String.valueOf(0));
		}else {
			mTextSelSize.setText(mems[0]);
			mTextUnit.setText(mems[1]);
		}		
	}

	/**
	 * 构建TreeView数据结构
	 */
	 private void buildTreeNode() {
     	mAdapter.buildTree();
     	if(cleanObject == FastimizerApp.cleanerCfg.getMemCollate() && mAdapter.getRootNode().getChildCount()>0)
     		mAdapter.setExpanded(mAdapter.getRootNode().getChilds().get(0), true);
     	else if(cleanObject == FastimizerApp.cleanerCfg.getFileCollate())
	     	for(TreeNode node:mAdapter.getRootNode().getChilds())
	     		if(node.getObject()!=FastimizerApp.cleanerCfg.getAppCache())
	     			mAdapter.setExpanded(node, true);
	 }
	 
	/**
	 * Tree Adapter 回调构造Tree数据结构
	 * @param group
	 * @return
	 */
	@Override
	public int getClildCount(Object group) {
		CleanupItem item = null;
		if(group == null)
			item = cleanObject;
		else if(group instanceof CleanupItem)
			item  = (CleanupItem)group;
		else 
			return 0;

		if(item==null)
			return 0;
		
		if(item.getItems()!=null && item.getItems().size()>0){
			int count = 0;
			for(CleanupItem aitem: item.getItems())
				if(aitem.getSize()>0)
					count ++;
			return count;
		}else
			return 0;
		
	}

	/**
	 * Tree Adapter 回调构造Tree数据结构
	 * @param group
	 * @param position
	 * @return
	 */
	@Override
	public Object getGroupChild(Object group, int position) {
		CleanupItem item;
		if(group == null)
			item = cleanObject;
		else if(group instanceof CleanupItem)
			item  = (CleanupItem)group;
		else 
			return null;
		
		if(item.getItems()!=null){
			int count = 0;
			for(CleanupItem aitem: item.getItems())
				if(aitem.getSize()>0 && count++==position){
					return aitem;
				}
		}
			return null;
	}

	/**
	 * Treee Adapter回调
	 * @param node
	 * @param convertView
	 * @param parent
	 * @return
	 */
	@Override
	public View getView(Object data, View convertView, ViewGroup parent) {
		return geTreeItemViewView((CleanupItem)data, convertView);
	}
	
	/**
	 * 创建Tree Item View
	 * @param node
	 * @param convertView
	 * @return
	 */
	private View geTreeItemViewView(CleanupItem item, View convertView) {
		View view;
		ViewHoder hoder;
		if(convertView!=null){
			view = convertView;
			hoder = (ViewHoder)view.getTag();
		}else{
			view = Utils.createView(this, R.layout.control_treeviewitem, null);
			hoder = new ViewHoder();
			hoder.logo = (ImageView)view.findViewById(R.id.image_treeitem_logo);
			hoder.name = (TextView)view.findViewById(R.id.txet_treeitem_name);
			hoder.size = (TextView)view.findViewById(R.id.text_treeitem_size);
			hoder.selected = (CheckBox)view.findViewById(R.id.ckb_treeitem_selected);
			view.setTag(hoder);
		}
		long size=0;
		
		String name=null;

		if(item.getPackageame()!=null){
			AppInfo appInfo = Applications.getInstalledPackage(this).get(item.getPackageame());
			Drawable drawable = null;
			if(appInfo!=null)
				drawable = appInfo.getIcon(this);
			
			if(drawable==null){
				drawable = getResources().getDrawable(R.drawable.ic_default);
			}
			
			hoder.logo.setImageDrawable(drawable);
		} else {
			hoder.logo.setImageDrawable(null);
		}

		name = item.getName(this);
		size = item.getSize();
		
		if(item.getPareant()==FastimizerApp.cleanerCfg.getAppCache()){
			hoder.selected.setVisibility(View.INVISIBLE);
		}else{
			hoder.selected.setVisibility(View.VISIBLE);
			hoder.selected.setOnCheckedChangeListener(null);
			hoder.selected.setChecked(item.IsSelected());
			hoder.selected.setOnCheckedChangeListener(this);
			hoder.selected.setTag(item);
		}

		hoder.name.setText(name);
		if(size!=0)
			hoder.size.setText(FileMem.formatMemory(size));
		else {
			hoder.size.setText("");
		}
		
		return view;
	}
	
	/**
	 * TreeView 视图项目
	 * @author asa
	 *
	 */
	private class ViewHoder{
		ImageView logo;
		TextView name;
		TextView size;
		CheckBox selected;
	}

	/**
	 * 按钮click
	 * @param view
	 */
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.btn_ac_home:
			backToMain();
			break;
		case R.id.btn_c_cleanup:
			if((Integer)mBtnClean.getTag()==0)
				backToMain();
			else{
				startCleanr();
				setResult(1);
			}
			break;
		default:
			break;
		}
	}

	private void backToMain() {
		if(scanhandle!=null && scanhandle.running)
			scanhandle.goon = false;
		finish();					
	}

	/**
	 * CheckBox 点击
	 * @param arg0
	 * @param arg1
	 */
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		CleanupItem item = (CleanupItem)arg0.getTag();
		if(item==null)
			return;
		changeItemSelected((CleanupItem)item, arg1);
		
		if(!arg1)
			while (item.getPareant()!=null) {
				item = item.getPareant();
				item.setSelected(arg1);
			}
		else{
			item = item.getPareant();
			if(item!=null){
				boolean sel = true;
				for(CleanupItem aiItem: item.getItems())
					if(!aiItem.IsSelected()){
						sel = false;
						break;
					}
				item.setSelected(sel);
			}
		}
		mAdapter.notifyDataSetChanged();
		setSelectedText(getSelectedSize(cleanObject));
	}

	private void changeItemSelected(CleanupItem item, boolean arg1) {
		item.setSelected(arg1);
		
		if(item.getItems()!=null ){
			for(CleanupItem aitem: item.getItems()){
				changeItemSelected(aitem, arg1);
			}
		}
	}

	/**
	 * TreeAdapter回调（ TreeViewAdapterLinstener）
	 * @return
	 */
	@Override
	public Context getContext() {
		return this;
	}
	
	private long getSelectedSize(CleanupItem item) {
		long result = 0;
		if(item.getItems()!=null){
				for(CleanupItem aItem :item.getItems())
					result += getSelectedSize(aItem);
		}else if( item.IsSelected()){
			result = item.getSize();
		}
		return result;
	}

	private class ScanResultHandler extends BaseScanResult{

		public boolean running;
		private boolean goon = true;
		
		@Override
		public boolean begin(GarbageItem cfg) {
			if(!super.begin(cfg))
				return false;
			else
				return goon;
		}
		
		@Override
		public void end(GarbageItem cfg) {
			super.end(cfg);
		}
		
		@Override
		public boolean found(GarbageItem cfg, String name, long size) {
			super.found(cfg, name, size);
			
			final String msg = name;
			final long totalSize = getSelectedSize(cleanObject);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTextTotalSize.setText(msg);
					setSelectedText(totalSize);
				}
			});
			return goon;
		}
		
	}
	
	private class CleanResultHandler extends BaseCleanResult{
		private boolean running = false;
		@Override
		public boolean found(GarbageItem cfg, String name, long size) {
			super.found(cfg, name, size);
			
			final String msg = name;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTextTotalSize.setText(msg);
				}
			});
			return true;
		}
	}

}
