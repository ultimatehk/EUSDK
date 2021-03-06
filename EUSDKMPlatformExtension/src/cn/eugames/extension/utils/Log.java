/**
 * 封装Log类，打Log均使用此类
 * 
 * @author huangke
 *
 */
package cn.eugames.extension.utils;

public class Log {

	public static final String LOG_SDK = "EUSDK----->";
	public static final String LOG_MOUDLE = "EUSDK_MODULE";
	public static final String LOG_PERSONAL = "HuangKe----->";

	private static boolean isDebugLog = false;

	public static void setDebugLog(boolean log) {
		isDebugLog = log;
	}

	public static void d(String tag, String msg) {
		if (isDebugLog || VersionUtil.deBug())
			android.util.Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (isDebugLog || VersionUtil.deBug())
			android.util.Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebugLog || VersionUtil.deBug())
			android.util.Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		e(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebugLog || VersionUtil.deBug())
			android.util.Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (isDebugLog || VersionUtil.deBug())
			android.util.Log.w(tag, msg);
	}
}
