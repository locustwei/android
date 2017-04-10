/**
 * ֪ͨ����غ���
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
	 * ��ʼ��Ҫ�õ���ϵͳ����
	 */
	public void initService(Context context) {
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new Builder(context);
	}
	
	/** 
	 * �����ǰ������֪ͨ�� 
	 */
	public void clearNotify(int notifyId){
		mNotificationManager.cancel(notifyId);//ɾ��һ���ض���֪ͨID��Ӧ��֪ͨ
	}
	
	/**
	 * �������֪ͨ��
	 * */
	public void clearAllNotify() {
		mNotificationManager.cancelAll();// ɾ���㷢������֪ͨ
	}
	
	/**
	 * @��ȡĬ�ϵ�pendingIntent,Ϊ�˷�ֹ2.3�����°汾����
	 * @flags����:  
	 * �ڶ�����פ:Notification.FLAG_ONGOING_EVENT  
	 * ���ȥ���� Notification.FLAG_AUTO_CANCEL 
	 */
//	public PendingIntent getDefalutIntent(int flags){
//		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
//		return pendingIntent;
//	}
	
	/** ��ʾ֪ͨ�� */
	public void showNotify(int notifyId, String title, String content, String ticker){
		mBuilder.setContentTitle(title)
				.setContentText(content)
//				.setNumber(number)//��ʾ����
				.setTicker(ticker);//֪ͨ�״γ�����֪ͨ��������������Ч����
		mNotificationManager.notify(notifyId, mBuilder.build());
//		mNotification.notify(getResources().getString(R.string.app_name), notiId, mBuilder.build());
	}
	
	/** ��ʾ����ͼ���֪ͨ�� */
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
//				.setNumber(number)//��ʾ����
				.setStyle(inboxStyle)//���÷��
				.setTicker(ticker);// ֪ͨ�״γ�����֪ͨ��������������Ч����
		mNotificationManager.notify(notifyId, mBuilder.build());
		// mNotification.notify(getResources().getString(R.string.app_name),
		// notiId, mBuilder.build());
	}
	
	/** ��ʾ֪ͨ�������ת��ָ��Activity */
	public void showIntentActivityNotify(int notifyId, String title, String content, String ticker, Context context, Intent intent, int res){
//		Notification mNotification = new Notification();//Ϊ�˼������⣬���ø÷��������Զ�����BUILD��ʽ����
//		Notification mNotification  = new Notification.Builder(this).getNotification();//���ַ�ʽ�Ѿ���ʱ
//		//PendingIntent ��ת����
		PendingIntent pendingIntent=PendingIntent.getActivity(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
		mBuilder.setSmallIcon(res)
				.setTicker(ticker)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(pendingIntent);
		Notification mNotification = mBuilder.build();
		//����֪ͨ  ��Ϣ  ͼ��  
		mNotification.icon = res;
		//��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;//FLAG_ONGOING_EVENT �ڶ�����פ�����Ե���������������ȥ��  FLAG_AUTO_CANCEL  ������������ȥ��
		//������ʾ֪ͨʱ��Ĭ�ϵķ������𶯡�LightЧ��  
		mNotification.defaults = Notification.DEFAULT_VIBRATE;
		//���÷���֪ͨ��ʱ��  
		mNotification.when=System.currentTimeMillis(); 
		mNotification.flags = Notification.FLAG_AUTO_CANCEL; //��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
		mNotificationManager.notify(notifyId, mNotification);
	}
	
}
