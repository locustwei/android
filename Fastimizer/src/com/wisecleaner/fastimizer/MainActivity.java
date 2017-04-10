/**
 * 主窗口
 */
package com.wisecleaner.fastimizer;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wisecleaner.compont.ActivityBase;
import com.wisecleaner.compont.HttpData;
import com.wisecleaner.compont.HttpData.HttpCompleted;
import com.wisecleaner.fastimizer.FastimizerConfig.CleanerConfig;
import com.wisecleaner.fastimizer.cleaner.CleanActivity;
import com.wisecleaner.fastimizer.cleaner.CleanupActor;
import com.wisecleaner.fastimizer.cleaner.JunkCfg;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.BaseCleanResult;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.BaseScanResult;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.CleanupItem;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.GarbageItem;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.ScanStateEnum;
import com.wisecleaner.things.Applications;
import com.wisecleaner.things.FileMem;
import com.wisecleaner.things.FileMem.FileSize;
import com.wisecleaner.things.Googleplay;
import com.wisecleaner.things.Utils;
import com.wisecleaner.views.MeterChartView;
import com.wisecleaner.views.ViewHandler;
import com.wisecleaner.views.WinTimer;
import com.wisecleaner.views.WinTimer.WinTimerHander;

public class MainActivity extends ActivityBase implements WinTimerHander, ViewHandler, HttpCompleted<JSONObject>, OnClickListener, AnimationListener {
	
	private static final int FLASHVIEW_RESULT = 0;
	private static final int CLEANVIEW_RESULT = 1;
	private static final int ID_CLEANITEM = 0xFDD;        //清理项View使用ID（view.setid）
	
	private MeterChartView mScoreChart, mMemChart, mStorageChart;
	private ViewGroup mScanView, mBtnView;
	private TextView mTextScanMsg, mTextMem, mTextStore;//, mTextScore, mTextHissize;
	
	private FileSize mMemState;   //内存状态。
	private FileSize mStroreState; //存储状态
	
	private Button mBtnDoClean;
	
	private HashMap<CleanupItem, CfgViewHolder> cfgViewMap;
	private ScanResultHandler   scanhandler; 
	private CleanResultHandler cleanHandler;

	private PopupWindow menuWindow;
	private boolean showMenum=true;  //解决menu按钮点击是还没创建menuWindow的问题
	
	private ArrayList<WinTimer>scoreTimers ;
	private WinTimer memoryTimer;
	
	private Animation inAnim, outAnim;
	private View outView, inView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mScoreChart = (MeterChartView)findViewById(R.id.chart_m_score);
        mScoreChart.setDrawHandler(this);
        mScoreChart.setTickCount(128);
        mMemChart = (MeterChartView)findViewById(R.id.chart_m_memory);
        mMemChart.setDrawHandler(this);
        mMemChart.setTickCount(40);
        mStorageChart = (MeterChartView)findViewById(R.id.chart_m_store);
        mStorageChart.setDrawHandler(this);
        mStorageChart.setTickCount(40);
        
//        mSwitch = (ViewGroup)findViewById(R.id.view_m_bottompanel);
        mScanView = (ViewGroup)findViewById(R.id.view_m_scanmsg);
        mScanView.setVisibility(View.GONE);
        mBtnView = (ViewGroup)findViewById(R.id.view_m_btnview);
        
        mBtnDoClean = (Button)findViewById(R.id.btn_m_docleanup);
        
        mTextScanMsg = (TextView)findViewById(R.id.text_m_scanmsg);
        mTextStore = (TextView)findViewById(R.id.text_m_storesize);
        mTextMem = (TextView)findViewById(R.id.text_m_memsize);
               
        memoryTimer = new WinTimer(this);  //定时器，更新内存状态
        memoryTimer.setInterVal(1000);
        memoryTimer.start(true);
        
        inAnim =new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
        		Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0); 
        inAnim.setDuration(500);
        		//AnimationUtils.loadAnimation(this, R.anim.anim_swing_in_bottom);
        inAnim.setAnimationListener(this);
        outAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
        		Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1); 
        		//AnimationUtils.loadAnimation(this, R.anim.anim_swing_out_bottom);
        outAnim.setDuration(500);
        outAnim.setAnimationListener(this);
        
        refreshStorage();
        refreshMemoryState();

        if(FastimizerConfig.getCleanerConfig(this).IsFirstrun()){   //第一次运行
            Intent intent = new Intent(this, FlashActivity.class);
            startActivityForResult(intent, FLASHVIEW_RESULT);
        }else
        	mScoreChart.startBreathing();
//		RootCommand("chmod 777 "+getPackageCodePath());
	}        

	/**
	 * 为每一项清理项创建一个View，列在屏幕下方
	 */
	private void createScanitemView() {
		cfgViewMap = new HashMap<CleanupItem, CfgViewHolder>();
		for(CleanupItem collate: FastimizerApp.cleanerCfg.getCleanCollates()){
			for(CleanupItem item: collate.getItems()){
				View view = Utils.createView(this, R.layout.control_main_scanitem, mScanView);
				view.setOnClickListener(this);
				view.setId(ID_CLEANITEM);
				view.setTag(item);
				
				CfgViewHolder holder = new CfgViewHolder(view);
				
				holder.caption.setText(item.getName(this));
				mScanView.addView(view, mScanView.getChildCount()-1);
				cfgViewMap.put(item, holder);
			}
		}
	}
	
	/**
	 * 点击事件
	 * @param v
	 */
	public void onClick(View v){
    	switch (v.getId()) {
    	case R.id.btn_m_menu:  //菜单
    		showMenum = menuWindow != null;     
    		openOptionsMenu();
    		break;
		case R.id.btn_m_speedup:  //内存清理Activity
	    	startCleanActive(FastimizerApp.cleanerCfg.getMemCollate(), getResources().getString(R.string.speedup), true);
			break;
		case R.id.btn_m_cleanup: //垃圾清理Activity
	    	startCleanActive(FastimizerApp.cleanerCfg.getFileCollate(), getResources().getString(R.string.cleanup), false);
			break;
		case R.id.chart_m_score:         //扫描&评分
			startScaner();
			break;
		case R.id.btn_m_docleanup:  //一键清理 || 取消扫描
			if(scanhandler==null)
				return;
			if(scanhandler.running)
				cacelScan();
			else
				startClean();
			break;
		case ID_CLEANITEM:         //清理项View点击。（打开清理项细节）
			if(scanhandler!=null && scanhandler.running)
				return;
	    	startCleanActive( (CleanupItem)v.getTag(), null, ((CleanupItem)v.getTag()).getPareant()==FastimizerApp.cleanerCfg.getMemCollate());
			break;
		default:
			break;
		}
    }

	
	private void startCleanActive(CleanupItem item, String caption, boolean isMemory) {
		if(scoreTimers!=null && scoreTimers.size()>0)
			return;
		CleanActivity.cleanObject = item;
    	Intent intent = new Intent(MainActivity.this,CleanActivity.class);
    	intent.putExtra(CleanActivity.TITLE_STRING, caption);
    	intent.putExtra(CleanActivity.ISMEMORY, isMemory);
    	startActivityForResult(intent, CLEANVIEW_RESULT);		
	}

	private void cacelScan() {
		if(scanhandler==null || !scanhandler.running)
			return;
		scanhandler.goon = false;
	}

	/**
	 * 开始清理（线程）
	 */
    private void startClean() {
    	if(cleanHandler==null)
    		cleanHandler = new CleanResultHandler();

		if(cleanHandler.running || (scanhandler!=null && scanhandler.running))
			return;
		
    	cleanHandler.goon = true;
    	cleanHandler.running = true;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				CleanupActor.startClean(MainActivity.this, FastimizerApp.cleanerCfg, cleanHandler);
				cleanHandler.running = false;
				MainActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						cleanCompleted();
					}

				});
			}
		}).start();
		
        showScanViews();
//		mScoreChart.startAnimate();
	}
    
	private void cleanCompleted() {
		//清理了，不离开App就不再扫描。（避免清理不了的东西重复出现）
		FastimizerApp.cleanerCfg.getAppCache().setState(ScanStateEnum.scaned);
		FastimizerApp.cleanerCfg.getBackApp().setState(ScanStateEnum.scaned);
		
		updateScore(getScore());
		scanCompleted();
		hideScanViews();
		CleanerConfig config = FastimizerConfig.getCleanerConfig(this);
		config.setTotalSize(cleanHandler.getSize()+config.getTotalSize());
		config.save();
		
		
	}

	/**
     * 开始扫描
     */
	private void startScaner() {
		if(scoreTimers!=null && scoreTimers.size()>0)
			return;
		if(scanhandler==null)
			scanhandler = new ScanResultHandler();
		if(scanhandler.running || (cleanHandler!=null && cleanHandler.running))  //正在进行
			return;
		mScoreChart.stopBreathing();
		
		scanhandler.goon = true;
		scanhandler.running = true;

		mScoreChart.setValue(0);
		updateScore(100);

//		mScoreChart.startAnimate();

		new Thread(new Runnable() {
			@Override
			public void run() {

				CleanupActor.startScan(MainActivity.this, FastimizerApp.cleanerCfg, scanhandler);
				scanhandler.running = false;
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						scanCompleted();
					}
				});
			}
		}).start();

		if(cfgViewMap==null) //没有创建清理项View
			createScanitemView();		
        showScanViews();

	}

	/**
	 * 刷新内存状态（Timer 调用）
	 */
	private void refreshMemoryState(){
        mMemState = FileMem.getMemorySize(this);
        mMemChart.setValue((int) (100 * mMemState.UsedSize  / mMemState.TotalSize));
        mTextMem.setText(String.format("%s/%s", FileMem.formatMemory(mMemState.UsedSize), FileMem.formatMemory(mMemState.TotalSize) ) );
    }
	
	/**
	 * 刷新存储空间状态
	 */
	private void refreshStorage() {
		FileSize tmp;
		
		mStroreState = FileMem.getFileState(Environment.getDataDirectory().getAbsolutePath());   // /data/

		if(Environment.isExternalStorageRemovable()){  //外置卡
			tmp = FileMem.getSdcardSize();//.getFileState(Environment.getExternalStorageDirectory().getAbsolutePath());
			mStroreState.FreeSize += tmp.FreeSize;
			mStroreState.TotalSize += tmp.TotalSize;
			mStroreState.UsedSize += tmp.UsedSize;			
		}else{  //内置卡 包含在/data下（所以不要），统计外置卡
			tmp = FileMem.getFileState(FileMem.getRemovableStorage());
			mStroreState.FreeSize += tmp.FreeSize;
			mStroreState.TotalSize += tmp.TotalSize;
			mStroreState.UsedSize += tmp.UsedSize;			
		}
		
		tmp = FileMem.getFileState(Environment.getRootDirectory().getAbsolutePath());  // /syste/
		mStroreState.FreeSize += tmp.FreeSize;
		mStroreState.TotalSize += tmp.TotalSize;
		mStroreState.UsedSize += tmp.UsedSize;
		
		mStorageChart.setValue((int) (100 * mStroreState.UsedSize / mStroreState.TotalSize));
		mTextStore.setText(String.format("%s/%s", FileMem.formatMemory(mStroreState.UsedSize), FileMem.formatMemory(mStroreState.TotalSize) ) );
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        createPopupMenu(menu);
        if(!showMenum)
        	menuWindow.showAsDropDown(findViewById(R.id.btn_m_menu));
        return showMenum;
    }
    
    /**
     * 创建弹出菜单窗口
     * @param menu
     */
    private void createPopupMenu(Menu menu) {
    	int width = 0;
    	LinearLayout view = new LinearLayout(this);
    	view.setOrientation(LinearLayout.VERTICAL);
    	view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	view.setBackgroundColor(getResources().getColor(R.color.textcolor));
//    	view.setBackgroundResource(R.drawable.menu_background);
		
		for(int i=0; i<menu.size(); i++){
			MenuItem menuItem = menu.getItem(i);
			
			View itemView = Utils.createView(this, R.layout.control_menu_item, view);
			itemView.setId(menuItem.getItemId());
			itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onOptionsItemSelected(v.getId());
				}
			});
			ImageView imageView = (ImageView)itemView.findViewById(R.id.image_mm_icon);
			if(menuItem.getIcon()!=null){
				imageView.setImageDrawable(menuItem.getIcon());
			}else {
				imageView.setVisibility(View.INVISIBLE);
			}
			TextView textView = (TextView)itemView.findViewById(R.id.text_mm_caption);
			textView.setText(menuItem.getTitle());

			itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			width = width>itemView.getMeasuredWidth()?width:itemView.getMeasuredWidth();

			view.addView(itemView);
		}
		menuWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		menuWindow.setFocusable(true);  
		menuWindow.setOutsideTouchable(true);  
		menuWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_background));
//		menuWindow.setAnimationStyle(android.R.style.Animation_Translucent);
		menuWindow.setWidth(width+70);
	}

	@Override
    public boolean onMenuOpened(int featureId, Menu menu) {
    	if(menuWindow!=null){
    		menuWindow.showAsDropDown(findViewById(R.id.btn_m_menu));//, Gravity.NO_GRAVITY, 0, 0);
    		return false;
    	}else{
    		return super.onMenuOpened(featureId, menu);
    	}
    }
    
    public boolean onOptionsItemSelected(int menuid) {
    	if(menuWindow!=null)
    		menuWindow.dismiss();
    	
    	switch (menuid) {
        case R.id.menu_update:  //检查更新
        	HttpData.asyncGetUrl(FastimizerApp.CHECKUPDATE_URL, null, this);
//        	Googleplay.openMarketForUpdate(this);
            return true;
        case R.id.menu_feedback: 
        	Intent intent = new Intent(MainActivity.this,WebViewActivity.class);
	    	intent.putExtra(WebViewActivity.URL, FastimizerApp.FEEDBACK_URL);
	    	intent.putExtra(WebViewActivity.CAPTION, getResources().getString(R.string.feedback));
	    	startActivity(intent);
        	return true;
//        case R.id.menu_help:
//        	Intent intent1 = new Intent(MainActivity.this, WebViewActivity.class);
//	    	intent1.putExtra(WebViewActivity.URL, FastimizerApp.HELP_URL);
//	    	intent1.putExtra(WebViewActivity.CAPTION, getResources().getString(R.string.help));
//	    	startActivity(intent1);
//        	return true;
        case R.id.menu_about:
        	Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
	    	startActivity(intent2);
        	return true;
//        case R.id.menu_settings:
//        	Intent intent3 = new Intent(MainActivity.this, SettingsActivity.class);
//	    	startActivity(intent3);
//        	return true;
        default:
            return false;
    	}
    }

	/**
     * 扫描前把界面设置为扫描状态。
     */
    private void showScanViews() {
    	if(mBtnView.getVisibility()!=View.GONE){
    		outView = mBtnView;
    		inView = mScanView;
	    	mBtnView.startAnimation(outAnim);
    	}
    	
    	mTextScanMsg.setVisibility(View.VISIBLE);
    	mBtnDoClean.setText(R.string.cancel);
    	mBtnDoClean.setBackgroundResource(R.drawable.btnselector_white);
    	mBtnDoClean.setTextColor(R.color.silvertext);
    	for(CfgViewHolder viewHolder: cfgViewMap.values()){
    		viewHolder.message.setText("");
    		viewHolder.logo.setVisibility(View.INVISIBLE);
    	}
	}
    
    /**
     * 扫描后把界面复原。
     */
    private void hideScanViews() {
    	if(mScanView.getVisibility()!=View.GONE){
//	    	mScanView.setVisibility(View.GONE);
    		inView = mBtnView;
    		outView = mScanView;
	    	mScanView.startAnimation(outAnim);
    	}

	}
    
    /**
     * Timer 回调（刷新内存状态）
     */
	@Override
	public void onTimer(WinTimer sender) {
		if(sender==memoryTimer)   //刷新内存timer
			refreshMemoryState();
		else{
			if(sender.isStoped()){
				scoreListAdd(sender, false);
				return;
			}
			int tager = (Integer)sender.getTag();
			int cur = mScoreChart.getValue();
			int value = tager>cur?cur+1:cur-1;
			mScoreChart.setValue(value);
			
			if(value==tager){
				sender.cancel();
				scoreListAdd(sender, false);
			}
		}
	}
	/**
	 * s扫描结束
	 */
	private void scanCompleted() {
		mTextScanMsg.setVisibility(View.INVISIBLE);
		mBtnDoClean.setText(R.string.cleanup_btn);    	
		mBtnDoClean.setBackgroundResource(R.drawable.btnselector_green);
    	mBtnDoClean.setTextColor(getResources().getColor(R.color.textcolor));

    	if(scanhandler.goon==false)  //取消
    		hideScanViews();
	}
	
	/**
	 * 计算得分
	 * @return
	 */
	private int getScore() {
		long memsize = FastimizerApp.cleanerCfg.getMemCollate().getSize();
		long fileszie = FastimizerApp.cleanerCfg.getFileCollate().getSize();
		
//		int score = 96-(int)((double)fileszie / (double)mStroreState.UsedSize * 100 + 
//				(double)memsize / (double)mMemState.UsedSize * 10);
		int score = 100- (int)((float)fileszie / (200f*1024f) + (float)memsize / (30f*1024f*1024f));
		if(score<20)
			score = 20;
		return score;
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case FLASHVIEW_RESULT:
			if(resultCode==1){
				startScaner();
	        	FastimizerConfig.getCleanerConfig(this).save();
			}else 
				finish();
			break;
		case  CLEANVIEW_RESULT:
			if(resultCode==1){
				if(scanhandler!=null)
					updateScore(getScore());
				if(mScanView.getVisibility()==View.VISIBLE){
					CfgViewHolder viewHolder = cfgViewMap.get(CleanActivity.cleanObject);
					if(viewHolder!=null){
						long size = CleanActivity.cleanObject.getSize();
						viewHolder.message.setText(FileMem.formatMemory(size));
						if(size==0)
							viewHolder.logo.setImageResource(R.drawable.green);
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mScanView.getVisibility()==View.VISIBLE){
			if(scanhandler!=null && scanhandler.running)
				cacelScan();
			else
				hideScanViews();
		}else{
			super.onBackPressed();
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Applications.installedPackages = null;
//		FastimizerConfig.getCleanerConfig(this).save();
//		FastimizerConfig.getMainConfig(this).save();
	}
	
	/**
	 * 检查更新Http完成回调（如果有则打开”应用市场“）
	 */
	@Override
	public void onHttpCompleted(JSONObject result, String url) {
		if(result!=null && result.optInt("versioncode")>Utils.getVersionCode(this)){
			Googleplay.openMarketForUpdate(this);
		}else{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(MainActivity.this, R.string.msg_no_new_version, Toast.LENGTH_LONG).show();					
				}
			});
		}
			
	}
	
	private void updateScore(int score) {
		if(scoreTimers==null){
			scoreTimers = new ArrayList<WinTimer>();
		}
		WinTimer timer = new WinTimer(this);
		timer.setTag(score);
		scoreListAdd(timer, true);
	}
	
	/**
	 * 多线程同步维护定时器列表。
	 * @param timer
	 * @param add
	 */
	private synchronized void  scoreListAdd(WinTimer timer, boolean add){
		
		if(add){
			int count = 0;
			if(scoreTimers.size()==0){
				count = mScoreChart.getValue()-(Integer)timer.getTag();
				if(count!=0){
					timer.setInterVal(1000/Math.abs(count));
					timer.start(true);
				}
			}
			if(count>0){
				scoreTimers.add(timer);
				Log.i("add", String.valueOf(timer.getTag()));
			}
		}else{ 
			if(!scoreTimers.contains(timer))
				return;
			scoreTimers.remove(timer);
			Log.i("del____", String.valueOf(timer.getTag()));

			if(scoreTimers.size()>0){
				timer = scoreTimers.get(0);
				int count = mScoreChart.getValue()-(Integer)timer.getTag();
				if(count!=0){
					timer.setInterVal(1000/Math.abs(count));
					timer.start(true);
				}else{
					scoreTimers.remove(timer);
					Log.i("del zero", String.valueOf(timer.getTag()));
				}
			}
			
		}
	}

	/**
	 * 扫描结果监听对象（用于显示扫描信息）
	 * @author asa
	 *
	 */
	private class ScanResultHandler extends BaseScanResult{

		public boolean running;
		private CfgViewHolder curView = null;
		private boolean goon = true;
		
		private String memStr;
		private String fileStr;
		
		public ScanResultHandler() {
			memStr = getResources().getString(R.string.memoryjunk);
			fileStr = getResources().getString(R.string.filesjunk);
		}
		
		@Override
		public boolean begin(GarbageItem cfg) {
			if(cfgViewMap.containsKey(cfg)){
				init();
				curView = cfgViewMap.get(cfg);
				final CfgViewHolder view = curView;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view.progress.setVisibility(View.VISIBLE);
						view.logo.setVisibility(View.INVISIBLE);
					}
				});
			}

			if(!super.begin(cfg))
				return false;
			else{
				return goon;
			}
		}
		
		@Override
		public void end(GarbageItem cfg) {
			super.end(cfg);
				
			final CfgViewHolder view = cfgViewMap.get(cfg);

			if(view!=null){
				final long thesize = cfg.getSize();   //保存，以防被后台线程修改
				final String s;
				if(cfg.getId()==JunkCfg.ID_BACKGROUNDAPP)
					s = String.format(memStr, FileMem.formatMemory(thesize));
				else
					s = String.format(fileStr, FileMem.formatMemory(thesize));
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view.progress.setVisibility(View.GONE);
						
						view.message.setText(s);
						if(thesize>0){
							view.logo.setImageResource(R.drawable.red);
							updateScore(getScore());
						}else
							view.logo.setImageResource(R.drawable.green);
						view.logo.setVisibility(View.VISIBLE);
					}
				});
			}
		}
		
		@Override
		public boolean found(GarbageItem cfg, String name, long size) {
			super.found(cfg, name, size);
			if(curView!=null){
				final String msg = name;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mTextScanMsg.setText(msg);
					}
				});
			}
			return goon;
		}
	}
	/**
	 * 清理结果监听对象
	 * @author asa
	 *
	 */
	private class CleanResultHandler extends BaseCleanResult{
		public boolean running;
		private CfgViewHolder curView = null;
		private boolean goon = true;
		
		@Override
		public boolean begin(GarbageItem cfg) {
			super.begin(cfg);
			if(cfg instanceof CleanupItem){
				final CfgViewHolder view = cfgViewMap.get(cfg);
				if(view!=null){
					curView = view;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							view.progress.setVisibility(View.VISIBLE);
						}
					});
				}
			}
			return goon;
		}
		
		@Override
		public void end(GarbageItem cfg) {
			super.end(cfg);
				
			if(cfg instanceof CleanupItem){
				if(curView!=null){
					final CfgViewHolder view = curView;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							view.progress.setVisibility(View.GONE);
						}
					});
				}
			}

		}
		
		@Override
		public boolean found(GarbageItem cfg, String name, long size) {
			super.found(cfg, name, size);
			if(size>0){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						mScoreChart.setValue(getScore());						
					}
				});
			}
			if(curView!=null){
				final String msg = name;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mTextScanMsg.setText(msg);
					}
				});
			}
			return goon;
		}

	}
	/**
	 * 为每个清理项创建的View，此对象保存View信息
	 * @author asa
	 *
	 */
	private class CfgViewHolder{
		View view;
		ImageView logo;
		TextView caption;
		ProgressBar progress;
		TextView message;
		
		public CfgViewHolder(View v){
			view = v;
			caption = (TextView)view.findViewById(R.id.text_ms_caption);
			progress = (ProgressBar)view.findViewById(R.id.progressBar_ms_1);
			message = (TextView)view.findViewById(R.id.text_ms_msg);
			logo = (ImageView)view.findViewById(R.id.image_ms_logo);
		}
	}

	/**
	 * 仪表盘View draw回调函数。在仪表盘上附加一些信息
	 */
	private TextPaint scorePaint = null, textPaint;
	@Override
	public void drawView(View view, Canvas canvas) {
		if(scorePaint == null){
			Typeface tf = Typeface.createFromAsset (getAssets() , "fonts/Helvetica LT 33 Thin Extended.ttf" );
			scorePaint = new TextPaint();
			scorePaint.setColor(Color.WHITE);
			scorePaint.setStyle(Paint.Style.FILL);
			scorePaint.setAntiAlias(true); // 消除锯齿  
			scorePaint.setTypeface(tf);
//			paint.setFlags(Paint.ANTI_ALIAS_FLAG); 
			textPaint = new TextPaint();
			textPaint.setColor(Color.WHITE);
			textPaint.setStyle(Paint.Style.FILL);
			textPaint.setAntiAlias(true); // 消除锯齿  
		}
		Paint paint;
		String text=null, text1=null;
		boolean isMsg=false;
		MeterChartView chart = (MeterChartView)view;
		if(view.equals(mStorageChart) || view.equals(mMemChart)){
			text = String.format("%d%%", chart.getValue());
			paint = textPaint;
		}else{
			if(scanhandler==null){
				text = getResources().getString(R.string.checkup);
				text1 = String.format(getResources().getString(R.string.msg_his_totalszie),
						FileMem.formatMemory(FastimizerConfig.getCleanerConfig(this).getTotalSize()));
				paint = textPaint;
				isMsg = true;
			}else{
				text = String.valueOf(mScoreChart.getValue());
				text1 = getResources().getString(R.string.score);
				paint = scorePaint;
			}
		}

		if(text!=null){
			RectF rectF = chart.getRect();
			rectF.inset(rectF.width()*0.25f, 0);
			float textsize;
			if(isMsg)
				textsize = Utils.getTextSize(paint, text, rectF.width());
			else {
				rectF.inset(rectF.width()*0.2f, 0);
				textsize = Utils.getTextSize(paint, "00", rectF.width());
			}
			paint.setTextSize(textsize);
	        Rect rect1 = new Rect();
	        RectF rect = new RectF();
	        paint.getTextBounds(text, 0, text.length(), rect1);
	        rect.set(rect1);
	        float y = rectF.top + (rectF.height() - rect.height())/2f + rect.height();
	        if(text1!=null)
	        	y -= rect.height()/3;
	        float x = rectF.left + (rectF.width() - rect.width()) / 2f - rect.left;
			canvas.drawText(text, x, y, paint);
//			canvas.drawRect(rectF, paint);
//			rect.offset(x, y);
//			canvas.drawRect(rect, paint);
//			canvas.drawRect(chart.getRect(), paint);
			if(text1!=null){
				y += rect.height()*2f/3f;
				float textsize1 = Utils.getTextSize(textPaint, text1, rectF.width());
				if(textsize1>textsize/3.5f)
					textsize1 = textsize/3.5f;
				textPaint.setTextSize(textsize1);
				textPaint.getTextBounds(text1, 0, text1.length(), rect1);
				rect.set(rect1);
				y += rect.height()/3f;
				x = rectF.left + (rectF.width() - rect.width()) / 2f - rect.left;
				canvas.drawText(text1,  x, y, textPaint);
			}
        }
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(inView!=null){
			outView.setVisibility(View.GONE);
			inView.startAnimation(inAnim);
			inView.setVisibility(View.VISIBLE);
			inView = null;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
}
