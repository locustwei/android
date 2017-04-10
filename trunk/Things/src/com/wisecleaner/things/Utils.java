/**
 * 其他公用函数
 */
package com.wisecleaner.things;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Utils {
	/**
	 * 根据资源创建视图
	 * @param context
	 * @param resid
	 * @param parent
	 * @return
	 */
	public static View createView(Context context, int resid, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(resid, parent, false);
	}
	
	 /**
	  * 判断网络是否可用
	  * @param context
	  * @return
	  */
	public static boolean isDeviceOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
	/**
	 * App 版本
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return 0;
	}
	
	public static String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return "";
	}
	
	/**
	 * 计算适应宽度的字符串，字体大小
	 * @param paint
	 * @param text
	 * @param width
	 * @return
	 */
	public static float getTextSize(Paint paint, String text, float width) {
		int result = 1;
		boolean found_desired_size = true;

		while (found_desired_size){
		    paint.setTextSize(result++);
		    float w = paint.measureText(text);
		    if(w>=width)
		    	return result;
		}
		return 0;  
	}
	
	/**
	 * dip换算成像数
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	/**
	 * 媒体文件Uri换成文件实际路径
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromUri(Context context, Uri contentUri) {
		if(contentUri==null)
			return null;
		
	    Cursor cursor = null;
	    try {
	        String[] proj = { MediaStore.Images.Media.DATA };
	        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    } finally {
	        if (cursor != null) {
	            cursor.close();
	        }
	    }
	}

	/**
	 * 打开facebook app
	 * @param context
	 * @param post
	 * @return
	 */
	public static boolean OpenFacebookApp(Context context, String post) {
		Intent intent1;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
			if(packageInfo==null)
				return false;
			intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(post));
		} catch (NameNotFoundException e) {
			return false;
		}
		
        context.startActivity(intent1);
        return true;
	}
	
	/**
	 * 打开twitter app
	 * @param context
	 * @param post
	 * @return
	 */
	public static boolean OpenTwitterApp(Context context, String post) {
		Intent intent2;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.twitter.android", 0);
			if(packageInfo==null)
				return false;
			intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(post));
			intent2.setPackage("com.twitter.android");
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		} catch (NameNotFoundException e) {
			return false;			
		}
	    
		context.startActivity(intent2);
		return true;
	}
}
