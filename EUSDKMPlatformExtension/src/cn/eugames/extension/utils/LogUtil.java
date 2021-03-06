/**
 * 服务端日志记录类
 * @author huangke
 */
package cn.eugames.extension.utils;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import cn.eugames.extension.common.EUSDKBaseAdapter;
import cn.eugames.extension.common.EUSDKConfigParameters;
import cn.eugames.extension.crashreporter.DeviceInfo;

public class LogUtil {

	public static final String EU_I = "267b4dfec0b36ea68479baa4f325868e";

	// 启动日志
	public static void writeGameStartLog(EUSDKConfigParameters config,
			Activity activity, Map<String, Object> extendMap) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.putAll(getDeviceIDInformation(activity));
		header.putAll(getDeciceInformation(activity));
		header.putAll(getEUInformation(activity, config));
		header.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/start/?", header));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/start/", header);
	}

	// 进入日志
	public static void writeGameEnterLog(EUSDKConfigParameters config,
			Activity activity, Map<String, Object> extendMap) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.putAll(getDeviceIDInformation(activity));
		param.putAll(getDeciceInformation(activity));
		param.putAll(getEUInformation(activity, config));
		param.put("unify_user_id", Utils.getItem(extendMap, "userId"));
		param.put("server_flag", Utils.getItem(extendMap, "serverId"));
		param.put("server_name", Utils.getItem(extendMap, "serverName"));
		param.put("role_id", Utils.getItem(extendMap, "roleId"));
		param.put("role_name", Utils.getItem(extendMap, "roleName"));
		param.put("role_level", Utils.getItem(extendMap, "roleLevel"));
		param.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/enter/?", param));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/enter/", param);
	}

	// 登陆后，登陆日志
	public static void writeGameLoginPhoneLog(EUSDKConfigParameters config,
			String role_id, String role_name, Activity activity, String uid,
			String loginType, Map<String, Object> extendMap) {

		HashMap<String, String> param = new HashMap<String, String>();
		param.put("mac_address", DeviceInfo.getMacAddr(activity));
		param.put("imei", Utils.getimei(activity));
		param.put("ifa", "0");
		param.put("ifv", "0");
		param.put("i", config.getPlatformIdMd5());
		param.put("os_type", "Android");
		param.put("os_version", android.os.Build.VERSION.RELEASE);
		param.put("resolution", Utils.getResolution(activity));
		param.put("operators", config.getPlatformName());
		param.put("device_model", Utils.getDeviceModel(activity));
		param.put("server_flag", Utils.getItem(extendMap, "serverId"));
		param.put("server_name", Utils.getItem(extendMap, "serverName"));
		param.put("role_level", Utils.getItem(extendMap, "roleLevel"));
		param.put("role_id", role_id);
		param.put("role_name", role_name);
		param.put("batch_no", config.getBatchNo());
		param.put("phone_ram", Utils.getTotalMemory(activity));
		param.put("login_type", loginType);
		param.put("user_id", uid);
		param.put("client_id", config.getClientId());
		// param.put("getui_id", PushMsgApi.getui_id);

		// 仅有母包才可以设置推广ID
		if (config.getPlatformIdMd5().equals(EU_I))
			param.put("md5_extend_id", config.getExtendId());
		param.put("timestamp",
				String.valueOf(System.currentTimeMillis() / 1000));
		param.put("sdk_version", VersionUtil.getVersion());
		param.put("sign", Utils.sign(
				"/api/GatewayLogin/?" + Utils.signString(param),
				config.getClientKey(), "UTF-8"));
		param.put("sign_type", Utils.SIGN_TYPE);

		RestResult restResult = Utils.euApiRequest("GET",
				"http://api.mobile.gyyx.cn/api/GatewayLogin/",
				Utils.encodeQueryString(param));
		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
			return;
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
		}
	}

	// 安装日志
	public static void writeGameInstallLog(EUSDKConfigParameters config,
			Context activity, Map<String, Object> extendMap) {
		HashMap<String, String> header = new HashMap<String, String>();
		header.putAll(getDeviceIDInformation(activity));
		header.putAll(getDeciceInformation(activity));
		header.put("client_id", config.getClientId());
		header.put("package_id", Utils.getItem(extendMap, "packageId"));
		header.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/install/?", header));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/install/",
				header);
	}

	// 卸载日志
	public static void writeGameUninstallLog(EUSDKConfigParameters config,
			Context activity, Map<String, Object> extendMap) {
		HashMap<String, String> header = new HashMap<String, String>();
		// header.putAll(getDeviceIDInformation(activity));
		header.put("mac_address", Utils.getItem(extendMap, "mac"));
		header.put("imei", Utils.getItem(extendMap, "imei"));
		header.put("ifa", "0");
		header.put("ifv", "0");
		header.put("ios_id", "0");
		header.putAll(getDeciceInformation(activity));
		header.putAll(getEUInformation(activity, config));
		header.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/uninstall/?", header));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/uninstall/",
				header);
	}

	// 更新开始日志
	public static void writeGameUpdatingLog(EUSDKConfigParameters config,
			Activity activity, Map<String, Object> extendMap) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.putAll(getDeviceIDInformation(activity));
		param.putAll(getDeciceInformation(activity));
		param.putAll(getEUInformation(activity, config));
		param.put("update_data_id", Utils.getItem(extendMap, "updateDataId"));
		param.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/updating/?", param));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/updating/",
				param);
	}

	// 更新完成日志
	public static void writeGameUpdatedLog(EUSDKConfigParameters config,
			Activity activity, Map<String, Object> extendMap) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.putAll(getDeviceIDInformation(activity));
		param.putAll(getDeciceInformation(activity));
		param.putAll(getEUInformation(activity, config));
		param.put("update_data_id", Utils.getItem(extendMap, "updateDataId"));
		param.put("update_progress", Utils.getItem(extendMap, "updateProgress"));
		param.put("update_is_success",
				Utils.getItem(extendMap, "updateIsSuccess"));
		param.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/updated/?", param));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/updated/", param);
	}

	//
	public static void writeWaiGuaLog(EUSDKConfigParameters config,
			Activity activity, Map<String, Object> extendMap) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.putAll(getDeviceIDInformation(activity));
		param.putAll(getEUInformation(activity, config));
		param.put("wai_gua_package", Utils.getItem(extendMap, "waiGuaPackage"));
		param.put("wai_gua_description",
				Utils.getItem(extendMap, "waiGuaDescription"));
		param.put("wai_gua_name", Utils.getItem(extendMap, "waiGuaName"));
		param.put("operation_type", Utils.getItem(extendMap, "operationType"));
		param.put("token", EUSDKBaseAdapter.userInfo.getToken());
		param.putAll(getSignInformation(config.getClientKey(),
				"/gateway/stats/game/waigua/?", param));
		sendHttp("http://api.mobile.gyyx.cn/gateway/stats/game/waigua/", param);
	}

	/**
	 * 获取签名相关信息的Map组
	 * 
	 * @param signKey
	 * @param url
	 * @param param
	 * @return
	 */
	public static HashMap<String, String> getSignInformation(String signKey,
			String url, HashMap<String, String> param) {
		param.put("timestamp",
				String.valueOf(System.currentTimeMillis() / 1000));
		param.put("sdk_version", VersionUtil.getVersion());
		param.put("sign",
				Utils.sign(url + Utils.signString(param), signKey, "UTF-8"));
		//采用md5签名方式
		param.put("sign_type", Utils.SIGN_TYPE);
		return param;
	}

	/**
	 * 获取艺游相关信息的Map组
	 * 
	 * @param activity
	 * @param config
	 * @return
	 */
	public static HashMap<String, String> getEUInformation(Context activity,
			EUSDKConfigParameters config) {
		HashMap<String, String> euInformationMap = new HashMap<String, String>();
		euInformationMap.put("batch_no", config.getBatchNo());
		euInformationMap.put("i", config.getPlatformIdMd5());
		// 只有母包才能设置推广ID
		if (config.getPlatformIdMd5().equals(EU_I))
			euInformationMap.put("md5_extend_id", config.getExtendId());

		// gyInformationMap.put("getui_id", PushMsgApi.getui_id);
		euInformationMap.put("client_id", config.getClientId());
		return euInformationMap;
	}

	/**
	 * 获取设备相关信息的Map组
	 * 
	 * @param activity
	 * @return
	 */
	public static HashMap<String, String> getDeciceInformation(Context activity) {
		HashMap<String, String> deviceInformationMap = new HashMap<String, String>();
		deviceInformationMap.put("os_type", "Android");
		deviceInformationMap
				.put("os_version", android.os.Build.VERSION.RELEASE);
		deviceInformationMap.put("resolution", Utils.getResolution(activity));
		deviceInformationMap.put("operators", Utils.getOperators(activity));
		deviceInformationMap
				.put("device_model", Utils.getDeviceModel(activity));
		deviceInformationMap.put("phone_ram", Utils.getTotalMemory(activity));
		return deviceInformationMap;
	}

	/**
	 * 获取设备唯一标识信息的Map组
	 * 
	 * @return
	 */
	public static HashMap<String, String> getDeviceIDInformation(
			Context activity) {
		HashMap<String, String> deviceIDMap = new HashMap<String, String>();
		deviceIDMap.put("mac_address", DeviceInfo.getMacAddr(activity));// Utils.getWifiInfo(activity).getMacAddress()
		deviceIDMap.put("imei", Utils.getimei(activity));
		deviceIDMap.put("ifa", "0");
		deviceIDMap.put("ifv", "0");
		deviceIDMap.put("ios_id", "0");
		return deviceIDMap;
	}

	/**
	 * 发送日志请求
	 * 
	 * @param url
	 * @param param
	 */
	public static RestResult sendHttp(String url, HashMap<String, String> param) {

		RestResult restResult = Utils.euApiRequest("POST", url,
				Utils.encodeQueryString(param));

		if (restResult.getContent() == null) {
			Utils.logD("请求服务器失败,未返回状态码");
		}
		if (restResult.getStatusCode() == 200) {
			Utils.logD("请求服务器成功");
		} else {
			Utils.logD("请求服务器失败，返回状态码非200");
		}
		return restResult;
	}
}
