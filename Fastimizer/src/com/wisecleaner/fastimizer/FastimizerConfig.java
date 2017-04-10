/**
 * 设置信息单元（读取、保存设置信息）
 */
package com.wisecleaner.fastimizer;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wisecleaner.things.FileMem;

public class FastimizerConfig {
	private static CleanerConfig cleanerConfig = null;
	private static MainConfig mainConfig = null;

	private static abstract class AppConfig{
		protected String filename;
		private boolean firstrun = false;
		protected JSONObject data;

		protected abstract String getFileName();

		public AppConfig(Context context) {
			filename = context.getFilesDir()+"/"+getFileName();
			String configData = FileMem.readTextFile(filename);
			if(configData==null){
				firstrun = true;
				data = new JSONObject();
			}else {
				try {
					data = new JSONObject(configData);
				} catch (JSONException e) {
					firstrun = true;
					data = new JSONObject();
				}
			}
		}
				
		public boolean IsFirstrun(){
			return firstrun;
		}
	
		public boolean save() {
			firstrun = false;
			if(data==null)
				return false;
			
			String dataString = data.toString();
			return FileMem.saveTextFile(filename, dataString);
		}
	}
	
	/**
	 * 主程序设置信息
	 * @param context
	 * @return
	 */
	public static MainConfig getMainConfig(Context context) {
		if(mainConfig==null){
			mainConfig = new MainConfig(context);
		}
		return mainConfig;
	}
	
	/**
	 * 主程序设置信息
	 * @author asa
	 *
	 */
	public static class MainConfig extends AppConfig{
		private static final String CFG_NAME = "config.cfg";
		
		public MainConfig(Context context) {
			super(context);
		}

		@Override
		protected String getFileName() {
			return CFG_NAME;
		}

		public int getMsgId() {
			return data.optInt("msgid");
		}

		public void setMsgId(int msgid) {
			try {
				data.put("msgid", msgid);
				save();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static CleanerConfig getCleanerConfig(Context context) {
		if(cleanerConfig==null){
			cleanerConfig = new CleanerConfig(context);
		}
		return cleanerConfig;
	}

	/**
	 * 清理相关设置信息
	 * @author asa
	 *
	 */
	public static class CleanerConfig extends AppConfig{
		private static final String CFG_NAME = "clean.cfg";

		public CleanerConfig(Context context) {
			super(context);
		}

		public long getTotalSize() {
			return data.optLong("totalsize");
		}
	
		public void setTotalSize(long size) {
			try {
				data.put("totalsize", size);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected String getFileName() {
			return CFG_NAME;
		}

		public Date getLastCleanTime() {
			long time = data.optLong("lasttime");
			if(time==0)
				return null;
			else
				return new Date(time);
		}

		public void setLastCleanTime(long time) {
			try {
				this.data.put("lasttime", time);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}