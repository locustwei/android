/**
 * 清理配置文件解析（配置文件为JSON数据）。
 * 清理为树形结构，                         根
 *              文件                                |          内存
 * 系统缓存|应用垃圾                         |          后台应用
 * （默认）|(由配置文件定义）           |          (默认） 
 *                文件 |  隐私痕迹  
 */
package com.wisecleaner.fastimizer.cleaner;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wisecleaner.fastimizer.FastimizerApp;
import com.wisecleaner.fastimizer.R;
import com.wisecleaner.things.Applications;
import com.wisecleaner.things.FileMem;


public class JunkCfg {
	public static final int ID_MEMORY = 0;      //后台应用内存
	public static final int ID_BACKGROUNDAPP = 1;      //后台应用内存
	public static final int ID_FILE = 10; 
	public static final int ID_APPCACHE = 11;               //应用缓存
	public static final int ID_APPJUNK = 12;                        //应用临时文件	

	public static final String CONFIG_FILENAME = "cleanconfig.cfg";
	
	private static final String ITEM_CLASS = "clazz";                         //分类
	private static final String ITEM_ID = "id";                                   //系统编号（除特定编号外无意义）
	private static final String  DefaultName   = "defaultname";            //显示名
	private static final String TranName = "tranname";                     //多语言翻译字段 （替换DefaultName）       
	private static final String  Recommended  = "recommended";       //推荐清理
	private static final String Items = "items";                                    //子项
	
	//存在判断段
//	private static final String Exists = "exists";                                   
	private static final String PackageName = "packagename";           //app package;
	//file, dir
	
	//文件清理
	private static final String Files = "files";                                          
	private static final String Path = "path";
	private static final String Filter = "filter";                                    //过滤
	private static final String FindInSubFolder = "findinsubfolder";    //是否包含子目录
	private static final String RemoveSubFolder = "removesubfolder";  //是否允许删除子目录
	private static final String Exclude = "exclude";                                //排外条件
	
	//数据库清理（Sqllite）
//	private static final String Db = "db";
	private static final String[] EXCLUDE_PROCESS = new String[]{		//杀不动的进程排外
		"system",
		"android.process.acore",
		"android.process.media",
		
		"com.miui.guardprovider",
		"com.miui.home",
		"com.miui.providers.weather",
		"com.miui.networkassistant",
		"com.miui.securitycenter",
		"com.miui.securitycenter:remote",
		"com.miui.sdk",
		"com.miui.whetstone",
		"com.lbe.security.miui",
		"com.trafficctr.miui",

		"com.xiaomi.dm",
		"com.xiaomi.xmsf",

		"com.android.defcontainer",
		"com.android.inputmethod.latin",
		"com.android.incallui",
		"com.android.phone",
		"com.android.mms",
		"com.android.nfc",
		"com.android.systemui",
		"com.android.settings",
		"com.android.smspush",
		"com.android.server.telecom",
		"com.android.thememanager",
		
		"com.google.android.inputmethod.pinyin",
		"com.google.android.googlequicksearchbox:search",
		"com.google.android.gms.wearable",
		"com.google.android.apps.fitness",
		"com.google.process.gapps",
		"com.google.process.location",
		"com.google.android.gms",
		
		
		"com.amap.android.location",
		"com.bel.android.dspmanager",
		"com.qualcomm.services.location",
		"com.sand.airdroid",
		"com.system.analytics.cpa"};
	
	/*
	localHashSet.add("com.android.bluetooth");
    localHashSet.add("com.android.certinstaller");
    localHashSet.add("com.android.contacts");
    localHashSet.add("com.android.defcontainer");
    localHashSet.add("com.android.deskclock");
    localHashSet.add("com.android.development");
    localHashSet.add("com.android.emailpolicy");
    localHashSet.add("com.android.htcignoreSettings");
    localHashSet.add("com.android.htmlviewer");
    localHashSet.add("com.android.inputmethod.latin");
    localHashSet.add("com.android.launcher");
    localHashSet.add("com.android.magicsmoke");
    localHashSet.add("com.android.packageinstaller");
    localHashSet.add("com.android.protips");
    localHashSet.add("com.android.quicksearchbox");
    localHashSet.add("com.android.server.vpn");
    localHashSet.add("com.android.ignoreSettings");
    localHashSet.add("com.android.spare_parts");
    localHashSet.add("com.android.stk");
    localHashSet.add("com.android.providers.subscribedfeeds");
    localHashSet.add("com.android.contacts");
    localHashSet.add("com.android.providers.contacts");
    localHashSet.add("com.android.providers.downloads");
    localHashSet.add("com.android.providers.drm");
    localHashSet.add("com.android.providers.telephony");
    localHashSet.add("com.android.providers.media");
    localHashSet.add("com.google.android.location");
    localHashSet.add("com.htc.copyright");
    localHashSet.add("com.noshufou.android.su");
    localHashSet.add("com.sec.android.app.lbstestmode");
    localHashSet.add("net.samdroid.samdroidtools");
    localHashSet.add("org.openintents.filemanager");
    localHashSet.add("android.process.media");
    localHashSet.add("com.android.server.vpn.enterprise");
    localHashSet.add("com.android.smspush");
    localHashSet.add("com.android.hiddenmenu");
    localHashSet.add("com.samsung.syncmlservice");
    localHashSet.add("com.sec.android.app.bluetoothtest");
    localHashSet.add("com.android.providers.assisteddialing");
    localHashSet.add("com.samsung.sec.android.application.csc");
    localHashSet.add("com.sec.android.inputmethod");
    localHashSet.add("com.google.android.gsf");
    localHashSet.add("com.android.server.device.enterprise");
    localHashSet.add("com.android.providers.security");
    localHashSet.add("com.sec.android.sCloudRelayData");
    localHashSet.add("com.samsung.vmmhux");
    localHashSet.add("com.sec.android.provider.logsprovide");
    localHashSet.add("com.android.providers.applications");
    localHashSet.add("com.android.providers.userdictionary");
    localHashSet.add("system");
    localHashSet.add("com.android.systemui");
    localHashSet.add("com.android.nfc");
    localHashSet.add("com.android.acore");
    localHashSet.add("com.android.phone");
    localHashSet.add("com.android.voicedialer");
    localHashSet.add("com.android.settings");
    localHashSet.add("com.android.providers.drm");
    localHashSet.add("com.android.providers.settings");
    localHashSet.add("com.htc.settings.accountsync");
    localHashSet.add("com.htc.htcsettingwidgets");*/
	 
	private ArrayList<CleanupItem> cleanCollates = null;
	private CleanupItem memoryRoot, backApp, fileRoot, appCache;
	
	/**
	 * 清理项分类枚举
	 * @author asa
	 *
	 */
//	public static enum CfgObjectClass{
//		collate, files, memory
//	}
	
	/**
	 * 加载SDCard文件
	 */
	public void LoadCfgFile() {
		String path = FileMem.getSdcardPath() + "/" + FastimizerApp.DATA_PATH ;
		File file = new File(path);
		if(file.exists()){
			File[] files = file.listFiles();
			if(files==null)
				return;
			for(int i=0; i<files.length; i++){
				if(files[i].getName().matches(".*.cfg")){
					String data = FileMem.readTextFile(files[i].getPath());
					addCleanCollates(data);
				}
			}
		}
	}
	
	/**
	 * 加载资源（raw）
	 * @param context
	 */
	public void LoadCfgRes(Context context) {
		String data = FileMem.readRawTextFile(context, R.raw.cleanconfig);
		if(data==null)
			return;
		addCleanCollates(data);
	}
	
	/**
	 * 解析配置文件
	 * @param cfg
	 * @return
	 */
	public JunkCfg(Context context){
		cleanCollates = new ArrayList<CleanupItem>();  //根节点两个分类
		//增加默认内存清理————————————————————————————————————
		memoryRoot = new CleanupItem(null);
//		memoryRoot.setName(context.getResources().getString(R.string.cfg_memname));
		cleanCollates.add(memoryRoot);
		
		backApp = new CleanupItem(null);
		backApp.setId(JunkCfg.ID_BACKGROUNDAPP);
		backApp.setName(context.getResources().getString(R.string.cfg_backgroundapp));
		backApp.mExcludes = new ArrayList<String>();
		for(int i=0; i<EXCLUDE_PROCESS.length; i++)
			backApp.mExcludes.add(EXCLUDE_PROCESS[i]);
		memoryRoot.addItem(backApp);
		
		//增加默认缓存清理————————————————————————————————————
		fileRoot = new CleanupItem(null);
//		fileRoot.setName(context.getResources().getString(R.string.cfg_filename));
		fileRoot.mItems = new ArrayList<JunkCfg.CleanupItem>();
		cleanCollates.add(fileRoot);
		
		appCache = new CleanupItem(null);
		appCache.setId(JunkCfg.ID_APPCACHE);
		appCache.setName(context.getResources().getString(R.string.cfg_appcache));
		fileRoot.addItem(appCache);

		LoadCfgRes(context);
		fileRoot.getItems().get(1).setName(context.getResources().getString(R.string.cfg_appjunks));
		LoadCfgFile();
	}
	
	private void addCleanCollates(String data) {
		if(data==null)
			return;
		try {
			JSONObject object = new JSONObject(data);
			addCleanCollates(object);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * JOSN格式配置文件
	 * @param mData
	 * @return
	 */
	private void addCleanCollates(JSONObject mData) {
		if(mData==null)
			return;
		CleanupItem cleanobj = new CleanupItem(mData);
		if(mData.has(ITEM_CLASS)){
			String s = mData.optString(ITEM_CLASS);
			if(s!=null){
				if(s.equalsIgnoreCase("memory") ){
					getMemCollate().addItem(cleanobj);
				}else{
					getFileCollate().addItem(cleanobj);
				}
			}
		}else
			getFileCollate().mItems.add(cleanobj);
	}
	
	public ArrayList<CleanupItem> getCleanCollates() {
		return cleanCollates;
	}
	/**
	 * 计算扫描垃圾的数量
	 * @return
	 */
	public long getSize(){
		if(cleanCollates==null)
			return 0;
		
		long result = 0;
		for(int i=0; i<cleanCollates.size(); i++){
			result += cleanCollates.get(i).getSize();
		}
		
		return result;
	}
	/**
	 * 内存分类
	 * @return
	 */
	public CleanupItem getMemCollate(){
		return memoryRoot;
	}
	/**
	 * 文件分类
	 * @return
	 */
	public CleanupItem getFileCollate(){
		return fileRoot;
	}
	
	public CleanupItem getAppCache() {
		return appCache;
	}
	
	public CleanupItem getBackApp() {
		return backApp;
	}
	
	/**
	 * 扫描结果（同时兼做清理项的基类）
	 * @author asa
	 *
	 */
	public static class GarbageItem {
		private Integer id = null;
		private String name;
		private long size;
		private boolean selected = true;       //选择是否清理
		
		public GarbageItem() {
			
		}
		
		public GarbageItem(String name, long size) {
			this.name = name;
			this.size = size;
		}
		
		public Integer getId() {
			return id;
		}
		
		public void setId(Integer id) {
			this.id = id;
		};
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		public long getSize() {
			return size;
		}
		public void setSize(long size) {
			this.size = size;
		}
		
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		public boolean IsSelected() {
			return this.selected;
		}
	}
	
	/**
	 * 清理项状态枚举
	 * @author asa
	 *
	 */
	public static enum ScanStateEnum {
		noset, scaned,cleaned
	}

	/**
	 * 清理项
	 * @author asa
	 *
	 */
	public static class CleanupItem extends GarbageItem{
		protected JSONObject mData;
		private ArrayList<CleanupItem> mItems;
		private ArrayList<CleanupFile> mFiles;
		private Boolean scanable = null;         // 选择是否扫描
		private ScanStateEnum  state = ScanStateEnum.noset;       //扫描状态
		private CleanupItem pareant;
		
		//collates-------------------------------------------------
		private ArrayList<String> mExcludes = null;       //排外列表
		private String packageame;                                  //
		//--------------------------------------------------------
		
		public CleanupItem(JSONObject jsonObject) {
			mData = jsonObject;
		}

		public Integer getId(){
			if(super.getId()!=null)
				return super.getId();
			
			if(mData==null || !mData.has(ITEM_ID))
				return -1;
			
			return mData.optInt(ITEM_ID);
		}
		
		public String getName(Context context) {
			if(super.getName()==null ){
				String pkname = getPackageame();
				if(pkname!=null && Applications.installedPackages!=null && Applications.installedPackages.containsKey(pkname) && context!=null){
					super.setName(Applications.installedPackages.get(pkname).getAppname(context));
				}else if(mData!=null && mData.has(TranName)){ 
					String tranname = mData.optString(TranName);
					int resid = context.getResources().getIdentifier(tranname, "string", "com.wisecleaner.fastimizer");
					setName(context.getResources().getString(resid));
				}else if(mData!=null && mData.has(DefaultName) ){
					setName(mData.optString(DefaultName));
				}
			}
			return super.getName();
		}

		public long getSize(){
			long result = super.getSize();
			
			if(mItems!=null)
				for(CleanupItem item: mItems)
					result += item.getSize();
			else if(mFiles!=null && mFiles.size()>0) {
				for(CleanupFile file: mFiles)
					result += file.getSize();
			}
			
			return result;
		}

		public ArrayList<CleanupFile> getFiles() {
			if(mFiles==null){
				if(mData==null)
					return null;
				
				try {
					JSONArray items;
//					CfgObjectClass clazz;
					if(mData.has(Files)){
						items = mData.getJSONArray(Files);
					}else 
						return null;
					
					mFiles = new ArrayList<CleanupFile>();
					
					for(int i=0; i<items.length(); i++){
						
						CleanupFile item = new CleanupFile(items.getJSONObject(i));
						item.pareant = this;
						mFiles.add(item);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}			
			return mFiles;
		}
		
		public void setFiles(CleanupFile[] files) {
			if(mFiles==null)
				mFiles = new ArrayList<JunkCfg.CleanupFile>();
			mFiles.clear();
			if(files==null)
				return;
			for(int i=0; i<files.length; i++){
				mFiles.add(files[i]);
				files[i].pareant = this;
			}
		}

		public void setScanable(boolean scanable) {
			this.scanable = scanable;
			if(mData!=null)
				try {
					mData.put(Recommended, scanable);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			
			if(mItems!=null)
				for(CleanupItem item: mItems)
					item.setScanable(scanable);
		}
		
		public boolean IsScanable() {
			if(scanable==null){
				if(mData!=null && mData.has(Recommended))
					scanable = mData.optBoolean(Recommended, true);
			}
			if(scanable==null)
					scanable = true;
			return scanable;
		}
		
		public ScanStateEnum getState() {
			return state;
		}
		
		public void setState(ScanStateEnum state) {
			this.state = state;
		}
		
		public ArrayList<CleanupItem> getItems() {
			if(mItems==null){
				if(mData==null)
					return null;
				
				try {
					JSONArray items;
//					CfgObjectClass clazz;
					if(mData.has(Items)){
						items = mData.getJSONArray(Items);
					}else 
						return null;
					
					mItems = new ArrayList<CleanupItem>();
					
					for(int i=0; i<items.length(); i++){
						CleanupItem item = new CleanupItem(items.getJSONObject(i));
						String pckName = item.getPackageame();
						if(pckName!=null && Applications.installedPackages!=null && !Applications.installedPackages.containsKey(pckName))
							continue; //没有安装跳过；
						item.pareant = this;
						mItems.add(item);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}			
			return mItems;
		}

		public void setItems(CleanupItem[] items) {
			if(mItems==null)
				mItems = new ArrayList<JunkCfg.CleanupItem>();
			mItems.clear();
			if(items==null)
				return;
			setSelected(true);
			for(int i=0; i<items.length; i++){
				mItems.add(items[i]);
				items[i].pareant = this;
				if(!items[i].IsSelected())
					setSelected(false);
			}
		}
		
		public ArrayList<String> getExcludes() {
			if(mExcludes!=null)
				return mExcludes;
						
			if(mData==null || !mData.has(Exclude))
				return null;

			try {
				JSONArray excludes = mData.getJSONArray(Exclude);
				mExcludes = new ArrayList<String>();
				for(int i=0; i<excludes.length(); i++){
					mExcludes.add(excludes.getString(i));
				}
				return mExcludes;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public void setExcludes(ArrayList<String> excludes) {
			if(excludes==null)
				return;
			
			if(mExcludes==null)
				mExcludes = new ArrayList<String>();
			else
				mExcludes.clear();
			this.mExcludes.addAll(excludes);
		}
		
		public String getPackageame() {
			if(packageame==null){
				if(mData!=null)
					packageame = mData.optString(PackageName, null);
			}
			return packageame;
		}
		
		public void setPackageame(String packageame) {
			this.packageame = packageame;
		}
				
					
		public CleanupItem getPareant() {
			return pareant;
		}

		public void addItem(CleanupItem object) {
			if(mItems==null)
				mItems = new ArrayList<JunkCfg.CleanupItem>();
			mItems.add(object);
			object.pareant = this;
		}
		
		public void sortItemBySize() {
			ArrayList<CleanupItem> items = getItems();
			if(items!=null)
				Collections.sort(items, new Comparator<CleanupItem>() {
					@Override
					public int compare(CleanupItem lhs, CleanupItem rhs) {
						return (int) (rhs.getSize() - lhs.getSize());
					}
				});
		}
	}
	
	public static class CleanupFile extends GarbageItem {
		protected JSONObject mData;
		private ArrayList<GarbageItem> scanResult = null;    //扫描结果
		private CleanupItem pareant;
		//files------------------------------------------
		private String[] mPaths = null;                        //扫描路径
		private String[] mFilters = null;                       //过滤条件（正则表达式）
		private Boolean includeSubFolder;
		private Boolean removeSubFolder;
		private ArrayList<String> mExcludes = null;       //排外列表
		//---------------------------------------------------------
		
		public CleanupFile(JSONObject jsonObject) {
			mData = jsonObject;
		}
		
		public CleanupItem getPareant() {
			return pareant;
		}

		public GarbageItem addNewResult() {
			GarbageItem result = new GarbageItem();
			addResult(result);
			return result;
		}
		
		public void addResult(GarbageItem result) {
			if(scanResult==null)
				scanResult = new ArrayList<JunkCfg.GarbageItem>();
			scanResult.add(result);
			
		}

		public void addResult(String name, long size) {
			GarbageItem result = addNewResult();
			result.setName(name);
			result.setSize(size);			
		}
		
		public ArrayList<GarbageItem> getScanResult() {
			return scanResult;
		}
		
		public long getSize(){
			long result = 0;
			
			if(scanResult!=null)
				for(int i=0; i<scanResult.size(); i++)
					result += scanResult.get(i).getSize();
			
			return result;
		}

		public void clearResult() {
			if(scanResult!=null)
				scanResult.clear();
		}

		public String[] getPath() {
			if(mPaths!=null)
				return mPaths;
			
			if(mData==null || !mData.has(Path))
				return null;

			try {
				JSONArray paths = mData.getJSONArray(Path);
				mPaths = new String[paths.length()];
				for(int i=0; i<paths.length(); i++){
					mPaths[i] = paths.getString(i);
				}
				return mPaths;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public void setPaths(String[] paths) {
			this.mPaths = paths;
		}
		
		public String[] getFilters() {
			if(mFilters!=null)
				return mFilters;
						
			if(mData==null || !mData.has(Filter))
				return null;

			try {
				JSONArray filters = mData.getJSONArray(Filter);
				mFilters = new String[filters.length()];
				for(int i=0; i<filters.length(); i++){
					mFilters[i] = filters.getString(i);
				}
				return mFilters;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public void setFilters(String[] filters) {
			this.mFilters = filters;
		}
		
		public ArrayList<String> getExcludes() {
			if(mExcludes!=null)
				return mExcludes;
						
			if(mData==null || !mData.has(Exclude))
				return null;

			try {
				JSONArray excludes = mData.getJSONArray(Exclude);
				mExcludes = new ArrayList<String>();
				for(int i=0; i<excludes.length(); i++){
					mExcludes.add(excludes.getString(i));
				}
				return mExcludes;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public void setExcludes(ArrayList<String> excludes) {
			if(excludes==null)
				return;
			
			if(mExcludes==null)
				mExcludes = new ArrayList<String>();
			else
				mExcludes.clear();
			this.mExcludes.addAll(excludes);
		}

		public boolean IsIncludeSubFolder() {
			if(includeSubFolder!=null)
				return includeSubFolder;
			else if(mData==null){
				return false;
			}else{
				if(!mData.has(FindInSubFolder))
					includeSubFolder = true;
				else
					includeSubFolder = mData.optBoolean(FindInSubFolder);
				return includeSubFolder;
			}
		}
		
		public void setIncludeSubFolder(Boolean includeSubFolder) {
			this.includeSubFolder = includeSubFolder;
		}
		
		public boolean IsRemoveSubFolder(){
			if(removeSubFolder!=null)
				return removeSubFolder;
			else if(mData==null){
				return false;
			}else{
				if(!mData.has(RemoveSubFolder))
					removeSubFolder = true;
				else
					removeSubFolder = mData.optBoolean(RemoveSubFolder);
				return removeSubFolder;
			}
		}
		
		public void setRemoveSubFolder(Boolean removeSubFolder) {
			this.removeSubFolder = removeSubFolder;
		}

	}
	
	/**
	 * 扫描（清理）监视回调
	 * @author asa
	 *
	 */
	public static interface ScanCleanResult{
		public boolean begin(GarbageItem cfg);
		public void end(GarbageItem cfg);
		public boolean found(GarbageItem cfg, String name, long size);
	}
	/**
	 * 扫描监视基础实现（使用时继承）
	 * @author asa
	 *
	 */
	public static class BaseScanResult implements ScanCleanResult{
		protected int count = 0;
		protected long size = 0;
		private GarbageItem curObject;
		
		public BaseScanResult(){
		}
		
		public void init() {
			count = 0;
			size = 0;
		}
		
		/**
		 * 扫描回调（开始）
		 * @param cfg 
		 */
		public boolean begin(GarbageItem cfg) {
			curObject = cfg;
			if(cfg instanceof CleanupFile)
				((CleanupFile)cfg).clearResult();
			else if (cfg instanceof CleanupItem){
			}
			return true;
		}
				
		/**
		 * 扫描回调（结束）
		 * @param cfg 
		 */
		public void end(GarbageItem cfg) {
			if (cfg instanceof CleanupItem)
				((CleanupItem)cfg).setState(ScanStateEnum.scaned);
			curObject = null;
		}

		
		/**
		 * 扫描回调（找到项目)
		 */
		public boolean found(GarbageItem cfg, String name, long size) {
			if(size>=0){
				count++;
				this.size += size;
			}
			
			return true;
		}
		public int getCount() {
			return count;
		}
				
		public long getSize() {
			return size;
		}
						
		public GarbageItem getCurObject() {
			return curObject;
		}
	}

	/**
	 * 
	 * @author asa
	 *
	 */
	public static class BaseCleanResult implements ScanCleanResult{
		protected int count = 0;
		protected long size = 0;
		private GarbageItem curObject;
		/**
		 * 扫描回调（开始）
		 * @param cfg 
		 */
		public boolean begin(GarbageItem cfg) {
			curObject = cfg;
			return true;
		}
				
		/**
		 * 扫描回调（结束）
		 * @param cfg 
		 */
		public void end(GarbageItem cfg) {
			 if (cfg instanceof CleanupItem)
					((CleanupItem)cfg).setState(ScanStateEnum.cleaned);
			curObject = null;
		}

		
		/**
		 * 扫描回调（找到项目)
		 */
		public boolean found(GarbageItem cfg, String name, long size) {
			if(curObject!=null && (curObject instanceof CleanupFile) && ((CleanupFile)curObject).getScanResult()!=null)
				((CleanupFile)curObject).getScanResult().remove(cfg);
			count++;
			this.size += size;
			return true;
		}

		public GarbageItem getCurObject() {
			return curObject;
		}
		
		public int getCount() {
			return count;
		}
		
		public long getSize() {
			return size;
		}
	}
}
