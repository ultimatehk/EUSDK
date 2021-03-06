package cn.eugames.extension.common;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.eugames.extension.crashreporter.CrashLogCallBack;
import cn.eugames.extension.crashreporter.crashProperty;
import cn.eugames.extension.utils.Log;
import cn.eugames.extension.utils.LogUtil;
import cn.eugames.extension.utils.MD5;
import cn.eugames.extension.utils.ProgressbarHelper;
import cn.eugames.extension.utils.RestResult;
import cn.eugames.extension.utils.SharePrefHelper;
import cn.eugames.extension.utils.Utils;
import cn.eugames.extension.utils.VersionUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


public abstract class EUSDKBaseAdapter implements IEuSdkAdapter,
		CrashLogCallBack {
	//请求地址
	private static String getIDUrl = "http://api.mobile.gyyx.cn/api/GatewayOrder/";
	
	public static EUSDKUserInfo userInfo;
	public static final String TAG = "EUSDK----->";
	protected static Activity mContext;
	
	protected static final int GET_TOKEN_SUCCESS = 1;
	protected static final int DISSHOW_PROGRESS = 2;
	protected static final int UPLOAD_TOKEN_FAIL = 3;
	protected static final int GET_FINGERPRINT = 4;
	
	public static EUSDKConfigParameters mConfig;
	private EUSDKListener payListener = null;
	private HashMap<String, String> uploadTokenExtendMap;

	public void setPayListener(EUSDKListener payListener) {
		this.payListener = payListener;
	}

	public Handler baseHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_TOKEN_SUCCESS:
				hideProgressbar();
				getTokenSuccessCallback(msg);
				// Toast.makeText(mContext, (CharSequence) msg.obj,
				// Toast.LENGTH_SHORT).show();
				break;
			case DISSHOW_PROGRESS:
				hideProgressbar();
				// Toast.makeText(mContext, "网络连接超时，请重新登陆",
				// Toast.LENGTH_SHORT).show();
				break;
			case UPLOAD_TOKEN_FAIL:
				hideProgressbar();
				uploadTokenFailCallback(msg, mContext);
				break;
			case GET_FINGERPRINT:
				EUSDKListener listener = (EUSDKListener) msg.obj;
				Bundle bundle = msg.getData();
				String result = bundle.getString("result");
				if ("ok".equals(result)) {
					listener.onComplete(bundle);
				} else {
					listener.onError(bundle);
				}
				break;
			default:
				break;
			}
		}
	};

	protected void backError(EUSDKListener listener, String errMessage) {
		Bundle bundle = new Bundle();
		bundle.putString("err_message", errMessage);
		Message msg = baseHandler.obtainMessage(UPLOAD_TOKEN_FAIL);
		msg.obj = listener;
		msg.setData(bundle);
		msg.sendToTarget();
	}

	protected boolean uploadToken(final EUSDKConfigParameters config,
			final EUSDKListener listener, final String token, String userId) {
		try {
			//注意，这里若userId传的是一个空值的话，则服务端需执行换userId的逻辑
			//如果渠道已经换到userId的话就直接传即可
			JSONObject jsonObject = uploadTokenRecord(config, token, userId);
			if (jsonObject == null) {
				backError(listener, "登陆失败，请重试");
				return false;
			}
			boolean result = jsonObject.getBoolean("result");
			if (!result) {
				backError(listener, jsonObject.getString("error_message"));
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		userInfo.setToken(token);
		return true;
	}

	protected void setUploadTokenExtendMap(HashMap<String, String> extendMap) {
		this.uploadTokenExtendMap = extendMap;
	}

	protected void backSuccess(EUSDKListener listener, String token) {
		Message msg = baseHandler.obtainMessage(GET_TOKEN_SUCCESS);
		msg.obj = listener;
		Bundle bundle = new Bundle();
		bundle.putString("token", token);
		msg.setData(bundle);
		msg.sendToTarget();
	}

	public void getTokenSuccessCallback(Message msg) {
		EUSDKListener eusdkListener = (EUSDKListener) msg.obj;
		if (eusdkListener != null) {
			eusdkListener.onComplete(msg.getData());
		}
	}

	public void showProgressBar() {
		ProgressbarHelper.getInstance(mContext).showProgressbar();
	}

	public void hideProgressbar() {
		ProgressbarHelper.getInstance(mContext).hideProgressbar();
	}

	@Override
	public void initEUApp(EUSDKConfigParameters config, Activity activity,
			EUSDKListener listener) {
	}

	@Override
	public abstract void loginEUApp(EUSDKConfigParameters config,
			EUSDKListener listener, Context context);

	@Override
	public void loginFastEUApp(EUSDKConfigParameters config,
			EUSDKListener listener, Context context) {
	}

	@Override
	public void logoutEUApp(EUSDKConfigParameters config, EUSDKListener listener,
			Context context) {
	}

	@Override
	public void showUserCenterEUApp(Context context1, EUSDKListener listener) {
	}

	@Override
	public abstract void rechargeEUApp(EUSDKConfigParameters config,
			String item, double price, int count, String order, String url,
			Map obligate, Activity activity, EUSDKListener listener);

	public boolean rechargeParamJudge(double price, int count, String order,
			EUSDKListener listener) {

		VersionUtil.getInstance(mContext).showDeBugMsg("user id is "+userInfo.getID());
		if (price <= 0 || count <= 0 || TextUtils.isEmpty(order.trim())) {
			Bundle bundle = new Bundle();
			bundle.putString("err_message", "充值参数有误");
			listener.onError(bundle);
			return false;
		}
		final float payMoney = (float) (count * price);

		if (payMoney > 100000) {
			Bundle bundle = new Bundle();
			bundle.putString("err_message", "充值金额不能大于10万元");
			listener.onError(bundle);
			return false;
		}

		if (TextUtils.isEmpty(EUSDKBaseAdapter.userInfo.getID())) {
			Bundle bundle = new Bundle();
			bundle.putString("err_message", "用户id不能为空");
			Log.d("EUSDK----->", "调用充值接口传的userid为空");
			listener.onError(bundle);
			return false;
		}
		return true;
	}

	/**
	 * 卸载监听，预留功能
	 * @param context
	 */
	public void initServer(Context context) {
		// Intent intent = new Intent(context, UninstallService.class);
		// context.startService(intent);
		// new UninstallService(context);
	}

	protected void uploadStartLog(final EUSDKConfigParameters config,
			final Activity activity) {
		// 开启线程上传启动日志
		VersionUtil.getInstance(activity).showVersion();
		new Thread(new Runnable() {
			@Override
			public void run() {
				EUSDKLogRecord.writeGameStartLog(config, activity);
			}
		}).start();
	}

	protected void uploadTokenFailCallback(Message msg, Context context) {
		EUSDKListener euSDKListener = (EUSDKListener) msg.obj;
		// Toast.makeText(context, "登陆失败，请重试", Toast.LENGTH_SHORT).show();
		euSDKListener.onError(msg.getData());
	}

	protected JSONObject uploadTokenRecord(EUSDKConfigParameters config,
			String token, String userId) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.putAll(LogUtil.getDeviceIDInformation(mContext));
		header.putAll(LogUtil.getDeciceInformation(mContext));
		header.putAll(LogUtil.getEUInformation(mContext, config));
		header.put("user_id", userId);
		header.put("token", token);
		if (uploadTokenExtendMap != null) {
			header.put("other_params",
					Utils.getItem(uploadTokenExtendMap, "other_params"));
		}
		//clientId和clentKey仅与游戏相关
		header.putAll(LogUtil.getSignInformation(config.getClientKey(),
				"/gateway/user/prelogin/?", header));
		RestResult restResult = LogUtil.sendHttp(
				"http://api.mobile.gyyx.cn/gateway/user/prelogin/", header);
		// header.put("timestamp",
		// String.valueOf(System.currentTimeMillis() / 1000));
		// // "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		// header.put("sign", Utils.sign(
		// "/ChargeUser/RecordLog/?" + Utils.signString(header),
		// config.getClientKey(),
		// "UTF-8"));
		// header.put("sign_type", Utils.SIGN_TYPE);
		// RestResult restResult =
		// Utils.gyyxApiRequest("POST","http://api.mobile.gyyx.cn/ChargeUser/RecordLog/",
		// Utils.encodeQueryString(header));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			return null;
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
			JSONObject jj = null;
			try {
				jj = new JSONObject(restResult.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jj;
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
			return null;
		}
	}

	protected JSONObject getPayID(EUSDKConfigParameters config,
			String game_order_no, String user_id, String item,
			String item_count, String price, String rmb, String url,
			Map obligate) {
		String serverFlag = (String) obligate.get("serverFlag") != null ? (String) obligate
				.get("serverFlag") : "0";
		String description = (String) obligate.get("description") != null ? (String) obligate
				.get("description") : "0";
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("game_order_no", game_order_no);
		header.put("user_id", user_id);
		header.put("i", config.getPlatformIdMd5());
		header.put("item", item);
		header.put("item_count", item_count);
		header.put("price", price);
		header.put("rmb", rmb);
		header.put("server_flag", serverFlag);
		header.put("notify_url", url);
		header.put("common_param", description);
		header.put("client_id", config.getClientId());
		header.put("game_role_id", Utils.getItem(obligate, "gameRoleID"));
		header.put("game_role_name", Utils.getItem(obligate, "gameRoleName"));
		header.put("game_role_grade", Utils.getItem(obligate, "gameRoleGrade"));
		header.put("need_good", Utils.getItem(obligate, "need_good"));
		//以后若有自有渠道包，需设置推广id
		if (config.getPlatformIdMd5().equals(LogUtil.EU_I))
			header.put("md5_extend_id", config.getExtendId());

		header.put("timestamp",
				String.valueOf(System.currentTimeMillis() / 1000));
		// "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		header.put("sdk_version", VersionUtil.getVersion());
		header.put("md5_keystore", MD5.getDigest(Utils.getKeystore(mContext)).toString());
		header.put("sign", Utils.sign(
				"/api/GatewayOrder/?" + Utils.signString(header),
				config.getClientKey(), "UTF-8"));
		header.put("sign_type", Utils.SIGN_TYPE);
		RestResult restResult = Utils.euApiRequest("GET", getIDUrl,
				Utils.encodeQueryString(header));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			return null;
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
			JSONObject jj = null;
			try {
				jj = new JSONObject(restResult.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jj;
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
			JSONObject jj = null;
			try {
				jj = new JSONObject(restResult.getContent());
				int errorCode = restResult.getStatusCode();
				String errorMes = jj.getString("message");
				final Bundle value = new Bundle();
				value.putInt("code", errorCode);
				value.putString("err_message", errorMes);
				if (payListener != null) {
					baseHandler.post(new Runnable() {
						@Override
						public void run() {
							payListener.onError(value);
						}
					});
				}

			} catch (final Exception e) {
				Bundle value = new Bundle();
				value.putInt("code", 501);
				value.putString("err_message", "catch问题" + e.getMessage());
				if (payListener != null) {
					baseHandler.post(new Runnable() {

						@Override
						public void run() {
							payListener.onException(e);
						}
					});
				}
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * apk更新检测
	 */
	public void checkUpdate() {

	}

	/**
	 * 根据数组判断方法是否必接
	 * 
	 * @param methods
	 * @return
	 */
	public boolean[] hasMethods(EUSDKConfigParameters config, String[] methods) {
		if (methods == null) {
			return null;
		}
		boolean[] rMethods = new boolean[methods.length];
		// String
		// s="{\"recharge\":\"TRUE\",\"showUserCenter\":\"TRUE\",\"logout\":\"TRUE\",\"showFloatWindow\":\"TRUE\",\"addictionPrevention\":\"TRUE\",\"realNameRegister\":\"TRUE\",\"checkUpdate\":\"TRUE\"}";
		String s = config.getMethodList();
		try {
			JSONObject jsonObject = new JSONObject(s);
			for (int i = 0; i < methods.length; i++) {

				rMethods[i] = jsonObject.getBoolean(methods[i]);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rMethods;
	}

	public void uploadLoginMessage(Context context, Map extendMap) {
	}
	
	public void uploadUserMessage(Context context, Map extendMap) {
	}

	/***
	 * 
	 * @param config
	 * @param token
	 * @return
	 */
	public JSONObject getUserID(EUSDKConfigParameters config, String token) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("i", config.getPlatformIdMd5());
		header.put("client_id", config.getClientId());
		header.put("app_id", config.getAppId());
		// header.put("extend_id", config.getExtend_id());
		header.put("token", token);
		header.put("timestamp",
				String.valueOf(System.currentTimeMillis() / 1000));
		// "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		header.put("sdk_version", VersionUtil.getVersion());
		header.put("sign", Utils.sign(
				"/api/ClientChargeUser/?" + Utils.signString(header),
				config.getClientKey(), "UTF-8"));
		header.put("sign_type", Utils.SIGN_TYPE);
		RestResult restResult = Utils.euApiRequest("GET",
				"http://api.mobile.gyyx.cn/api/ClientChargeUser/",
				Utils.encodeQueryString(header));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			return null;
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
			JSONObject jj = null;
			try {
				jj = new JSONObject(restResult.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jj;
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
			return null;
		}
	}


	protected String exchangeUserId(EUSDKConfigParameters config,
			String unifyUserId) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("userId", unifyUserId);
		header.put("client_id", config.getClientId());
		header.put("timestamp",
				String.valueOf(System.currentTimeMillis() / 1000));
		// "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		header.put("sdk_version", VersionUtil.getVersion());
		header.put("sign", Utils.sign(
				"/GatewayUnifyUser/?" + Utils.signString(header),
				config.getClientKey(), "UTF-8"));
		header.put("sign_type", Utils.SIGN_TYPE);
		RestResult restResult = Utils.euApiRequest("GET",
				"http://api.mobile.gyyx.cn/GatewayUnifyUser/",
				Utils.encodeQueryString(header));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			return null;
		}
		if (restResult.getStatusCode() == 200) {
			String userId = restResult.getContent().replace("\"", "")
					.replace("'", "");
			return userId;
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
			return null;
		}
	}

	public void getEUSDKLifeFingerprint(final EUSDKListener listener) {
		final Message msg = baseHandler.obtainMessage(GET_FINGERPRINT);

		String fingerPrint = SharePrefHelper.getStringFromSp(mContext,
				"gyyxsdk_info", "finger_print");
		if (!TextUtils.isEmpty(fingerPrint)) {
			Bundle bundle = new Bundle();
			bundle.putString("result", "ok");
			bundle.putString("finger_print", fingerPrint);
			msg.obj = listener;
			msg.setData(bundle);
			msg.sendToTarget();
			return;
		}
		new Thread() {
			public void run() {
				String fingerPrint = "";
				String isSuccess = "";
				String errMessage = "";
				JSONObject jsonObject = null;
				Bundle bundle = new Bundle();
				jsonObject = getEUSDKLifeFingerprintNetWork();
				if (jsonObject != null) {
					try {
						isSuccess = jsonObject.getString("IsSuccess");
						errMessage = jsonObject.getString("ErrorMessage");
						fingerPrint = jsonObject.getString("Values");
					} catch (Exception e) {
						e.printStackTrace();
					}
					if ("ok".equals(isSuccess)) {
						bundle.putString("result", isSuccess);
						bundle.putString("finger_print", fingerPrint);
						SharePrefHelper.writeStrToSp(mContext, "gyyxsdk_info",
								"finger_print", fingerPrint);
					} else {
						bundle.putString("result", "fail");
						bundle.putString("err_message", errMessage);
					}
				} else {
					bundle.putString("result", "fail");
					bundle.putString("err_message", "返回状态码非200");
				}
				msg.obj = listener;
				msg.setData(bundle);
				msg.sendToTarget();
			};
		}.start();
	}

	/**
	 * 指纹网络请求 函数名称：getGyyxLifeFingerprintNetWork<> 功能描述：
	 * 
	 * @author gaoshuanwu
	 * @return 请求成功返回body，失败返回null 修改日志:<>
	 */
	public JSONObject getEUSDKLifeFingerprintNetWork() {
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("i", mConfig.getPlatformIdMd5());
		String extendId = "0";
		if (mConfig.getPlatformIdMd5().equals(LogUtil.EU_I))
			extendId = mConfig.getExtendId();
		header.put("md5_extend_id", extendId);
		header.put("mac", Utils.getMacAddr(mContext));
		// header.put("extend_id", config.getExtend_id());
		header.put("imei", Utils.getimei(mContext));
		header.put("ifa", "0");
		header.put("ifv", "0");
		header.put("sdk_version",VersionUtil.getVersion());
		/*
		 * header.put("timestamp", String.valueOf(System.currentTimeMillis() /
		 * 1000)); // "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		 * header.put("sign", Utils.sign( " /api/Device/glfp/?" +
		 * Utils.signString(header), mConfig.getClientKey(), "UTF-8"));
		 * header.put("sign_type", Utils.SIGN_TYPE);
		 */
		RestResult restResult = Utils.euApiRequest("POST",
				"http://api.mobile.gyyx.cn/api/Device/glfp/",
				Utils.encodeQueryString(header));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			return null;
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
			JSONObject jj = null;
			try {
				jj = new JSONObject(restResult.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return jj;
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
			return null;
		}
	}

	/**
	 * 回调游戏的resume
	 * 
	 * @param activity
	 */
	public void resumeEUApp(Activity activity) {
	}

	public void pauseEUApp() {
	}

	public void destoryEUApp() {
		// 只有在程序退出的时候调用
	}

	public void exitEUApp(Context context, final EUSDKListener listener) {
		listener.onComplete(null);
	}

	public void applicationAttachBaseContext(EUSDKConfigParameters config,
			Context base) {

	}

	public void applicationOnCreate(EUSDKConfigParameters config, Context base) {
		
	}

	public void stopEUApp() {

	}
	
	public void startEUApp(Activity activity){
		
	}

	public void restartEUApp(Activity activity) {
		
	}

	public void activityResultEUApp(Activity activity, int requestCode,
			int resultCode, Intent data) {

	}

	public void onNewIntentEUApp(Intent intent) {
		
	}

	@Override
	public void handleCrashLog(final Bundle bundle) {

		new Thread() {
			public void run() {

				crashReportNet(bundle);
			};
		}.start();
	}

	private void crashReportNet(Bundle bundle) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("os_type", "Android");
		header.put("device_model", bundle.getString(crashProperty.DEVICE_NAME));
		header.put("os_version", bundle.getString(crashProperty.OS_VERSION));
		header.put("sdk_version", bundle.getString(crashProperty.SDK_VERSION));
		header.put("client_id", mConfig.getClientId());
		header.put("mac_address", bundle.getString(crashProperty.MAC_ADDRESS));
		header.put("imei", bundle.getString(crashProperty.IMEI));
		header.put("ram_space", bundle.getString(crashProperty.TOTAL_MEMORY));
		header.put("ram_remain_space", bundle.getString(crashProperty.AVAILABLE_MEMORY));
		header.put("exception_information", bundle.getString(crashProperty.STACK_TRACE));
		header.put("i", mConfig.getPlatformIdMd5());
		header.put("exception_time", bundle.getString(crashProperty.EXCEPTION_TIME));
		// header.put("is_original", "");

		header.put("timestamp",
				String.valueOf(System.currentTimeMillis() / 1000));
		header.put("sdk_version", VersionUtil.getVersion());
		// "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		header.put(
				"sign",
				Utils.sign(
						"/gateway/stats/game/exception/?"
								+ Utils.signString(header),
						mConfig.getClientKey(), "UTF-8"));
		header.put("sign_type", Utils.SIGN_TYPE);
		/*
		 * header.put("timestamp", String.valueOf(System.currentTimeMillis() /
		 * 1000)); // "http://api.mobile.gyyx.cn/api/GatewayOrder/"
		 * header.put("sign", Utils.sign( " /api/Device/glfp/?" +
		 * Utils.signString(header), mConfig.getClientKey(), "UTF-8"));
		 * header.put("sign_type", Utils.SIGN_TYPE);
		 */
		RestResult restResult = Utils.euApiRequest("POST",
				"http://api.mobile.gyyx.cn/gateway/stats/game/exception/",
				Utils.encodeQueryString(header));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			// return null;
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
			JSONObject jj = null;
			try {
				jj = new JSONObject(restResult.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// return jj;
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
			// return null;
		}
	}
}
