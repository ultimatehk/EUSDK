/**
 * 文件LOG日志
 * 
 * @author huangke
 *
 */
package cn.eugames.extension.utils;

import android.content.Context;

public class Log2File {
	private static Log2File logUtil;
	private String fileName;

	private Log2File() {

	}

	public static Log2File getInstanse(Context context) {
		if (logUtil == null)
			logUtil = new Log2File();
		VersionUtil.getInstance(context);
		if (VersionUtil.deBug())
			return logUtil;
		else
			return null;
	}

	public void init(String fileName) {
		this.fileName = fileName;
		if (logUtil != null)
			FileUtil.getInstanse().createFile(fileName);
	}

	public void appendContext(String content) {
		if (logUtil != null)
			FileUtil.getInstanse().appendContext(fileName, content);
	}

}
