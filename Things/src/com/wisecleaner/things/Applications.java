/**
 * App相关函数
 */
package com.wisecleaner.things;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;

import com.wisecleaner.things.FileMem.FileSize;

public final class Applications {	

	public static HashMap<String, AppInfo> installedPackages;          //系统已安装应用
	
	public static class AppInfo{
		private String appname = null;  
		private String packageName;
		private boolean runing;
		
	    private Drawable icon=null; 
	    
		private boolean isSystem;
		private ApplicationInfo info;
		
		public AppInfo(ApplicationInfo app) {
        	info = app;
            packageName = app.packageName;
            isSystem = (app.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM )!= 0;
		}

		public String getAppname(Context context) {
			if(appname==null && context!=null){
				appname = info.loadLabel(context.getPackageManager()).toString();
			}
			return appname;
		}
	
		public String getPackageName() {
			return packageName;
		}
	
		public Drawable getIcon(Context context) {
			if(icon==null)
				icon = info.loadIcon(context.getPackageManager());
			return icon;
		}
	
		public boolean isRuning() {
			return runing;
		}
	
		public boolean isSystem() {
			return isSystem;
		}
	}
	
	private static void initInstalledPackage(Context context) {

		installedPackages = new HashMap<String, AppInfo>();
		
		PackageManager pm = context.getPackageManager();
		
//        List<PackageInfo> packs = pm.getInstalledPackages(0xFFFFFFFF);  
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        
        for(ApplicationInfo app: apps){
        	AppInfo appInfo = new AppInfo(app);
            			
            installedPackages.put(app.packageName, appInfo);
        }
	}
	
	/**
	 * 加载已安装应用。在扫描时用到
	 * @param context
	 */
	public static HashMap<String, AppInfo> getInstalledPackage(Context context) {
		if(installedPackages==null)
			initInstalledPackage(context);
		return installedPackages;
	}


	public static String getCacheDir(Context context, String packageName) {
		try {
			Context c = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
			if(c!=null){
				File cacheFile = c.getCacheDir();
				if((cacheFile!=null) && cacheFile.exists())
					return c.getCacheDir().getAbsolutePath();
			}
			
		} catch (NameNotFoundException e) {
			
		}
		
		return null;
	}
	
	public  static long queryCacheSize(Context context, String packageName) {
		String dir = getCacheDir(context, packageName);
		if(dir==null)
			return 0;
		dir = FileMem.getSdcardPath() + dir;
		return FileMem.getFileSize(dir);
	}
	
	private static Method getPackageSizeInfo = null;
	public  static FileSize queryPacakgeSize(PackageManager pm, String packageName) {
		
		if ( packageName != null){
    		try {
    			if(getPackageSizeInfo==null){
    				if(android.os.Build.VERSION.SDK_INT  < 17)
    					getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
    				else
    					getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, int.class, IPackageStatsObserver.class);
    			}
    			PkgSizeObserver sizeObserver = new PkgSizeObserver();
    			
    			sizeObserver.completed = false;
    			if(android.os.Build.VERSION.SDK_INT  < 17)
    				getPackageSizeInfo.invoke(pm, packageName,  sizeObserver);
    			else
    				getPackageSizeInfo.invoke(pm, packageName, android.os.Process.myUid() / 100000, sizeObserver);
			    while (!sizeObserver.completed) {
					Thread.sleep(10);
				}
			    return sizeObserver.sizeInfo;
			} catch(Exception ex){
        		return null;
        	} 
    	}
    	
		return null;
    }
	
	 private static class PkgSizeObserver extends IPackageStatsObserver.Stub{
		 public FileSize sizeInfo;		
		 public boolean completed = false;
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			if(succeeded){
				sizeInfo = new FileSize();
				sizeInfo.FreeSize = pStats.cacheSize  ; //缓存大小
				sizeInfo.UsedSize = pStats.dataSize  ;  //数据大小 
				sizeInfo.TotalSize =	pStats.codeSize  ;  //应用程序大小
			}
			completed = true;
		}
	}
	 
	public static void KillbackApplications(Context context){

		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);		
		List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
            
		for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
//			android.os.Process.sendSignal(appProcessInfo.pid, android.os.Process.SIGNAL_KILL);
			if(appProcessInfo.processName.equals("com.wisecleaner.fastimizer"))
				continue;
			android.os.Process.killProcess(appProcessInfo.pid);
			am.killBackgroundProcesses(appProcessInfo.processName);
		}
		
		List<ActivityManager.RunningServiceInfo> serviices = am.getRunningServices(0xFFFF);
		for (ActivityManager.RunningServiceInfo serviceInfo : serviices) {
//			android.os.Process.sendSignal(appProcessInfo.pid, android.os.Process.SIGNAL_KILL);
			if(serviceInfo.process.equals("com.wisecleaner.fastimizer"))
				continue;
			android.os.Process.killProcess(serviceInfo.pid);
		}
	 }

	public static void KillbackApplications(ActivityManager am, ActivityManager.RunningAppProcessInfo app){
		android.os.Process.killProcess(app.pid);
		am.killBackgroundProcesses(app.processName);
	}
	
	private static Method deleteApplicationCacheFiles = null; 
	private static Method freeStorageAndNotify = null; 
	
	public static boolean clearCache(PackageManager pm, String packageName) {
		try {
			if(deleteApplicationCacheFiles==null){
				deleteApplicationCacheFiles = pm.getClass().getDeclaredMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
	            deleteApplicationCacheFiles.setAccessible(true);
			}
			CachePackageDataObserver cacheObserver = new CachePackageDataObserver();
        	deleteApplicationCacheFiles.invoke(pm, packageName, cacheObserver);
        	  while (!cacheObserver.completed) {
				Thread.sleep(10);
			}
        	  return cacheObserver.succeeded;
          }
          catch (Exception e) {
            e.printStackTrace();
            return false;
          }
	}
		
	public static boolean clearCache(PackageManager pm) {
		try {
			if(freeStorageAndNotify==null){
				freeStorageAndNotify = pm.getClass().getMethod("freeStorageAndNotify", Long.TYPE, IPackageDataObserver.class);
				if(freeStorageAndNotify==null)
					return false;
				freeStorageAndNotify.setAccessible(true);
			}
			
			Long freeStorageSize = Long.valueOf(FileMem.getEnvironmentSize()-1L);//Long.MAX_VALUE; //
			CachePackageDataObserver cacheObserver = new CachePackageDataObserver();
			freeStorageAndNotify.invoke(pm, freeStorageSize, cacheObserver);
			while (!cacheObserver.completed) {
				Thread.sleep(10);
			}
      	  return cacheObserver.succeeded;
      	  
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    private static class CachePackageDataObserver extends IPackageDataObserver.Stub {
    	public boolean completed = false;
    	public boolean succeeded = false;
        public void onRemoveCompleted(String packageName, boolean succeeded) {
        	completed = true;
        	this.succeeded = succeeded;
        }
    }
 }
