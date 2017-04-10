/**
 * …Ë÷√¥∞ø⁄
 */
package com.wisecleaner.fastimizer;

import android.os.Bundle;
import android.view.View;

import com.wisecleaner.compont.ActivityBase;

public class SettingsActivity extends ActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
//		TextView v = (TextView)findViewById(R.id.text_ac_caption);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ac_home:
			finish();
			break;

		default:
			break;
		}
	}
}	
