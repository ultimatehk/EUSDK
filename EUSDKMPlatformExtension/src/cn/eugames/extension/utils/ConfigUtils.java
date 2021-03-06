/**
 * Config配置文件解析类
 * @author huangke
 */
package cn.eugames.extension.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import cn.eugames.extension.common.EUSDKConfigParameters;

public class ConfigUtils {

	public static EUSDKConfigParameters refineConfigMsg(Context context) {

		EUSDKConfigParameters config = null;
		try {
			int id = context.getResources().getIdentifier("config", "raw",
					context.getPackageName());
			InputStream inStream = context.getResources().openRawResource(id);
			byte[] msgBytes = readInputStreamToByte(inStream);

			String data = StringUtils.decrypt(new String(msgBytes, "UTF-8"),
					"huangke");
			Log.i(Log.LOG_SDK, data);

			JSONObject json = new JSONObject(data);
			config = new EUSDKConfigParameters();
			config.setAppId(json.getString("appId"));
			config.setAppKey(json.getString("appKey"));
			config.setGameName(json.getString("gameName"));
			config.setOriention(json.getString("oriention"));
			config.setPlatformName(json.getString("platformName"));
			config.setPlatformIdMd5(json.getString("platformIdMd5"));
			config.setExtendId(json.getString("extendId"));
			config.setClientId(json.getString("clientId"));
			config.setClientKey(json.getString("clientKey"));
			config.setMethodList(json.getString("actionList"));
			config.setManuFactoryId(json.getString("storeFlag"));
			config.setGameFlag(json.getString("gameFlag"));
			config.setServerFlag(json.getString("serverFlag"));

			if (TextUtils.isEmpty(json.getString("extendId"))) {
				config.setExtendId("0");
			}
			if (!json.isNull("authCode")) {
				config.setAuthCode(json.getString("authCode"));
			}

			return config;
		} catch (Exception e) {

		}

		return config;
	}

	private static void writeToFile(File file, String json) {
		try {
			FileOutputStream outStream = new FileOutputStream(file);
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(outStream));
			bufferedWriter.write(json);
			bufferedWriter.flush();
			bufferedWriter.close();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] readInputStreamToByte(InputStream in)
			throws IOException {
		int length = 0;
		byte buffer[] = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((length = in.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}

		return out.toByteArray();
	}

}
