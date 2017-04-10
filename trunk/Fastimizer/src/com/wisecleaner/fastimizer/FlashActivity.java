package com.wisecleaner.fastimizer;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.wisecleaner.compont.ActivityBase;
import com.wisecleaner.things.Utils;
import com.wisecleaner.views.PointsDraw;

public class FlashActivity extends ActivityBase {

	private ArrayList<View> views;
	private Button btnOk;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flash);
		
		views = new ArrayList<View>();
		View view = Utils.createView(this, R.layout.fram_flash_1, null);
		views.add(view);
		
		view = Utils.createView(this, R.layout.fram_flash_2, null);
		btnOk = (Button)view.findViewById(R.id.btn_ff_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});
		
		((CheckBox)view.findViewById(R.id.ckb_ff_agree)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				btnOk.setEnabled(isChecked);
			}
		});
		
		TextView textEula = (TextView)view.findViewById(R.id.text_ff_eula);
		textEula.setText(Html.fromHtml("[<u>EULA</u>]"));
		textEula.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebViewActivity.openLicense(FlashActivity.this);
			}
		});
		
		((PointsDraw)view.findViewById(R.id.points_ff_bottom)).setSelecIndex(1);

		views.add(view);
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_f_1); 
		viewPager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}
			
			@Override
			public Object instantiateItem(ViewGroup  container, int position) {
				container.addView(views.get(position)); 
				return views.get(position);
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(views.get(position)); 
			}
			
			
		});
	}
	
}
