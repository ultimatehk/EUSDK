/**
 * 显示必接方法
 * @author huangke
 */
package cn.eugames.extension.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.eugames.extension.common.EUSDKBaseAdapter;
import android.content.Context;

public class MethodLogUtil {

	private static String fileName;
	private static final String START_TYPE = "========START========";
	private static final String END_TYPE = "========END========";

	/**
	 * init方法输出
	 * 
	 * @throws IOException
	 */
	public static void init(Context context, String config) {
		VersionUtil.getInstance(context);
		if (VersionUtil.deBug()) {
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm");
			fileName = EUSDKBaseAdapter.mConfig.getGameName() + "_"
					+ EUSDKBaseAdapter.mConfig.getPlatformName()
					+ sdf.format(d) + ".txt";
			FileUtil.getInstanse().createFile(fileName);
			FileUtil.getInstanse().appendContext(fileName, "config is \n");
			FileUtil.getInstanse().appendContext(fileName, config + "\n");
			FileUtil.getInstanse().appendContext(fileName, START_TYPE);
			FileUtil.getInstanse().appendContext(fileName,
					"执行了init方法,版本号为" + VersionUtil.euSDKVersion);
		}
	}

	public static void showUserCenter() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void login() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void logout() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	// 生命指纹方法
	public static void getEULifeFingerprint() {
		if (VersionUtil.deBug()) {
			FileUtil.getInstanse().appendContext(fileName, "执行了获取生命指纹方法");
		}
	}

	public static void recharge() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void hasMethods(String[] methods) {
		if (VersionUtil.deBug()) {
			for (String method : methods)
				FileUtil.getInstanse().appendContext(fileName,
						"游戏判断了[" + method + "]是否可执行");
		}
	}

	public static void checkUpdate() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void resume() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void pause() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void destory() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void exit(boolean isBack) {
		if (VersionUtil.deBug()) {
			if (isBack) {
				FileUtil.getInstanse().appendContext(fileName, END_TYPE);
				fileName = null;
			} else {
				FileUtil.getInstanse().appendContext(fileName, "执行了exit方法");
			}
		}
	}

	public static void stop() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void restart() {

		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void newIntent() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void activityResult() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void postUserLevelUpGameLog() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void start() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void postEnterGameLog() {
		if (VersionUtil.deBug()) {
			FileUtil.getInstanse().appendContext(fileName, "执行了上传角色信息方法");
		}
	}

	public static void postUpdatedGameLog() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void postUpdatingGameLog() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void addictionPrevention() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void realNameRegister() {
		scanf(new Throwable().getStackTrace()[0].getMethodName());
	}

	public static void getExtendMessage(String authorization, int type) {
		if (VersionUtil.deBug()) {
			FileUtil.getInstanse().appendContext(
					fileName,
					"执行了getExtendMessage方法,authorization is +" + authorization
							+ ",type is " + type);
		}
	}

	public static void ScanUrl(String urlStr, String returnStr) {
		if (VersionUtil.deBug()) {
			if (!Utils.isEmpty(urlStr)) {
				FileUtil.getInstanse().appendContext(fileName,
						"\n+++++网络请求开始+++++");
				FileUtil.getInstanse().appendContext(fileName, urlStr);
				FileUtil.getInstanse().appendContext(fileName,
						"+++++网络请求结束+++++\n");
			}
			if (!Utils.isEmpty(returnStr)) {
				FileUtil.getInstanse().appendContext(fileName,
						"\n-----网络返回开始-----");
				FileUtil.getInstanse().appendContext(fileName, returnStr);
				FileUtil.getInstanse().appendContext(fileName,
						"-----网络返回结束-----\n");
			}
		}
	}

	private static void scanf(String methodName) {
		try {
			if (VersionUtil.deBug()) {
				FileUtil.getInstanse().appendContext(fileName,
						"执行了" + methodName + "方法");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
