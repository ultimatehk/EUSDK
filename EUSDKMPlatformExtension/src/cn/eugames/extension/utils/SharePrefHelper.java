/**
 * sharedPreference缓存持久类
 * @author huangke
 */
package cn.eugames.extension.utils;

import android.app.Activity;
import android.content.SharedPreferences;

public class SharePrefHelper {
	public static void writeStrToSp(Activity activity, String fileName,
			String key, String value) {
		SharedPreferences settings = activity.getSharedPreferences(fileName, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public static void writeIntToSp(Activity activity, String fileName,
			String key, int value) {
		SharedPreferences settings = activity.getSharedPreferences(fileName, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getIntFromSp(Activity activity, String fileName,
			String key) {
		SharedPreferences settings = activity.getSharedPreferences(fileName, 0);
		int value = settings.getInt(key, 0);
		return value;
	}

	public static String getStringFromSp(Activity activity, String fileName,
			String key) {
		SharedPreferences settings = activity.getSharedPreferences(fileName, 0);
		String value = settings.getString(key, "");
		return value;
	}
}
