/**
 * 封装R文件获取资源类
 * @author huangke
 */
package cn.eugames.extension.utils;

import android.content.Context;

public class RHelper {
	public static int getLayoutResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "layout",
				context.getPackageName());
	}

	public static int getIdResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "id",
				context.getPackageName());
	}

	public static int getStringResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "string",
				context.getPackageName());
	}

	public static String getStringResNameByName(Context context, String name) {
		int resourceId = context.getResources().getIdentifier(name, "string",
				context.getPackageName());

		if (resourceId == 0) {
			return "";
		} else {
			return context.getResources().getString(resourceId);
		}
	}

	public static String getStringResNameById(Context context, int id) {

		return context.getResources().getString(id);
	}

	public static int getDrawableResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "drawable",
				context.getPackageName());
	}

	public static int getRawResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "raw",
				context.getPackageName());
	}

	public static int getAnimResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "anim",
				context.getPackageName());
	}

	public static int getStyleResIDByName(Context context, String name) {
		return context.getResources().getIdentifier(name, "style",
				context.getPackageName());
	}
}
