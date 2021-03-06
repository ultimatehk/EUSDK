/**
 * 全局崩溃捕获初始化类
 * @author huangke
 */
package cn.eugames.extension.crashreporter;

import android.content.Context;

public class CrashReporter {
	public static void init(final Context context, CrashLogCallBack callback) {
		Thread.UncaughtExceptionHandler localUncaughtExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		if (localUncaughtExceptionHandler instanceof CrashHandler)
			return;
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context,
				callback, localUncaughtExceptionHandler));
	}

}
