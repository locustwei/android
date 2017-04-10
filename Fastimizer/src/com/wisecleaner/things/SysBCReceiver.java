/**
 * 系统广播接收器。
 * 1、接收启动广播。注册ALARM定时检查新闻等（每30分钟一次)
 */
package com.wisecleaner.things;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wisecleaner.fastimizer.AlarmTask;

public class SysBCReceiver extends BroadcastReceiver {

	private static final String SERVICE_ACTION = "com.wisecleaner.timer_action";
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	String action = intent.getAction();
    	if(action==null)
    		return;
        Log.i("SysBCReceiver", "BroadcastReceiver onReceive here.... " + action);

        if (action.equals(Intent.ACTION_BOOT_COMPLETED) ||
        		action.equals(Intent.ACTION_USER_PRESENT)) {
        	registerCleanerService(context);
        }else if(action.equals(SERVICE_ACTION)){
        	AlarmTask.chckTimer(context);
        }
    }
	
    /**
     * 注册ALARM定时检查新闻等（每30分钟一次)
     * @param context
     */
    public static void registerCleanerService(Context context){
        Log.i("SysBCReceiver", "registerCleanerService was called.." );
        
        PendingIntent alarmSender = null;
//        Intent startIntent = new Intent(context, WiseCleanerService.class);
        Intent startIntent = new Intent();
        startIntent.setAction(SERVICE_ACTION);
        try {
            alarmSender = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("SysBCReceiver", "registerCleanerService failed  " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30*60*1000, alarmSender);
    }    
}
