/**
 * Google应用市场相关函数
 */

package com.wisecleaner.things;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

public class Googleplay {

	public static final int ErrorDialogResultCode     = 1234;
	public static final int REQUEST_CODE_PICK_ACCOUNT = 1235;
	public static final String App_Install_ID    = "678280160447-3pmo15q5grntbbqipe2feevtginp79og.apps.googleusercontent.com";
	public static final String App_Web_ID        = "678280160447-jtorjeu4p3vv3sofh37vodqmh6v82dhi.apps.googleusercontent.com";
	public static final String SCOPE_WISECARE365 = "audience:server:client_id:" + App_Web_ID;
	public static final String APP_ADDRESS       = "https://play.google.com/store/apps/details?id=com.facebook.katana";
	public static final String MARKET_ACTIVITY   = "market://details?id=%s";
	
	public static boolean isAvailable(Activity context){
		/*
		int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (statusCode == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, context, ErrorDialogResultCode);
            dialog.show();
            return false;
        } else {
        	return false;
        }
        */
		return false;
	}
	
	/** Starts an activity in Google Play Services so the user can pick an account */
    public static String pickUserAccount(Activity context) {
    	/*
    	Account[] accounts = AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
    	if(accounts==null)
    		return null;
    	else if(accounts.length>1){
	        String[] accountTypes = new String[]{"com.google"};
	        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
	                accountTypes, false, null, null, null, null);
	        context.startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    		return null;
    	}else {
			return accounts[0].name;
		}
		*/
    	return null;
    }
    
    public static String getAppToken(Context context, String accountName) {
    	/*
    	try {
    		String token = GoogleAuthUtil.getToken(context, accountName, SCOPE_WISECARE365);
    		GoogleAuthUtil.invalidateToken(context, token);
    		return token;
		} catch (UserRecoverableAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GoogleAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	return null;
	}
    
    public static void getAndUseAuthTokenInAsyncTask(Context context, String accountName) {
        AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				return getAppToken((Context)params[0], (String)params[1]);
			}

        };
        task.execute(context, accountName);
    }
	
    public static void openMarketForUpdate(Activity context) {
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(Uri.parse(String.format(MARKET_ACTIVITY, context.getPackageName())));
    	try {
        	context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
		}
	}
}
