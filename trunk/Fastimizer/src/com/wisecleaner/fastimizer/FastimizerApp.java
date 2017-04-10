/**
 * 
 */

package com.wisecleaner.fastimizer;

import android.app.Application;

import com.wisecleaner.fastimizer.cleaner.JunkCfg;
import com.wisecleaner.fastimizer.cleaner.JunkCfg.ScanStateEnum;
import com.wisecleaner.things.SysBCReceiver;

public class FastimizerApp extends Application {

	public static final String HOME_URL                = "http://mobile.wisecleaner.com/";
//	public static final String CHECKUPDATE_URL  = "http://mobile.wisecleaner.com/android/update/Fastimizer.htm";
	public static final String CHECKUPDATE_URL  = "http://mobile.wisecleaner.com/update/fastimizer.htm";
	public static final String FEEDBACK_URL          = "http://www.wisecleaner.net/uninstallfeedback/index.php?product=fastimizer&ver=1.0";
	public static final String HELP_URL                   = "http://mobile.wisecleaner.com/android/help/Fastimizer";
//	public static final String FACEBOOK_URL         = "https://facebook.com/plugins/likebox.php?href=https://www.facebook.com/wisecleanersoft&amp;width=292&amp;height=62&amp;colorscheme=light&amp;show_faces=false&amp;header=false&amp;stream=false&amp;show_border=true&amp;appId=1387712684775306";
	public static final String FACEBOOK_URL         = "https://www.facebook.com/227233060688903";
	public static final String TWITTER_URL             = "https://twitter.com/share?original_referer=http://www.wisecleaner.com/&source=tweetbutton&text=Easy to defrag memory and free up more memory with one-click.&url=http://www.wisecleaner.com/&via=wisecleaner";
	public static final String LICENSE_URL              = "http://mobile.wisecleaner.com/EULA/fastimizer.html";
	public static final String NEWS_URL                  = "http://wisecleaner.com/android/fastimizer/news/message.htm";     //新闻地址


	public static final String DATA_PATH               = "wisecleaner/Fastimizer";  //SDCARD数据文件路径
	
	public static JunkCfg cleanerCfg;                      //(清理配置文件)这东西放这里是方便作为参数传递。

	public FastimizerApp(){
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		SysBCReceiver.registerCleanerService(this);
		cleanerCfg = new JunkCfg(this);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public void onApplicationResume() {
		if(cleanerCfg!=null){
			cleanerCfg.getAppCache().setState(ScanStateEnum.noset);
			cleanerCfg.getBackApp().setState(ScanStateEnum.noset);
		}
	}

	public void onApplicationPause() {
		
	}
}
