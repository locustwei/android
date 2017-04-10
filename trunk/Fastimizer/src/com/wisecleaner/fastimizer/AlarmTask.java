/****
 * ʹ��ϵͳ����ALARM_SERVICE����ʱ����
 * 1�����������Ϣ������7�㣩
 * 2�������û�����������3���
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
	
	private NotificationBar notificationBar;            //������
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
	 * �������
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
	 * �������
	 * Http��ɻص�����������ڱ�������ͼ�ꡣ
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
	 * ִ�ж�ʱ��飨���š���ʾ����������
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
	 * 3�����ʾ��������
	 * @param n
	 */
	private void notifyClean(int n) {
		Intent intent = new Intent(context, MainActivity.class);
		notificationBar.showIntentActivityNotify(0, "������ʾ", "n��û��������", "", context, intent, R.drawable.ic_launcher);
	}

}
