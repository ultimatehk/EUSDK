package cn.eugames.platform;

import java.util.Map;

import cn.eugames.extension.common.EUSDKBaseAdapter;
import cn.eugames.extension.common.EUSDKConfigParameters;
import cn.eugames.extension.common.EUSDKListener;
import cn.eugames.extension.common.EUSDKLogRecord;
import cn.eugames.extension.common.IAddictionPrevention;
import cn.eugames.extension.common.IRealNameSdkAdapter;
import cn.eugames.extension.common.ISetExtendListener;
import cn.eugames.extension.crashreporter.CrashReporter;
import cn.eugames.extension.utils.ConfigUtils;
import cn.eugames.extension.utils.MethodLogUtil;
import cn.eugames.extension.utils.Utils;
import cn.eugames.platform.module.EUSDKExtract;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class EUSDKUnify {

	private static final EUSDKUnify instance = new EUSDKUnify();
	private static EUSDKConfigParameters config;
	private EUSDKExtract euSDKExtract;
	public static Context context;

	private EUSDKUnify() {
		euSDKExtract = EUSDKExtract.getInstance();
	}

	public static EUSDKUnify getInstance() {
		return instance;
	}

	public void showUserCenter(Activity activity, EUSDKListener listener) {
		MethodLogUtil.showUserCenter();
		euSDKExtract.showUserCenterEUApp(activity, listener);
	}

	// /***
	// * 接入个推，注意 该方法应该在初始化SDk之后调用
	// * @param activity
	// * @param accessToken
	// */
	// public void usePushApi(Activity activity, String accessToken) {
	// PushMsgApi.initPushParameters(config.getAppKey(), config.getAppId(),
	// accessToken, activity);
	// }
	/***
	 * SDK初始化，强烈建议你在Activity的onCreate()方法中调用
	 * 
	 * @param activity
	 */
	public void init(Activity activity, EUSDKListener listener) {
		CrashReporter.init(activity, euSDKExtract);
		config = ConfigUtils.refineConfigMsg(activity);
		context = activity;
		EUSDKBaseAdapter.mConfig = config;
		MethodLogUtil.init(activity, config.toString());
		
		// 调用梆梆,1.10版本以下，默认启动梆梆
		/**
		if (!Utils.isEmpty(config.getMethodSingle("checkPlug"))
				&& config.getMethodSingle("checkPlug").equals("true")
				|| Utils.isEmpty(config.getMethodSingle("checkPlug")))
			//new BangcleHelper().startProcessListener(activity, config);
			 */
		euSDKExtract.initEUApp(config, activity, listener);
		//个推信息预留
		//PushMsgApi.initPushParameters(config.getAppKey(), config.getAppId(),"", activity);
				
		// euSDKExtract.initServer(activity);
		// GyyxCpa.init(activity, config.getCpaId(), config.getChanel());
	}

	/***
	 * SDK登录功能
	 * 
	 * @param activity
	 * @param listener
	 * @param showGiftInfo
	 *            登录回调统一接口
	 */
	public void login(Activity activity, final EUSDKListener listener) {
		MethodLogUtil.login();
		EUSDKListener loginListener = new EUSDKListener() {
			@Override
			public void onException(Exception arg0) {
				listener.onException(arg0);
			}

			@Override
			public void onError(Bundle arg0) {
				listener.onError(arg0);
			}

			@Override
			public void onComplete(Bundle arg0) {
				listener.onComplete(arg0);
			}

			@Override
			public void onCancel() {
				listener.onCancel();
			}
		};
		euSDKExtract.loginEUApp(config, loginListener, activity);
	}

	/***
	 * SDk 注销回调接口
	 * 
	 * @param activity
	 * @param listener
	 *            注销回调统一接口
	 */
	public void logout(Activity activity, EUSDKListener listener) {
		MethodLogUtil.logout();
		euSDKExtract.logoutEUApp(config, listener, activity);
	}

	/**
	 * 生命指纹函数 函数名称：getEULifeFingerprint<> 功能描述：
	 * 
	 * @author huangke
	 * @return 生命指纹字符串
	 */
	public void getEULifeFingerprint(EUSDKListener listener) {
		MethodLogUtil.getEULifeFingerprint();
		euSDKExtract.getEUSDKLifeFingerprint(listener);

	}

	/***
	 * SDk支付功能
	 * 
	 * @param activity
	 * @param item
	 * @param price
	 * @param count
	 * @param order
	 * @param url
	 * @param obligate
	 * @param listener
	 *            支付回调统一接口
	 */
	public void recharge(Activity activity, String userId, String item,
			final double price, final int count, final String order,
			String url, Map obligate, final EUSDKListener listener) {

		MethodLogUtil.recharge();
		EUSDKBaseAdapter.userInfo.setID(userId);

		euSDKExtract.setPayListener(listener);
		euSDKExtract.rechargeEUApp(config, item, price, count, order, url,
				obligate, activity, listener);

	}

	/**
	 * 根据数组判断方法是否必接
	 * 
	 * @param methods
	 * @return
	 */
	public boolean[] hasMethods(String[] methods) {
		MethodLogUtil.hasMethods(methods);
		return euSDKExtract.hasMethods(config, methods);
	}

	/**
	 * apk更新检测
	 */
	public void checkUpdate() {
		MethodLogUtil.checkUpdate();
	}

	/***
	 * 游戏窗口显示 悬浮窗 --- 光宇平台独有
	 * 说明：和游戏方对接的是最外层的统一接口，所以该接口除了对各个SDK相同的部分提供统一的方法，还要对比较特殊的SDK提供单独的方法处理
	 * 
	 * @param activity
	 */
	public void resume(Activity activity) {
		MethodLogUtil.resume();
		euSDKExtract.resumeEUApp(activity);
	}

	/***
	 * 游戏窗口隐藏悬浮窗 --- 光宇平台独有
	 * 说明：和游戏方对接的是最外层的统一接口，所以该接口除了对各个SDK相同的部分提供统一的方法，还要对比较特殊的SDK提供单独的方法处理
	 * 
	 */
	public void pause() {
		MethodLogUtil.pause();
		euSDKExtract.pauseEUApp();
	}

	/***
	 * 游戏窗口退出销毁悬浮窗等资源 -- 光宇平台独有
	 * 说明：和游戏方对接的是最外层的统一接口，所以该接口除了对各个SDK相同的部分提供统一的方法，还要对比较特殊的SDK提供单独的方法处理
	 */
	public void destory() {
		MethodLogUtil.destory();
		euSDKExtract.destoryEUApp();
	}

	/***
	 * 在游戏主窗体按back键退出游戏时调用，会弹出退出确认框 ---光宇平台独有
	 * 说明：和游戏方对接的是最外层的统一接口，所以该接口除了对各个SDK相同的部分提供统一的方法，还要对比较特殊的SDK提供单独的方法处理
	 */
	public void exit(final Context context, final EUSDKListener listener) {
		MethodLogUtil.exit(false);
		EUSDKListener tempListener = new EUSDKListener() {

			@Override
			public void onException(Exception arg0) {
				listener.onException(arg0);

			}

			@Override
			public void onError(Bundle arg0) {
				listener.onError(arg0);
			}

			@Override
			public void onComplete(Bundle arg0) {
				// ((Activity) context).finish();
				MethodLogUtil.exit(true);
				listener.onComplete(arg0);
			}

			@Override
			public void onCancel() {
				listener.onCancel();

			}
		};
		euSDKExtract.exitEUApp(context, tempListener);

	}

	public void stop() {
		MethodLogUtil.stop();
		this.euSDKExtract.stopEUApp();
	}

	public void restart(Activity activity) {
		MethodLogUtil.restart();
		this.euSDKExtract.restartEUApp(activity);
	}

	public void start(Activity activity) {
		MethodLogUtil.start();
		this.euSDKExtract.startEUApp(activity);
	}

	public void activityResult(Activity activity, int requestCode,
			int resultCode, Intent data) {
		MethodLogUtil.activityResult();
		this.euSDKExtract.activityResultEUApp(activity, requestCode,
				resultCode, data);
	}

	public void newIntent(Intent intent) {
		MethodLogUtil.newIntent();
		this.euSDKExtract.onNewIntentEUApp(intent);
	}

	/**
	 * 游戏方调用，记录角色登陆日志
	 * 
	 * @param extendMap
	 *            ：[roleId、roleName、roleLevel、serverId、serverName、userId]
	 * @param activity
	 * 
	 * 
	 */
	public void postEnterGameLog(final Map extendMap, final Activity activity) {
		// GyyxLogRecord.writeGameLoginPhoneLog(config, activity, extendMap);
		MethodLogUtil.postEnterGameLog();
		EUSDKLogRecord.writeGameEnterLog(config, activity, extendMap);
		euSDKExtract.uploadLoginMessage(activity, extendMap);
	}

	/**
	 * 
	 * @param extendMap
	 *            :[updateDataId、updateProgress、updateIsSuccess]
	 * @param activity
	 */
	public void postUpdatedGameLog(final Map extendMap, final Activity activity) {
		MethodLogUtil.postUpdatedGameLog();
		EUSDKLogRecord.writeGameUpdatedLog(config, activity, extendMap);
	}

	/**
	 * 更新开始记录
	 * 
	 * @param extendMap
	 *            :[updateDataId]
	 * @param activity
	 */
	public void postUpdatingGameLog(final Map extendMap, final Activity activity) {
		MethodLogUtil.postUpdatingGameLog();
		EUSDKLogRecord.writeGameUpdatingLog(config, activity, extendMap);
	}

	/**
	 * 角色升级记录
	 * 
	 * @param extendMap
	 *            ：[roleName、roleLevel、serverId]
	 * @param activity
	 */
	public void postUserLevelUpGameLog(final Map extendMap,
			final Activity activity) {
		MethodLogUtil.postUserLevelUpGameLog();
		euSDKExtract.uploadUserMessage(activity, extendMap);
	}

	/**
	 * 防沉迷注册查询
	 */
	public void addictionPrevention(final String userId,
			final EUSDKListener listener) {
		MethodLogUtil.addictionPrevention();
		if (euSDKExtract instanceof IAddictionPrevention) {
			final IAddictionPrevention addictionPrevention = (IAddictionPrevention) euSDKExtract;
			euSDKExtract.baseHandler.post(new Runnable() {

				@Override
				public void run() {
					addictionPrevention.addictionPrevention(config, userId,
							listener);

				}
			});
			// addictionPrevention.addictionPrevention(config,userId,listener);
		}
	}

	/**
	 * 实名注册
	 */
	public void realNameRegister(final String userId,
			final EUSDKListener listener) {
		MethodLogUtil.realNameRegister();
		if (euSDKExtract instanceof IRealNameSdkAdapter) {
			final IRealNameSdkAdapter realNameSdkAdapter = (IRealNameSdkAdapter) euSDKExtract;
			euSDKExtract.baseHandler.post(new Runnable() {

				@Override
				public void run() {
					realNameSdkAdapter.realNameRegister(config, userId,
							listener);

				}
			});
			// realNameSdkAdapter.realNameRegister(config,userId,listener);
		}
	}

	public void setExtendListener(String type, EUSDKListener listener) {
		if (euSDKExtract instanceof ISetExtendListener) {
			((ISetExtendListener) euSDKExtract).setExtendListener(type,
					listener);
		}
	}

	/**
	 * 
	 * @param authorization
	 *            授权码
	 * @param type
	 *            获取类型
	 * 
	 * @return 根据type返回对应的值
	 */
	public String getExtendMessage(String authorization, int type) {

		MethodLogUtil.getExtendMessage(authorization, type);
		String reStr = "";
		if (!Utils.isEmpty(authorization)
				&& !Utils.isEmpty(config.getAuthCode())
				&& authorization.equals(config.getAuthCode())) {
			switch (type) {
			case 0:
				reStr = config.getExtendId();
				break;
			case 1:
				reStr = config.getPlatformIdMd5();
				break;
			default:
				reStr = "";
				break;
			}
		}
		return reStr;
	}

}
