
package com.zoepapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

// to store and retrieve some data in the app for long term (even if the app is closed)
public final class SharedPrefsHelper {

	private static final String KEY_FILE_NAME = "zoep_app_prefs";

	public static final String KEY_EMAIL= "key_email";
	public static final String KEY_USER_ID= "key_user_id";
	public static final String KEY_USER_NAME= "key_user_name";
	public static final String KEY_USER_ROLE= "key_user_role";
	public static final String KEY_BCA_ID= "key_bca_Id";
    public static final String KEY_LAST_LOGIN_TIME = "key_last_login_time";
	public static String getValue(final Context ctx,String name) {
		return ctx.getSharedPreferences(
						SharedPrefsHelper.KEY_FILE_NAME, Context.MODE_PRIVATE)
				.getString(name,"");
	}

	public static void setValue(final Context ctx,String name, String value) {
		final SharedPreferences prefs = ctx.getSharedPreferences(
				SharedPrefsHelper.KEY_FILE_NAME, Context.MODE_PRIVATE);
		final Editor editor = prefs.edit();
		editor.putString(name,value);
		editor.commit();
	}

	public static void clearSharedPrefs(final Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(SharedPrefsHelper.KEY_FILE_NAME, Context.MODE_PRIVATE);
		settings.edit().clear().commit();
	}


}
