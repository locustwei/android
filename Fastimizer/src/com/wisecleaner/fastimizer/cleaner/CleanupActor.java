/**
 * 执行扫描、清理动作
 */

package com.wisecleaner.fastimizer.cleaner;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Debug.MemoryInfo;

import com.wisecleaner.fastimizer.cleaner.JunkCfg.CleanupFile;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.CleanupItem;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.GarbageItem;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.ScanCleanResult;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.ScanStateEnum;
import com.wisecleaner.things.Applications;
import com.wisecleaner.things.Applications.AppInfo;
import com.wisecleaner.things.FileMem;
import com.wisecleaner.things.FileMem.FileSize;

public class CleanupActor {
		
	private static String PATH_SDCARD = "sdcard";        //清理项指定SDCard，由于系统不同映射具体路径有别。
	
	/**
	 * 全部扫描
	 * @param context
	 * @param handler
	 */
	public static void startScan(Context context, JunkCfg cfg, ScanCleanResult result) {
		if(cfg.getCleanCollates()!=null){
			for(CleanupItem cleanItem :cfg.getCleanCollates()){
				if(!scanCleanItem(context, cleanItem, result))
					return;
			}
		}
		
	}
	
	/**
	 * 清理
	 * @param context
	 * @param handler
	 * @param cfg
	 */
	public static void startClean(Context context, JunkCfg cfg, ScanCleanResult result) {
		if(cfg.getCleanCollates()!=null){
			for(CleanupItem cleanItem :cfg.getCleanCollates()){
				if(!cleanCleanItem(context, cleanItem, result))
					return;
			}
		}
	}
	
	/**
	 * 扫描后台进程内存占用
	 * @param context
	 * @param handler
	 */
	public static boolean scanBackgroundApp(Context context, ScanCleanResult result, CleanupItem cfg) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);		
		List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
		
		ArrayList<String> exclusions = cfg.getExcludes();
		
		boolean br = true;
		
		ArrayList<CleanupItem> objects = new ArrayList<JunkCfg.CleanupItem>();

		for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
			if(appProcessInfo.processName.equals(context.getPackageName()))       //自己
				continue;
			
			if(exclusions!=null){   //排外
				if(exclusions.contains(appProcessInfo.processName))
					continue;
			}
			
			AppInfo app = Applications.getInstalledPackage(context).get(appProcessInfo.processName);
			if(app==null)
				for(int i=0; i<appProcessInfo.pkgList.length; i++){
					app = Applications.getInstalledPackage(context).get(appProcessInfo.pkgList[i]);
					if(app!=null){
						if(exclusions!=null){   //排外
							if(exclusions.contains(appProcessInfo.pkgList[i])){
								app = null;
								continue;
							}
						}

						break;
					}
				}
			
			if(app==null)
				continue;
			
			long size = -1;
			MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{appProcessInfo.pid});
			if(memoryInfo != null && memoryInfo[0] != null && memoryInfo[0].getTotalPrivateDirty() > 0){
				size = (long)memoryInfo[0].getTotalPrivateDirty() * 1024;
				

				CleanupItem object = new CleanupItem(null);
				object.setPackageame(appProcessInfo.processName);
				object.setSize(size);
				object.setName(app.getAppname(context));
				objects.add(object);
			}
			
			if(result!=null){
				br = result.found(cfg, appProcessInfo.processName, size);
				if(!br)
					break;
			}
		}
		if((objects.size()>0)&&
			cfg.getState()==ScanStateEnum.noset)   //这个条件是为了作假
		{
			cfg.setItems(objects.toArray(new CleanupItem[]{}));
		}
		
		return br;
	}
	
	/**
	 * 结束后台运行进程
	 */
	public static boolean cleanBackgroundApp(Context context, ScanCleanResult result, CleanupItem cfg) {
		boolean br = true;
		
		if(cfg!=null && cfg.getItems()!=null){
			ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);		
			List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
			
			ArrayList<CleanupItem> tmp = new ArrayList<JunkCfg.CleanupItem>();
			tmp.addAll(cfg.getItems());
			
			for(CleanupItem item: tmp){
				if(!item.IsSelected())
					continue;
				for(ActivityManager.RunningAppProcessInfo app: appProcessList){
					if(app.processName.equals(item.getPackageame())){
						Applications.KillbackApplications(am, app);
						break;
					}
				}
				cfg.getItems().remove(item);
				if(result!=null){
					br = result.found(item, item.getName(), item.getSize());
					if(!br)
						break;
				}
			}
		}
		
		return br;
	}
	
	/**
	 * 扫描应用缓存
	 * @param context
	 * @param handler
	 * @param packs
	 * @param pm
	 */
	public static boolean scanAppCache(Context context, ScanCleanResult result, CleanupItem cfg) {
		boolean br = true;
		PackageManager pm = context.getPackageManager();

		ArrayList<String> exclusions = cfg.getExcludes();
		ArrayList<CleanupItem> objects = new ArrayList<JunkCfg.CleanupItem>();

		for(String packageName : Applications.getInstalledPackage(context).keySet()) {     
			
			if(exclusions!=null){
				if(exclusions.contains(packageName))
					continue;
			}
			
			long size = -1;
			FileSize fileSize = Applications.queryPacakgeSize(pm, packageName);
			
			
			if(fileSize!=null && fileSize.FreeSize>0){
				size = fileSize.FreeSize;
				CleanupItem object = new CleanupItem(null);
				object.setPackageame(packageName);
				object.setSize(size);;
				objects.add(object);
			}
			
			if(result!=null){
				if(!result.found(cfg, packageName, size)){
					br = false;
					break;
				}
			}
		}
		if((objects.size()>0)&&
				cfg.getState()==ScanStateEnum.noset)   //这个条件是为了作假
		{
			cfg.setItems(objects.toArray(new CleanupItem[]{}));
		}
		return br;
				
	}

	/**
	 * 删除应用缓存
	 * @param context
	 * @param handler
	 * @param cfg
	 */
	public static boolean cleanAppCache(Context context, ScanCleanResult result, CleanupItem cfg) {
		boolean br = true;
		if(cfg!=null && cfg.IsSelected() && cfg.getItems()!=null){
			PackageManager pm = context.getPackageManager();
			Applications.clearCache(pm);     //没有权限下面的动作只是做做样子。

			ArrayList<CleanupItem> tmp = new ArrayList<JunkCfg.CleanupItem>();
			tmp.addAll(cfg.getItems());
			
			for(CleanupItem item: tmp){
				Applications.clearCache(pm, item.getPackageame());  //需要有SysApp权限
				
				if(result!=null){
					br = result.found(item, item.getName(), item.getSize());
					if(!br)
						break;
				}

				FileSize fileSize = new FileSize();//Applications.queryPacakgeSize(pm, item.getPackageame());
				if(fileSize!=null)
					item.setSize(fileSize.FreeSize);
			}
		}
		
		return br;
	}
	
	/**
	 * 扫描定义类别
	 * @param cfg
	 * @param handler
	 */
	public static boolean scanCleanItem(Context context, CleanupItem cfg, ScanCleanResult result) {
		if(cfg==null )  //|| cfg.getState()==ScanStateEnum.scaned
			return true;
		if(result!=null){
			if(!result.begin(cfg)){
				result.end(cfg);
				return false;
			}
		}
		
		boolean br = true;
		
		switch (cfg.getId()) {
		case JunkCfg.ID_APPCACHE:
			br = scanAppCache(context, result, cfg);
			break;
		case JunkCfg.ID_BACKGROUNDAPP:
			br = scanBackgroundApp(context, result, cfg);
			break;
		default:
			ArrayList<CleanupItem> items = cfg.getItems();
			if(items!=null && items.size()>0){
				for(CleanupItem item: items){
					if(!item.IsScanable())
						continue;
					br = scanCleanItem(context, item, result);
					if(!br)
						break;
				}
			}
			
			ArrayList<CleanupFile> files = cfg.getFiles();
			if(files!=null && files.size()>0){
				for(CleanupFile file: files){
					br = scanCleanFile(file, result);
					if(!br)
						break;
				}
			}
			break;
		}
		
		if(result!=null)
			result.end(cfg);
		return br;
	}
	
	/**
	 * 清理垃圾项
	 * @param cfg
	 * @param handler
	 */
	public static boolean cleanCleanItem(Context context, CleanupItem cfg, ScanCleanResult result) {
		if(result!=null){
			if(!result.begin(cfg)){
				result.end(cfg);
				return false;
			}
		}
		boolean br = true;

		switch (cfg.getId()) {
		case JunkCfg.ID_APPCACHE:
			br = cleanAppCache(context, result, cfg);
			break;
		case JunkCfg.ID_BACKGROUNDAPP:
			br = cleanBackgroundApp(context, result, cfg);
			break;
		default:
			ArrayList<CleanupItem> items = cfg.getItems();
			if(items!=null && items.size()>0){
				for(CleanupItem item: items){
					br = cleanCleanItem(context, item, result);
					if(!br)
						break;
				}
			}

			if(cfg.IsSelected()){
				ArrayList<CleanupFile> files = cfg.getFiles();
				if(files!=null && files.size()>0){
					for(CleanupFile file: files){
						br = cleanCleanFile(file, result);
						if(!br)
							break;
					}
				}
			}
			break;
		}

		if(result!=null)
			result.end(cfg);
		return br;
	}
	
	/**
	 * 配置文件扫描
	 * @param cleanFile
	 * @param handler
	 */
	private static boolean scanCleanFile(CleanupFile cleanFile, ScanCleanResult result) {
		if(result!=null){
			if(!result.begin(cleanFile))
				return false;
		}
		boolean br = true;
		for(int i=0; i<cleanFile.getPath().length; i++){
			String path = cleanFile.getPath()[i];
			if(path==null || path.equals(""))
				continue;
			
			if(!path.endsWith("/"))
				path = path +"/";
			if(path.startsWith(PATH_SDCARD)){
				String sdpath = FileMem.getSdcardPath();
				if(sdpath==null)
					continue;
				path = path.replaceFirst(PATH_SDCARD, sdpath);
			}

			File pathFile = new File(path);
			if(pathFile.exists()){
				pathFile.listFiles(new CfgFileFilter(cleanFile, result));
			}
		}
		
		result.end(cleanFile);
		
		return br;
	}

	/**
	 * 删除垃圾文件
	 * @param cleanFile
	 * @param handler
	 */
	private static boolean cleanCleanFile(CleanupFile cleanFile, ScanCleanResult result) {
		if(result!=null){
			if(!result.begin(cleanFile))
				return false;
		}
		
		if(!cleanFile.IsSelected())
			return true;

		boolean br = true;
		if(cleanFile!=null && cleanFile.getScanResult()!=null){
			ArrayList<GarbageItem> tmp = new ArrayList<JunkCfg.GarbageItem>();
			tmp.addAll(cleanFile.getScanResult());
			for(GarbageItem item: tmp){
				if(item.IsSelected()){
					File file = new File(item.getName());
					if(file.exists()){
						if(file.isFile() && file.delete()){
							if(result!=null){
								br = result.found(item, item.getName(), item.getSize());
								if(!br)
									break;
							}
						}else if(file.isDirectory() && cleanFile.IsRemoveSubFolder() && file.delete()){
							if(result!=null){
								br = result.found(item, item.getName(), item.getSize());
								if(!br)
									break;
							}
						}
					}
				}
			}
		}
		if(result!=null)
			result.end(cleanFile);
		return br;
	}
	
	/**
	 * 文件过滤类
	 * @author asa
	 *
	 */
	private static class CfgFileFilter implements FileFilter {
		private CleanupFile  mCfg;
		private String[] filter;
		private ArrayList<String> Exclude;
		private ScanCleanResult mHandler;
		
		public CfgFileFilter(CleanupFile  cleanFile, ScanCleanResult result) {
			mCfg = cleanFile;
			filter = cleanFile.getFilters();
			Exclude= cleanFile.getExcludes();
			mHandler = result;
		}

		@Override
		public boolean accept(File file) {
			boolean result = false;

			do{
				if(file.isDirectory() ){
					if( !mCfg.IsIncludeSubFolder()){
						result = false;
						break;
					}else{
						file.listFiles(this);  //递归子目录
						result = true;
						break;
					}
				}else if(filter==null || filter.length==0)
					result = true;
				else{
					for (int i = 0; i < filter.length; i++) {
						//if(file.getName().matches(filter[i])){
						if(file.getPath().matches(filter[i])){
							result = true;
							break;
						}
					}
				}
				
				if(result && Exclude!=null ){
					if(Exclude.contains(file.getName()))
						result = false;
				}
			}while(false);
			
			long size = -1;
			
			if(result){
				if(!file.isDirectory())
					size = file.length();
				else if(mCfg.IsRemoveSubFolder()){
					size = 0;
				}
				if(size!=-1){
					mCfg.addResult(file.getPath(), size);
				}
			}
			if(mHandler!=null)
				mHandler.found(mCfg, file.getPath(), size);
			
			return result;
		}
		
	}
}
