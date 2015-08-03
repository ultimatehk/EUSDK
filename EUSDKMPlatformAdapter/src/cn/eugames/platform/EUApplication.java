/**
 * application类实现
 * @author huangke
 */
package cn.eugames.platform;



import cn.eugames.extension.utils.ConfigUtils;
import cn.eugames.extension.utils.Log;
import cn.eugames.platform.module.EUSDKExtract;
import android.app.Application;
import android.content.Context;

public class EUApplication extends Application {

	private EUSDKExtract euSDKExtract = EUSDKExtract.getInstance();

	@Override
	protected void attachBaseContext(Context base) {
		if(euSDKExtract==null){
			Log.e(Log.LOG_SDK, "gyyxSDKExtract is null");
		}
		Log.i(Log.LOG_SDK, "in attachBaseContext");
		euSDKExtract.applicationAttachBaseContext(
				ConfigUtils.refineConfigMsg(base), base);
		super.attachBaseContext(base);
	}

	@Override
	public void onCreate() {
		euSDKExtract.applicationOnCreate(ConfigUtils.refineConfigMsg(this),
				this);
		super.onCreate();
	}
}
