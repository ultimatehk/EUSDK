/**
 * 崩溃日志记录回调
 * @author huangke
 */
package cn.eugames.extension.crashreporter;

import android.os.Bundle;

public interface CrashLogCallBack {

	public void handleCrashLog(Bundle bundle);
}
