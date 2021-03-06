/**
 * 获取设备信息
 * @author huangke
 *
 */
package cn.eugames.extension.crashreporter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class DeviceInfo {

	public static String getMacAddr(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService("wifi");
		WifiInfo info = wifi.getConnectionInfo();
		String macAddress = info.getMacAddress();
		return macAddress == null ? "" : macAddress;
	}

	public static String getSimOperator(Context context) {
		TelephonyManager telephonyMgr = (TelephonyManager) context
				.getSystemService("phone");
		return telephonyMgr.getSimOperator() + "";
	}

	public static String getDeviceVendor(Context context) {
		return Build.MANUFACTURER;
	}

	public static String getDeviceBrand(Context context) {
		String s = Build.MODEL;
		if (!TextUtils.isEmpty(s)) {
			s = s.replaceAll(" ", "");
		}

		return s == null ? "" : s;
	}

	public static String getDeviceModel(Context context) {

		String s = Build.MODEL;
		if (!TextUtils.isEmpty(s)) {
			s = s.replaceAll(" ", "");
		}
		return s == null ? "" : s;
	}

	public static String getOsName() {
		return "Android";
	}

	public static String getOsVersion(Context context) {
		return Build.VERSION.RELEASE;
	}

	public static String getOsSdkVersion(Context context) {
		return Build.VERSION.SDK;
	}

	public static String getOsApiLevel(Context context) {
		return Build.VERSION.RELEASE;
	}

	public static int getApiLevel(Context context) {
		int apiLevel = Build.VERSION.SDK_INT;
		return apiLevel;
	}

	public static String getFirmwareVersion(Context context) {
		String firmware = Build.MODEL + "_";
		firmware = firmware + Build.FINGERPRINT + "_";
		firmware = firmware + Build.RADIO;
		return firmware;
	}

	public static String getLeosApiLevel(Context context) {
		return Build.DISPLAY;
	}

	public static String getSource(Context context) {
		String name = context.getPackageName();
		String version;
		try {
			version = context.getPackageManager().getPackageInfo(name, 0).versionName;
		} catch (Exception e) {
			version = "unknown";
		}
		return "android:" + name + "-" + version;
	}

	public static String getLanguage(Context context) {
		Configuration configuration = context.getResources().getConfiguration();
		String lang = configuration.locale.toString().replace('_', '-');
		return lang;
	}

	public static String getImei(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return TextUtils.isEmpty(imei) ? "-1" : imei;
	}

	public static String getResolution(Activity activity) {
		int screenWidth = activity.getWindowManager().getDefaultDisplay()
				.getWidth();
		int screenHeight = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		if (screenWidth > screenHeight) {
			return screenWidth + "*" + screenHeight;
		}
		return screenHeight + "*" + screenWidth;
	}

	public static String getSdkVersion() {
		return "v1.8";
	}

	public static String getAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 64);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

	private static String getWeiboVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo("com.sina.weibo", 64);
			int code = info.versionCode;
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static double getTotalInternalMemorySize() {
		long mTotal;
		// /proc/meminfo读出的内核信息进行解释
		String path = "/proc/meminfo";
		String content = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path), 8);
			String line;
			if ((line = br.readLine()) != null) {
				content = line;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// beginIndex
		int begin = content.indexOf(':');
		// endIndex
		int end = content.indexOf('k');
		// 截取字符串信息

		content = content.substring(begin + 1, end).trim();
		mTotal = Integer.parseInt(content);
		return mTotal / 1024;
	}

	public static String isRoot() {
		try {
			if (Runtime.getRuntime().exec("su").getOutputStream() == null) {
				return "false";
			} else {
				return "true";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "false";
	}

	public static double getAvailableInternalMemorySize(Context context) {
		long MEM_UNUSED;
		// 得到ActivityManager
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 创建ActivityManager.MemoryInfo对象

		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);

		// 取得剩余的内存空间

		MEM_UNUSED = mi.availMem / 1024 / 1024;
		return MEM_UNUSED;
	}
}
