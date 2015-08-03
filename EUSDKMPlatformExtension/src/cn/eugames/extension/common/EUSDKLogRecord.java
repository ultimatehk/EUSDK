package cn.eugames.extension.common;

import java.util.Map;

import cn.eugames.extension.utils.Log;
import cn.eugames.extension.utils.LogUtil;
import cn.eugames.extension.utils.Utils;
import android.app.Activity;
import android.content.Context;


public class EUSDKLogRecord {
	// 启动日志
	public static void writeGameStartLog(final EUSDKConfigParameters config,
			final Activity activity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					cn.eugames.extension.utils.LogUtil.writeGameStartLog(config, activity, null);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "startSDKLog", e);
				}
			}
		}).start();
	}

	// 注册后，登陆日志
	public static void writeGameRegisterPhoneLog(
			final EUSDKConfigParameters config, final Activity activity,
			final String userId) {
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// LogUtil.writeGameRegisterPhoneLog(config, activity, userId,
		// GyyxSdkBaseAdapter.userInfo.getReg_type(), null);
		// }
		// }).start();
	}

	// 登陆后，登陆日志
	public static void writeGameLoginPhoneLog(
			final EUSDKConfigParameters config, final Activity activity,
			final Map extendMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.writeGameLoginPhoneLog(config,
							Utils.getItem(extendMap, "roleId"),
							Utils.getItem(extendMap, "roleName"), activity,
							Utils.getItem(extendMap, "userId"),
							EUSDKBaseAdapter.userInfo.getLogin_type(),
							extendMap);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "writeGameLoginPhoneLog", e);
				}
			}
		}).start();
	}// writeGameUpdatedLog

	// 记录更新完成日志
	public static void writeGameUpdatedLog(final EUSDKConfigParameters config,
			final Activity activity, final Map extendMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.writeGameUpdatedLog(config, activity, extendMap);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "writeGameUpdatedLog", e);
				}
			}
		}).start();
	}

	// 记录更新开始日志
	public static void writeGameUpdatingLog(final EUSDKConfigParameters config,
			final Activity activity, final Map extendMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.writeGameUpdatingLog(config, activity, extendMap);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "writeGameUpdatedLog", e);
				}
			}
		}).start();
	}

	// 记录进入游戏日志
	public static void writeGameEnterLog(final EUSDKConfigParameters config,
			final Activity activity, final Map extendMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.writeGameEnterLog(config, activity, extendMap);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "writeGameEnterLog", e);
				}
			}
		}).start();
	}

	// 记录游戏安装日志
	public static void writeGameInstallLog(final EUSDKConfigParameters config,
			final Context activity, final Map extendMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.writeGameInstallLog(config, activity, extendMap);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "writeGameInstallLog", e);
				}
			}
		}).start();
	}

	// 记录游戏卸载日志
	public static void writeGameUninstallLog(final EUSDKConfigParameters config,
			final Context activity, final Map extendMap) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.writeGameUninstallLog(config, activity, extendMap);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("GYYX_SDK", "writeGameUninstallLog", e);
				}
			}
		}).start();
	}
	
	// 记录更新完成日志
	public static void writeWaiGuaLog(final EUSDKConfigParameters config,
				final Activity activity, final Map extendMap) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						LogUtil.writeWaiGuaLog(config, activity, extendMap);
					} catch (Exception e) {
						e.printStackTrace();
						Log.e("GYYX_SDK", "writeWaiGuaLog", e);
					}
				}
			}).start();
		}
}
