/**
 * 通知栏相关函数
 */
package com.wisecleaner.things;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class NotificationBar{
	public NotificationManager mNotificationManager;
	public NotificationCompat.Builder mBuilder;
	/**
	 * 初始化要用到的系统服务
	 */
	public void initService(Context context) {
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new Builder(context);
	}
	
	/** 
	 * 清除当前创建的通知栏 
	 */
	public void clearNotify(int notifyId){
		mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
	}
	
	/**
	 * 清除所有通知栏
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// 删除你发的所有通知
	}
	
	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:  
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT  
	 * 点击去除： Notification.FLAG_AUTO_CANCEL 
	 */
//	public PendingIntent getDefalutIntent(int flags){
//		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
//		return pendingIntent;
//	}
	
	/** 显示通知栏 */
	public void showNotify(int notifyId, String title, String content, String ticker){
		mBuilder.setContentTitle(title)
				.setContentText(content)
//				.setNumber(number)//显示数量
				.setTicker(ticker);//通知首次出现在通知栏，带上升动画效果的
		mNotificationManager.notify(notifyId, mBuilder.build());
//		mNotification.notify(getResources().getString(R.string.app_name), notiId, mBuilder.build());
	}
	
	/** 显示大视图风格通知栏 */
	public void showBigStyleNotify(int notifyId, String title, String content, String ticker) {
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		String[] events = new String[5];
		// Sets a title for the Inbox style big view
		inboxStyle.setBigContentTitle(title);
		// Moves events into the big view
		for (int i=0; i < events.length; i++) {
		    inboxStyle.addLine(events[i]);
		}
		mBuilder.setContentTitle(title)
				.setContentText(content)
//				.setNumber(number)//显示数量
				.setStyle(inboxStyle)//设置风格
				.setTicker(ticker);// 通知首次出现在通知栏，带上升动画效果的
		mNotificationManager.notify(notifyId, mBuilder.build());
		// mNotification.notify(getResources().getString(R.string.app_name),
		// notiId, mBuilder.build());
	}
	
	/** 显示通知栏点击跳转到指定Activity */
	public void showIntentActivityNotify(int notifyId, String title, String content, String ticker, Context context, Intent intent, int res){
//		Notification mNotification = new Notification();//为了兼容问题，不用该方法，所以都采用BUILD方式建立
//		Notification mNotification  = new Notification.Builder(this).getNotification();//这种方式已经过时
//		//PendingIntent 跳转动作
		PendingIntent pendingIntent=PendingIntent.getActivity(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		mBuilder.setSmallIcon(res)
				.setTicker(ticker)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(pendingIntent);
		Notification mNotification = mBuilder.build();
		//设置通知  消息  图标  
		mNotification.icon = res;
		//在通知栏上点击此通知后自动清除此通知
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;//FLAG_ONGOING_EVENT 在顶部常驻，可以调用下面的清除方法去除  FLAG_AUTO_CANCEL  点击和清理可以去调
		//设置显示通知时的默认的发声、震动、Light效果  
		mNotification.defaults = Notification.DEFAULT_VIBRATE;
		//设置发出通知的时间  
		mNotification.when=System.currentTimeMillis(); 
		mNotification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		mNotificationManager.notify(notifyId, mNotification);
	}
	
}
