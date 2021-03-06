/**
 * 版本工具
 * 
 * @author huangke
 *
 */
package cn.eugames.extension.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

public class VersionUtil {
	// 这里记录艺游SDK版本号
	protected static final String euSDKVersion = "1.0.0";
	private ArrayList<String> deBugMacList = new ArrayList<>();
	private static boolean isDeBug = false;
	private static VersionUtil versionUtil = null;
	private static Context mContext;

	private VersionUtil() {
		isDeBug = isDeBugPhone();
	}

	public static VersionUtil getInstance(Context context) {
		mContext = context;
		if (versionUtil == null)
			versionUtil = new VersionUtil();
		return versionUtil;
	}

	/**
	 * 是否debug模式
	 * 
	 * @return
	 */
	public static boolean deBug() {
		return isDeBug;
	}

	public static String getVersion() {
		return euSDKVersion;
	}

	/**
	 * 弹出版本号信息
	 */
	public void showVersion() {
		if (isDeBug)
			Utils.showToast(mContext, "艺游SDK版本号：" + euSDKVersion);
	}

	/**
	 * 弹出测试信息的toast
	 * 
	 * @param msg
	 */
	public void showDeBugMsg(String msg) {
		if (isDeBug)
			Utils.showToast(mContext, msg);
	}

	/**
	 * 判断是否为debug设备
	 * 
	 * @return
	 */
	private boolean isDeBugPhone() {
		boolean isDebug = false;
		try {
			addMacMessage();
			isDebug = deBugMacList.contains(Utils.getMacAddr(mContext)
					.toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
			isDebug = false;
		}

		return isDebug;
	}

	/**
	 * 添加测试设备mac信息
	 * 
	 * @throws IOException
	 */
	private void addMacMessage() {
		// 格式示例
		// deBugMacList.add("88:32:9B:2A:0D:7D");

		try {
			int id = mContext.getResources().getIdentifier("eudebug", "raw",
					mContext.getPackageName());
			InputStream inStream = mContext.getResources().openRawResource(id);
			byte[] msgBytes = ConfigUtils.readInputStreamToByte(inStream);
			String[] macs = new String(msgBytes, "UTF-8").split("\n");
			Collections.addAll(deBugMacList, macs);
		} catch (Exception e) {

		}

	}
}
