/****
 * 使用系统服务（ALARM_SERVICE）定时任务。
 * 1、检查新闻消息（早上7点）
 * 2、提醒用户清理垃圾（3天后）
 */
package com.wisecleaner.fastimizer;

import java.util.Date;

import org.json.JSONObject;

import android.R.raw;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wisecleaner.compont.HttpData;
import com.wisecleaner.compont.HttpData.HttpCompleted;
import com.wisecleaner.things.NotificationBar;

public class AlarmTask implements HttpCompleted<JSONObject>{
	
	private NotificationBar notificationBar;            //标题栏
	private Context context;                    

	/*
	public static boolean startService(Context context) {
		Intent startIntent = new Intent(context, WiseCleanerService.class);  
		context.startService(startIntent);
		return true;
	}
	
	public static boolean isServiceRunning(Context context) {
		
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(100);

        if(null == serviceInfos || serviceInfos.size() < 1) {
            return false;
        }

        for(int i = 0; i < serviceInfos.size(); i++) {
            if(serviceInfos.get(i).service.getClassName().contains("com.wisecleaner.service.WiseMessagesService")) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
	*/
	
	/**
	 * 检查新闻
	 */
	public void checkNews() {
		HttpData.asyncGetUrl(FastimizerApp.NEWS_URL, null, this);				
	}
	
	public AlarmTask(Context context) {
		Log.i("WiseCleanerService", "onCreate.." );
		this.context = context;
		
		notificationBar = new NotificationBar();
		notificationBar.initService(context);
	}
	
	/**
	 * 检查新闻
	 * Http完成回调，如果有则在标题栏放图标。
	 */
	@Override
	public void onHttpCompleted(JSONObject result, String url) {
		Log.i("WiseCleanerService", "onCompleted.." );
		if(result==null)
			return;
		int msgid = result.optInt("id");
		if(msgid != FastimizerConfig.getMainConfig(context).getMsgId()){
			Intent intent = new Intent(context, WebViewActivity.class);
			intent.putExtra(WebViewActivity.URL, result.optString("url"));
			
			notificationBar.showIntentActivityNotify(msgid, result.optString("title"), result.optString("content"), "", context, intent, R.drawable.ic_launcher);
			FastimizerConfig.getMainConfig(context).setMsgId(msgid);
		}
	}

	/**
	 * 执行定时检查（新闻、提示清理垃圾）
	 * @param context
	 */
	public static void chckTimer(Context context) {
		Date curDate = new Date();
		if(curDate.getHours()==7){
			new AlarmTask(context).checkNews();
		}
		Date date = FastimizerConfig.getCleanerConfig(context).getLastCleanTime();
		if(date!=null){
			long time = curDate.getTime() - date.getTime();
			int n = (int) (time % (24*60*60*1000));
			if(n>3)
				new AlarmTask(context).notifyClean(n);
		}
	}

	/**
	 * 3天后提示清理垃圾
	 * @param n
	 */
	private void notifyClean(int n) {
		Intent intent = new Intent(context, MainActivity.class);
		notificationBar.showIntentActivityNotify(0, "清理提示", "n天没有清理了", "", context, intent, R.drawable.ic_launcher);
	}

}
