/**
 * 为Extract层提供接口
 * @author huangke
 */
package cn.eugames.extension.common;

import java.util.Map;

import android.app.Activity;
import android.content.Context;

public interface IEuSdkAdapter {

		/**
		 * 初始化接口
		 * @param config
		 * @param activity
		 * @param listener
		 */
	  	public  void initEUApp(EUSDKConfigParameters config,
	  			Activity activity, EUSDKListener listener) ;
	  	
	  	/**
	  	 * 登陆接口
	  	 * @param config
	  	 * @param listener
	  	 * @param context
	  	 */
	  	public  void loginEUApp(EUSDKConfigParameters config,
	  			EUSDKListener listener,Context context) ;
	  
	  	/**
	  	 * 快速登陆接口
	  	 * @param config
	  	 * @param listener
	  	 * @param context
	  	 */
	  	public  void loginFastEUApp(EUSDKConfigParameters config,
	  			EUSDKListener listener,Context context) ;
	  
	  	/**
	  	 * 注销接口
	  	 * @param config
	  	 * @param listener
	  	 * @param context
	  	 */
	  	public  void logoutEUApp(EUSDKConfigParameters config,
	  			EUSDKListener listener,Context context);
	  
	  
	  	/**
	  	 * 显示用户中心接口
	  	 * @param context1
	  	 * @param listener
	  	 */
	  	public  void showUserCenterEUApp(Context context1, EUSDKListener listener);
	  
	  	/**
	  	 * 充值接口
	  	 * @param config
	  	 * @param item
	  	 * @param price
	  	 * @param count
	  	 * @param order
	  	 * @param url
	  	 * @param obligate
	  	 * @param activity
	  	 * @param listener
	  	 */
	  	public  void rechargeEUApp( EUSDKConfigParameters config,
	  			 String item,  double price,  int count,
	  			 String order,  String url, Map obligate,
	  			 Activity activity,  EUSDKListener listener);

}
