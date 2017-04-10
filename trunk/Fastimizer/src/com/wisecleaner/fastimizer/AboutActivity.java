/**
 * About ´°¿Ú
 */
package com.wisecleaner.fastimizer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wisecleaner.compont.ActivityBase;
import com.wisecleaner.things.Utils;

public class AboutActivity extends ActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		
		TextView v = (TextView)findViewById(R.id.text_ac_caption);
		v.setText(R.string.about);
		
		v = (TextView)findViewById(R.id.text_a_version);
		v.setText(getResources().getString(R.string.version).toString() + Utils.getVersionName(this));
	}
	/*
	public void share(String nameApp, String imagePath, String message) {
	    try {
	        List<Intent> targetedShareIntents = new ArrayList<Intent>();
	        Intent share = new Intent(android.content.Intent.ACTION_SEND);
	        share.setType("image/jpeg");
	        List<ResolveInfo> resInfo = getPackageManager()
	                .queryIntentActivities(share, 0);
	        if (!resInfo.isEmpty()) {
	            for (ResolveInfo info : resInfo) {
	                Intent targetedShare = new Intent(
	                        android.content.Intent.ACTION_SEND);
	                targetedShare.setType("image/jpeg"); // put here your mime
	                                                        // type
	                if (info.activityInfo.packageName.toLowerCase().contains(
	                        nameApp)
	                        || info.activityInfo.name.toLowerCase().contains(
	                                nameApp)) {
	                    targetedShare.putExtra(Intent.EXTRA_SUBJECT,
	                            "Sample Photo");
	                    targetedShare.putExtra(Intent.EXTRA_TEXT, message);
//	                    targetedShare.putExtra(Intent.EXTRA_STREAM,
//	                            Uri.fromFile(new File(imagePath)));
	                    targetedShare.setPackage(info.activityInfo.packageName);
	                    targetedShareIntents.add(targetedShare);
	                    break;
	                }
	            }
	            Intent chooserIntent = Intent.createChooser(
	                    targetedShareIntents.remove(0), "Select app to share");
	            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
	                    targetedShareIntents.toArray(new Parcelable[] {}));
	            startActivity(chooserIntent);
	        }
	    } catch (Exception e) {
//	        Log.v("VM",
//	                "Exception while sending image on" + nameApp + " "
//	                        + e.getMessage());
	    }
	}
	*/
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ac_home:
			finish();
			break;
		case R.id.relativeL_a_homepage:
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FastimizerApp.HOME_URL));
			startActivity(intent);
			break;
		case R.id.relativeL_a_facebook:
			Intent intent1;
			String uri = "fb://page/227233060688903"; 
			if(!Utils.OpenFacebookApp(this, uri)){
				intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(FastimizerApp.FACEBOOK_URL));
				startActivity(intent1);
			}
			break;
		case R.id.relativeL_a_twitter:
			Intent intent2;
			if(!Utils.OpenTwitterApp(this, FastimizerApp.TWITTER_URL)){
				intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(FastimizerApp.TWITTER_URL));
				startActivity(intent2);
			}
			break;
		case R.id.relativeL_a_license:
			WebViewActivity.openLicense(this);
	    	break;
		default:
			break;
		}
	}
}
