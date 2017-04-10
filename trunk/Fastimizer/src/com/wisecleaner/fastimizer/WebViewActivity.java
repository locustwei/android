/**
 * 嵌入WebView打开网页窗口
 */
package com.wisecleaner.fastimizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.wisecleaner.compont.ActivityBase;

public class WebViewActivity extends ActivityBase {

	public static final String URL = "url";
	public static final String CAPTION="caption";
	
	private WebView mWebView;
	private Button btnHome;
	private WebViewClient  mWebClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
						
		btnHome = (Button)findViewById(R.id.btn_ac_home);
		mWebView = (WebView)findViewById(R.id.web_w_content);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);

		mWebClient = new WebViewClient();
		mWebView.setWebViewClient(mWebClient);
		mWebView.setWebChromeClient(new WebChromeClient());
		
		String url = getIntent().getStringExtra(URL);
		if(url!=null)
			mWebView.loadUrl(url);
		url = getIntent().getStringExtra(CAPTION);
		if(url!=null)
			btnHome.setText(url);
	}

//	public void Navigatel(String url, String caption) {
//		mWebView.loadUrl(url);
//		btnHome.setText(caption);
//	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_ac_home:
			finish();
			break;
		default:
			break;
		}
	}
	
	public static void openLicense(Context context) {
		Intent intent3 = new Intent(context,WebViewActivity.class);
		intent3.putExtra(WebViewActivity.CAPTION, context.getResources().getString(R.string.userlicense));
    	intent3.putExtra(WebViewActivity.URL, FastimizerApp.LICENSE_URL);
    	context.startActivity(intent3);
	}
}
