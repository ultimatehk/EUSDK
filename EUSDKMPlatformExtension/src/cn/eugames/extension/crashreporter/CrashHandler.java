/**
 * 崩溃信息处理handler
 * @author huangke
 */
package cn.eugames.extension.crashreporter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.eugames.extension.utils.FileUtil;
import cn.eugames.extension.utils.VersionUtil;
import android.content.Context;
import android.os.Bundle;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

	private Context mContext;
	private CrashLogCallBack mCallBack;
	private Thread.UncaughtExceptionHandler defaultHandler;

	public CrashHandler(Context context, CrashLogCallBack callback,
			Thread.UncaughtExceptionHandler handler) {
		mContext = context;
		// mCallBack = callback ;
		defaultHandler = handler;
		mCallBack = callback;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		final Writer writer = new StringWriter();
		final PrintWriter pWriter = new PrintWriter(writer);
		ex.printStackTrace(pWriter);
		String stackTrace = writer.toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String dateString = format.format(new Date());
		String dateString2 = format2.format(new Date());
		if (VersionUtil.deBug()) {
			String fileName = "CRASH_" + dateString2 + ".txt";
			FileUtil.getInstanse().createFile(fileName);
			FileUtil.getInstanse().appendContext(fileName, stackTrace);
		}

		/*
		 * try { dateString = URLEncoder.encode(dateString, "utf-8"); } catch
		 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		Bundle bundle = new Bundle();
		bundle.putString(crashProperty.APP_PACKAGE, mContext.getPackageName());
		bundle.putString(crashProperty.APP_VERSION,
				DeviceInfo.getAppVersion(mContext));
		bundle.putString(crashProperty.AVAILABLE_MEMORY,
				DeviceInfo.getAvailableInternalMemorySize(mContext) + "M");
		bundle.putString(crashProperty.EXCEPTION_CLASS, ex.getClass().getName());
		bundle.putString(crashProperty.OS_VERSION, android.os.Build.VERSION.SDK);
		bundle.putString(crashProperty.STACK_TRACE, stackTrace);
		bundle.putString(crashProperty.SDK_VERSION, DeviceInfo.getSdkVersion());
		bundle.putString(crashProperty.EXCEPTION_TIME, dateString);
		bundle.putString(crashProperty.THREAD_NAME, thread.getName());
		bundle.putString(crashProperty.DEVICE_NAME,
				DeviceInfo.getDeviceBrand(mContext));
		bundle.putString(crashProperty.MAC_ADDRESS,
				DeviceInfo.getMacAddr(mContext));
		bundle.putString(crashProperty.TOTAL_MEMORY,
				DeviceInfo.getTotalInternalMemorySize() + "M");
		bundle.putString(crashProperty.IS_ROOT, "");
		bundle.putString(crashProperty.IMEI, DeviceInfo.getImei(mContext));
		// mCallBack.handleCrashLog(bundle);
		// mCallBack = new CrashLogCallBackImpl();
		mCallBack.handleCrashLog(bundle);
		System.out.println(bundle);
		defaultHandler.uncaughtException(thread, ex);
		// System.exit(0);

	}

}
