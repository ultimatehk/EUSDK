package cn.eugames.extension.common;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.json.JSONException;
import org.json.JSONObject;

public class EUSDKConfigParameters implements Serializable {

	private String appId;
	private String appKey;
	private String gameName;
	private String oriention;
	private String platformName;
	private String platformIdMd5;
	private String extendId;
	private String clientId;
	private String clientKey;
	private String batchNo = 0 + "";
	private String methodList;
	private String manuFactoryId;
	private String gameFlag;
	private String serverFlag;
	private String authCode;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getOriention() {
		return oriention;
	}

	public void setOriention(String oriention) {
		this.oriention = oriention;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getPlatformIdMd5() {
		return platformIdMd5;
	}

	public void setPlatformIdMd5(String platformIdMd5) {
		this.platformIdMd5 = platformIdMd5;
	}

	public String getExtendId() {
		return extendId;
	}

	public void setExtendId(String extendId) {
		this.extendId = extendId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientKey() {
		return clientKey;
	}

	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getMethodList() {
		return methodList;
	}

	public void setMethodList(String methodList) {
		this.methodList = methodList;
	}

	public String getManuFactoryId() {
		return manuFactoryId;
	}

	public void setManuFactoryId(String manuFactoryId) {
		this.manuFactoryId = manuFactoryId;
	}

	public String getGameFlag() {
		return gameFlag;
	}

	public void setGameFlag(String gameFlag) {
		this.gameFlag = gameFlag;
	}

	public String getServerFlag() {
		return serverFlag;
	}

	public void setServerFlag(String serverFlag) {
		this.serverFlag = serverFlag;
	}

	public String getMethodSingle(String object) {
		String reStr = "";
		try {
			JSONObject jsonObject = new JSONObject(getMethodList());
			if (!jsonObject.isNull(object))
				reStr = jsonObject.getString(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return reStr;
	}
	
	

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		try {
			Class t = this.getClass();
			Field[] fields = t.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				sb.append(field.getName());
				sb.append(":");
				if (field.getType() == Integer.class) {
					sb.append(field.getInt(this));
				} else if (field.getType() == Long.class) {
					sb.append(field.getLong(this));
				} else if (field.getType() == Boolean.class) {
					sb.append(field.getBoolean(this));
				} else if (field.getType() == char.class) {
					sb.append(field.getChar(this));
				} else if (field.getType() == Double.class) {
					sb.append(field.getDouble(this));
				} else if (field.getType() == Float.class) {
					sb.append(field.getFloat(this));
				} else
					sb.append(field.get(this));
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

}
