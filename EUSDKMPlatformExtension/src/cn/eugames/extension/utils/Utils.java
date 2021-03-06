/**
 * 辅助工具聚合类
 * @author huangke
 */
package cn.eugames.extension.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class Utils {

	public static int SCREEN_WIDTH = -1;
	public static int SCREEN_HEIGHT = -1;
	private static int connectTimeout = 30 * 1000;
	private static int readTimeout = 30 * 1000;
	public static String SIGN_TYPE = "MD5";

	private static final String TAG = "EUSDK----->";
	private static boolean DEBUG = true;

	private static SSLContext sslContext = null;
	
	/**
	 * 获取运营商信息
	 * @param context
	 * @return
	 */
	public static String getOperators(Context context) {
		String operators = "";
		try {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String operator = telManager.getSimOperator();
			if (operator != null) {

				if (operator.equals("46000") || operator.equals("46002")
						|| operator.equals("46007")) {

					// 中国移动
					operators = "ChinaMobile";

				} else if (operator.equals("46001")) {

					// 中国联通
					operators = "ChinaUnicom";

				} else if (operator.equals("46003")) {

					operators = "ChinaTelecom";
					// 中国电信

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			operators = "";
		}
		return operators;
	}
	
	/**
	 * 获取设备分辨率
	 * @param context
	 * @return
	 */
	public static String getResolution(Context context) {
		DisplayMetrics display = context.getResources().getDisplayMetrics();
		SCREEN_WIDTH = display.widthPixels;
		SCREEN_HEIGHT = display.heightPixels;
		if (SCREEN_WIDTH > SCREEN_HEIGHT) {
			int flag = SCREEN_WIDTH;
			SCREEN_WIDTH = SCREEN_HEIGHT;
			SCREEN_HEIGHT = flag;
		}
		return SCREEN_HEIGHT + "*" + SCREEN_WIDTH;
	}
	/**
	 * 获取设备IMEI码
	 * @param context
	 * @return
	 */
	public static String getimei(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return TextUtils.isEmpty(imei) ? "0" : imei;
	}

	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		// logD(text);
		byte[] hash;
		try {

			hash = MessageDigest.getInstance("MD5").digest(
					text.getBytes(input_charset));
			StringBuilder hex = new StringBuilder(hash.length * 2);
			for (byte b : hash) {
				if ((b & 0xFF) < 0x10)
					hex.append("0");
				hex.append(Integer.toHexString(b & 0xFF));
			}

			// AppHelper.logD("Sign " + text + " to: " + hex.toString());

			return hex.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String signString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (isEmpty(value)) {
				continue;
			}

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		if (prestr.length() > 0 && prestr.endsWith("&")) {
			prestr = prestr.substring(0, prestr.length() - 1);
		}
		return prestr;
	}

	public static void logD(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static String getDeviceModel(Context context) {

		String s = Build.MODEL;
		if (!TextUtils.isEmpty(s)) {
			s = s.replaceAll(" ", "");
		}
		return TextUtils.isEmpty(s) ? "" : s;
	}

	public static void logE(String msg, Exception e) {
		Log.e(TAG, msg, e);
	}

	public static boolean isEmpty(String query) {
		boolean ret = false;
		if (query == null || query.trim().equals("")) {
			ret = true;
		}
		return ret;
	}

	public static String convertStreamToString(InputStream is) {
		if (is == null) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 编码请求串
	 * @param params
	 * @return
	 */
	public static String encodeQueryString(HashMap<String, String> params) {
		// TODO Auto-generated method stub
		List<String> keys = new ArrayList<String>(params.keySet());
		String prestr = "";

		try {
			for (int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				String value = params.get(key);

				if (i == keys.size() - 1) {
					prestr = prestr + key + "="
							+ URLEncoder.encode(value, "UTF-8");

				} else {
					value = value == null ? "" : value;
					prestr = prestr + key + "="
							+ URLEncoder.encode(value, "UTF-8") + "&";
				}
			}
		} catch (UnsupportedEncodingException e) {
			logE("QueryString error", e);
		}

		return prestr;
	}
	
	/**
	 * 发送请求
	 * @param method
	 * @param url
	 * @param query
	 * @return
	 */
	public static RestResult euApiRequest(String method, String url,
			String query) {
		RestResult result = new RestResult();

		HttpURLConnection httpConnect = null;

		URL target;
		try {
			logD(url + "?" + query);
			MethodLogUtil.ScanUrl(url + "?" + query, "");
			target = new URL(url + "?" + query);
			httpConnect = (HttpURLConnection) target.openConnection();
			httpConnect.setConnectTimeout(connectTimeout);
			httpConnect.setReadTimeout(readTimeout);
			httpConnect.setRequestMethod(method);
			httpConnect.setRequestProperty("Content-Length", "0");
			httpConnect.setRequestProperty("Build", Build.MODEL);
			httpConnect.connect();

			int responseCode = httpConnect.getResponseCode();
			if (responseCode < 200 || responseCode >= 300) {
				logD(MessageFormat.format("{0}:{1}", "ErrorGuid",
						httpConnect.getHeaderField("ErrorGuid")));
			}
			result.setStatusCode(responseCode);
			InputStream is = httpConnect.getInputStream();
			result.setContent(convertStreamToString(is));

		} catch (FileNotFoundException e) {

			InputStream is = httpConnect.getErrorStream();
			result.setContent(convertStreamToString(is));

		} catch (IOException e) {

			if (e.getStackTrace()[0].getMethodName().contains(
					"getAuthorizationCredentials"))

				result.setStatusCode(401);
			else
				logE("euApiRequest Failed:", e);
			InputStream is = httpConnect.getErrorStream();
			result.setContent(convertStreamToString(is));
		} finally {
			httpConnect.disconnect();
		}

		logD("Status " + result.getStatusCode());
		logD("Content " + result.getContent());
		MethodLogUtil.ScanUrl("", "Status is " + result.getStatusCode()
				+ "\nContent is " + result.getContent());
		return result;
	}
	
	/**
	 * 获取设备可用内存
	 * @param activity
	 * @return
	 */
	public static String getTotalMemory(Context activity) {
		long initial_memory = 0;
		Method _readProclines = null;
		try {
			Class<?> procClass;
			procClass = Class.forName("android.os.Process");
			Class<?> parameterTypes[] = new Class[] { String.class,
					String[].class, long[].class };
			_readProclines = procClass.getMethod("readProcLines",
					parameterTypes);
			Object arglist[] = new Object[3];
			final String[] mMemInfoFields = new String[] { "MemTotal:",
					"MemFree:", "Buffers:", "Cached:" };
			long[] mMemInfoSizes = new long[mMemInfoFields.length];
			mMemInfoSizes[0] = 30;
			mMemInfoSizes[1] = -30;
			arglist[0] = new String("/proc/meminfo");
			arglist[1] = mMemInfoFields;
			arglist[2] = mMemInfoSizes;
			if (_readProclines != null) {
				_readProclines.invoke(null, arglist);
				initial_memory = mMemInfoSizes[0] * 1024;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return Formatter.formatFileSize(activity, initial_memory);
	}
	
	/**
	 * 获取商品信息
	 * @param obligate
	 * @param key
	 * @return
	 */
	public static String getItem(Map obligate, String key) {
		return (String) obligate.get(key) != null ? (String) obligate.get(key)
				: "";
	}
	
	/**
	 * 获取批次号信息
	 * @param context
	 * @return
	 */
	public static String getBatchConfigMsg(Context context) {
		String batchNo = "0";
		try {
			int id = context.getResources().getIdentifier("batchconfig", "raw",
					context.getPackageName());
			InputStream inStream = context.getResources().openRawResource(id);
			byte[] msgBytes = ConfigUtils.readInputStreamToByte(inStream);
			String data = CyptoUtils.decode("huangke", new String(msgBytes,
					"UTF-8"));
			JSONObject json = new JSONObject(data);
			batchNo = json.getString("batchNo");
		} catch (Exception e) {
			Log.e("EUSDK----->", "统一SDK解析批次出错");
			e.printStackTrace();
		}
		return batchNo;
	}
	
	/**
	 * 获取MAC地址
	 * @param context
	 * @return
	 */
	public static String getMacAddr(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService("wifi");
		WifiInfo info = wifi.getConnectionInfo();
		String macAddress = info.getMacAddress();
		return macAddress == null ? "" : macAddress;
	}
	
	/**
	 * 封装Toast,使之可于UI线程运行
	 * @param context
	 * @param msg
	 */
	public static void showToast(final Context context, final String msg) {
		((Activity) context).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 获取签名文件信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getKeystore(Context context) {
		StringBuffer builder = new StringBuffer();
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_SIGNATURES);
			Signature[] signatures = packageInfo.signatures;
			for (Signature signature : signatures)
				builder.append(signature.toCharsString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
}
