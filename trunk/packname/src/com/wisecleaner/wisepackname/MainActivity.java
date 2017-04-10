package com.wisecleaner.wisepackname;

import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wisecleaner.things.Applications;
import com.wisecleaner.things.Applications.AppInfo;
import com.wisecleaner.things.FileMem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
	
	public void OnClick(View v){
		String Value = "";
		Iterator iter = Applications.getInstalledPackage(this).entrySet().iterator();
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    String key = (String)entry.getKey(); 
		    AppInfo val = (AppInfo)entry.getValue();
		    
		    Value += key + "  " + val.getAppname(this) +"\n";
		} 
		
		FileMem.saveTextFile("sdcard/packagenames.txt", Value);
	}
	
}
