package cn.eugames.platform.module;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;
import cn.eugames.extension.common.EUSDKBaseAdapter;
import cn.eugames.extension.common.EUSDKConfigParameters;
import cn.eugames.extension.common.EUSDKListener;
import cn.eugames.extension.common.EUSDKUserInfo;
import cn.eugames.extension.common.IAddictionPrevention;
import cn.eugames.extension.common.IRealNameSdkAdapter;
import cn.eugames.extension.utils.Log;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;

public class EUSDKExtract extends EUSDKBaseAdapter implements IRealNameSdkAdapter,IAddictionPrevention{
	private static final String TAG = "EUSDK_360----->";
	private boolean mIsLandScape = false ;
	private static final int EXCHANGE_USERID_SUCCESS = 1 ;
	private static final int EXCHANGE_USERID_SUCCESS_FOR_REALNAME = 2 ;
	
	
	public static void setLogoutListener(EUSDKListener logoutListener) {
	}
	private EUSDKExtract(){
		
	}
	private static EUSDKExtract gyyxSDKExtract = null;
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXCHANGE_USERID_SUCCESS:
				exchangeUserIdCallback(msg);
				break;

			case EXCHANGE_USERID_SUCCESS_FOR_REALNAME:
				exchangeUserIdForRealNameCallback(msg);
				break ;
			default:
				break;
			}
		
	}

		

	};
	
	
	private void exchangeUserIdForRealNameCallback(Message msg) {
		final EUSDKListener listener = (EUSDKListener) msg.obj ;
		 Bundle bundle = new Bundle();
	        // 界面相关参数，360SDK界面是否以横屏显示。
	        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, mIsLandScape);
	        // 必需参数，360账号id，整数。
	        bundle.putString(ProtocolKeys.QIHOO_USER_ID, msg.getData().getString("pUserId"));
	        // 必需参数，使用360SDK的实名注册模块。
	        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_REAL_NAME_REGISTER);
	        Intent intent = new Intent(mContext, ContainerActivity.class);
	        intent.putExtras(bundle);
	        Matrix.invokeActivity(mContext, intent, new IDispatcherCallback() {
	            @Override
	            public void onFinished(String data) {
	               // Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
	            	System.out.println(11);
	            }
	        });
		
	}
	private void exchangeUserIdCallback(Message msg) {
		final EUSDKListener listener = (EUSDKListener) msg.obj ;
		 Bundle bundle = new Bundle();
		 
	        // 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
	        bundle.putString(ProtocolKeys.ACCESS_TOKEN, userInfo.getToken());

	        // 必需参数，360账号id，整数。
	        bundle.putString(ProtocolKeys.QIHOO_USER_ID, msg.getData().getString("pUserId"));

	        // 必需参数，使用360SDK的防沉迷查询模块。
	        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_ANTI_ADDICTION_QUERY);

	        Intent intent = new Intent(mContext, ContainerActivity.class);
	        intent.putExtras(bundle);

	        Matrix.execute(mContext, intent, new IDispatcherCallback() {
	            @Override
	            public void onFinished(String data) {
	                Log.d("demo,anti-addiction query result = ", data);
	                if (!TextUtils.isEmpty(data)) {
	                    try {
	                        JSONObject resultJson = new JSONObject(data);
	                        int errorCode = resultJson.optInt("error_code");
	                        if (errorCode == 0) {
	                            JSONObject contentData = resultJson.getJSONObject("content");
	                            if(contentData != null) {
	                                // 保存登录成功的用户名及密码
	                                JSONArray retData = contentData.getJSONArray("ret");
	                                Log.d(TAG, "ret data = " + retData);
	                                if(retData != null && retData.length() > 0) {
	                                    int status = retData.getJSONObject(0).optInt("status");
	                                    Log.d(TAG, "status = " + status);
	                                    switch (status) {

	                                        case 0:  
	                                        	listener.onCancel();
	                                            break;

	                                        case 1:  // 查询结果:未成年
	                                        	listener.onError(new Bundle());
	                                            break;

	                                        case 2:  // 查询结果:已成年
	                                        	listener.onComplete(new Bundle());
	                                            break;

	                                        default:
	                                            break;
	                                    }
	                                    return;
	                                }
	                            }
	                        } else {
	                            /*Toast.makeText(SdkUserBaseActivity.this,
	                                    resultJson.optString("error_msg"), Toast.LENGTH_SHORT).show();*/
	                            return;
	                        }

	                    } catch (JSONException e) {
	                        e.printStackTrace();
	                    }

	                   /* Toast.makeText(mContext,
	                            getString(R.string.anti_addiction_query_exception),
	                            Toast.LENGTH_LONG).show();*/
	                }
	            }
	        });
		
	}
	@Override
	public void realNameRegister(final EUSDKConfigParameters config,final String unifyUserId,final EUSDKListener listener) {
		if(TextUtils.isEmpty(userInfo.getToken())){
			Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(TextUtils.isEmpty(unifyUserId)){
			Toast.makeText(mContext, "获取用户ID失败", Toast.LENGTH_SHORT).show();
			return ;
		}
	    new Thread(){
	    	public void run() {
	    		String pUserId = exchangeUserId(config, unifyUserId);
	    		if(!TextUtils.isEmpty(pUserId)){
	    			Message msg = mHandler.obtainMessage(EXCHANGE_USERID_SUCCESS_FOR_REALNAME);
	    			msg.obj = listener ;
	    			Bundle bundle = new Bundle();
	    			bundle.putString("pUserId", pUserId);
	    			msg.setData(bundle);
	    			msg.sendToTarget();
	    		}
	    	};
	    }.start();
		
		
		
	}
	@Override
	public void addictionPrevention(final EUSDKConfigParameters config,final String unifyUserId,final EUSDKListener listener) {
		
		if(TextUtils.isEmpty(userInfo.getToken())){
			Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(TextUtils.isEmpty(unifyUserId)){
			Toast.makeText(mContext, "获取用户ID失败", Toast.LENGTH_SHORT).show();
			return ;
		}
	    new Thread(){
	    	public void run() {
	    		String pUserId = exchangeUserId(config, unifyUserId);
	    		if(!TextUtils.isEmpty(pUserId)){
	    			Message msg = mHandler.obtainMessage(EXCHANGE_USERID_SUCCESS);
	    			msg.obj = listener ;
	    			Bundle bundle = new Bundle();
	    			bundle.putString("pUserId", pUserId);
	    			msg.setData(bundle);
	    			msg.sendToTarget();
	    		}
	    	};
	    }.start();
	}

	public synchronized static EUSDKExtract getInstance(){
  		if(gyyxSDKExtract==null){
			gyyxSDKExtract = new EUSDKExtract(); 
		}
  		return gyyxSDKExtract ;
  	}
	public  void initGYApp(final EUSDKConfigParameters config,
  			final Activity activity,EUSDKListener listener) {
		//打开日志输出开关 
		Log.setDebugLog(true);
		String oriention = config.getOriention();
		if("landscape".equals(oriention)){
			mIsLandScape = true ;
		}
  		mContext = activity ;
  		userInfo = new EUSDKUserInfo();
  		Matrix.init(activity);
  		uploadStartLog(config, activity);
  		if(listener!=null){
  			listener.onComplete(new Bundle());
  		}
  	}
  	public  void loginGYApp(final EUSDKConfigParameters config,
  			final EUSDKListener listener,final Context context) {
  		doSdkLogin(config,listener);
      }
  	protected void doSdkLogin(final EUSDKConfigParameters config,final EUSDKListener listener) {
  		Bundle bundle = new Bundle();
  		//  界面相关参数，360SDK 界面是否以横屏显示。
  		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, mIsLandScape);
  		//  可选参数，登录界面的背景图片路径，必须是本地图片路径
  		bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");
  		// *** 以下非界面相关参数  ***
  		//  必需参数，使用 360SDK 的登录模块。
  		bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
  		Intent intent = new Intent(mContext, ContainerActivity.class); 
  		intent.putExtras(bundle);
  		Matrix.invokeActivity(mContext, intent, new IDispatcherCallback() {
  		@Override
  		public void onFinished(String data) {
  			procGotTokenInfoResult(config,listener,data);
  			if (isCancelLogin(data))
  			{ 
  				listener.onCancel();
  			}
  		}
  		});
  		}
  	/**
  	* 处理 Token 返回
  	* @param data
  	*/
	private void procGotTokenInfoResult(final EUSDKConfigParameters config,final EUSDKListener listener ,String data) {
		
		if (!TextUtils.isEmpty(data)) {
			JSONObject jsonRes;
			try {
				jsonRes = new JSONObject(data);
				// error_code 状态码： 0 登录成功， -1 登录取消， 其他值：登录失败
				int errorCode = jsonRes.optInt("error_code");
				String dataString = jsonRes.optString("data");
				switch (errorCode) {
				case 0:
					JSONObject jsonObject = new JSONObject(dataString);
					final String token = jsonObject.getString("access_token");
					new Thread(){
						@Override
						public void run() {
							if(!TextUtils.isEmpty(token)){
								boolean result = uploadToken(config, listener, token, "");
								if(result){
									backSuccess(listener, token);
								}
							}
						}
					}.start();
					break;
				case -1:
					//用户取消登陆回调
					listener.onCancel();
					return;
				default:
					if (!TextUtils.isEmpty(dataString)) {
						Toast.makeText(mContext, dataString, Toast.LENGTH_SHORT)
								.show();
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
  	public  void logoutGYApp(EUSDKConfigParameters config,
  			final EUSDKListener listener,Context context) {
  		Intent intent = getQuitIntent(mIsLandScape);
        Matrix.invokeActivity(mContext, intent, new IDispatcherCallback(){
			@Override
			public void onFinished(String data) {
				 JSONObject json;
		            try {
		                json = new JSONObject(data);
		                int which = json.optInt("which", -1);
		                String label = json.optString("label");
		                switch (which) {
		                    case 0: // 用户关闭退出界面
		                       break;
		                    default:// 退出游戏
		                        // 注意：此处代码模拟游戏finish并杀进程退出的场景，对于不杀进程退出的游戏，直接finish即可。
		                      //  doGameKillProcessExit();
		                    	listener.onComplete(new Bundle());
		                    	break;
		                }
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
			}});
  	}
  	
  	public  void rechargeGYApp(final EUSDKConfigParameters config,
  			final String item, final double price, final int count,
  			final String order, final String notifyUrl, final Map obligate,
  			Activity activity, final EUSDKListener listener) {
	  		if(!rechargeParamJudge(price, count, order, listener)){
	  			return ;
	  		}
			
  			new Thread(new Runnable() {
			@Override
			public void run() {
				final JSONObject payID = getPayID(
						config,order, userInfo.getID(),item, count + "", price + "",count*price+"" , notifyUrl,obligate);
				
				baseHandler.post(new Runnable() {
					@Override
					public void run() {
						String result = null;
						String order_nolast = null ;
						String gws_url = null ;
						if(payID==null){
							Bundle bundle1 = new Bundle();
							bundle1.putString("err_message", "服务器生成订单号错误");
							listener.onError(bundle1);
							return ;
						}
						try {
							result = payID.getString("result");
							order_nolast = payID.getString("order_no");
							gws_url = payID.getString("gws_url");
							//gws_url = "http://api.mobile.gyyx.cn/gateway/platform/360/paynotify/20010/";
							userInfo.setID(payID.getString("user_id"));
							userInfo.setName(payID.getString("user_name"));
						
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!("success".equals(result))) {
							Bundle bundle1 = new Bundle();
							bundle1.putString("err_message", "服务器生成订单号错误");
							listener.onError(bundle1);
							return;
						}
						if (order_nolast == null) {
							Bundle bundle2 = new Bundle();
							bundle2.putString("err_message", "服务器生成订单号错误");
							listener.onError(bundle2);
							return;
						}
			    	Intent intent = new Intent(mContext, ContainerActivity.class);
					//  必需参数，使用 360SDK 的支付模块。
					intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);
					 // 可选参数，登录界面的背景图片路径，必须是本地图片路径
			       // intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");
					
			        Bundle bundle = new Bundle();
			    	bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, true);
			        // 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
			    	bundle.putString(ProtocolKeys.ACCESS_TOKEN, userInfo.getToken());
			    	 // 必需参数，360账号id，整数。
			    	bundle.putString(ProtocolKeys.QIHOO_USER_ID,  userInfo.getID());
			    	 //  必需参数，所购买商品金额,  以分为单位。金额大于等于 100 分， 360SDK 运行定额支付流程；   金额数为 0， 360SDK
			    	//运行不定额支付流程。
					bundle.putString(ProtocolKeys.AMOUNT, ((int) (price*100))*count+"");
					// 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
			        bundle.putString(ProtocolKeys.RATE, "1");
			        //必需参数，所购买商品名称，应用指定，建议中文，最大 10 个中文字。
					bundle.putString(ProtocolKeys.PRODUCT_NAME, item);
					// 必需参数，购买商品的商品id，应用指定，最大16字符。
					bundle.putString(ProtocolKeys.PRODUCT_ID, item);
					 //  必需参数，应用方提供的支付结果通知 uri，最大 255 字符。360 服务器将把支付接口回调给该 uri，具体协议请查
			    	//看文档中，支付结果通知接口–应用服务器提供接口。
					bundle.putString(ProtocolKeys.NOTIFY_URI, gws_url);
					//必需参数，游戏或应用名称，最大 16 中文字。
					bundle.putString(ProtocolKeys.APP_NAME, config.getGameName());
					 // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
			        // 充到统一的用户账户，各区服角色均可使用）。
					bundle.putString(ProtocolKeys.APP_USER_NAME, userInfo.getName());
					// 必需参数，应用内的用户id。
			        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
					bundle.putString(ProtocolKeys.APP_USER_ID, userInfo.getID());
			        // 可选参数，应用扩展信息1，原样返回，最大255字符。
			        bundle.putString(ProtocolKeys.APP_EXT_1, "");
			        // 可选参数，应用扩展信息2，原样返回，最大255字符。
			        bundle.putString(ProtocolKeys.APP_EXT_2,"");
					bundle.putString(ProtocolKeys.APP_ORDER_ID,order_nolast);
			    	intent.putExtras(bundle);
					//  界面相关参数，360SDK 登录界面背景是否透明。
					intent.putExtra(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, true);
					final String gyyxOrder = order_nolast ;
					Matrix.invokeActivity(mContext, intent, new IDispatcherCallback() {
						@Override
						public void onFinished(String data) {
					            JSONObject jsonRes;
					            try {
					            	Bundle bundle = new Bundle();
					                jsonRes = new JSONObject(data);
					                // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中。
					                // error_msg 状态描述
					                int errorCode = jsonRes.getInt("error_code");
					                switch (errorCode) {
					                    case 0:
					                    	 bundle.putString("gyyx_order_no",gyyxOrder);
							            	 bundle.putString("game_order_no",order);
							            	 bundle.putString("rmb_yuan",count*price+"");
							            	 listener.onComplete(bundle);
							            	 break;
					                    case 1:
					                    	bundle.putString("code", String.valueOf(errorCode));
					                    	bundle.putString("err_message", jsonRes.getString("error_msg"));
					                    	listener.onError(bundle);
					                    	break;
					                    case -1:
					                    	listener.onCancel();
					                    	break ;
					                    case -2: 
					                    	String errorMsg = jsonRes.optString("error_msg");
					                    	bundle.putString("code", String.valueOf(errorCode));
						                    bundle.putString("err_message", errorMsg);
						                    listener.onError(bundle);
					                    	break ;
					                    case 4010201:
					                    	bundle.putString("code", String.valueOf(errorCode));
					                    	bundle.putString("err_message", "AccessToken已失效，请重新登录");
					                    	listener.onError(bundle);
					                        break;
					                    case 4009911:
					                    	bundle.putString("code", String.valueOf(errorCode));
					                    	bundle.putString("err_message", "QT已失效，请重新登录");
					                    	listener.onError(bundle);
					                        break;
					                    default:
					                        break;
					                }
					            } catch (JSONException e) {
					                e.printStackTrace();
					            }
						}});
					}
				});
		}}).start();
  		}
  	public  void destoryGYApp() {
  		 // 只有在程序退出的时候调用
  		Matrix.destroy(mContext); 
  	}
  	
  	protected void doSdkSwitchAccount(final EUSDKConfigParameters config,final EUSDKListener listener) {
  		Bundle bundle = new Bundle();
  		//  界面相关参数，360SDK 界面是否以横屏显示。
  		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, mIsLandScape);
  		//  可选参数，登录界面的背景图片路径，必须是本地图片路径
  		bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");
  		// *** 以下非界面相关参数  ***
  		//  必需参数，使用 360SDK 的切换账号模块。
  		bundle.putInt(ProtocolKeys.FUNCTION_CODE, 
  		ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);
  		Intent intent = new Intent(mContext, ContainerActivity.class);
  		intent.putExtras(bundle);
  		Matrix.invokeActivity(mContext, intent, new IDispatcherCallback() {
  			@Override
  			public void onFinished(String data) {
  			Log.d(TAG, "mAccountSwitchCallback, data is " + data);
  			procGotTokenInfoResult(config, listener, data);
  			}
  			});
  	}
  	 /***
     * 生成调用360SDK退出接口的Intent
     * 
     * @param isLandScape
     * @return Intent
     */
    private static Intent getQuitIntent(boolean isLandScape) {

        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 必需参数，使用360SDK的退出模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_QUIT);

        Intent intent = new Intent(mContext, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }
    /**
     * 取消登录的处理
     * @param data
     * @return
     */
    private boolean isCancelLogin(String data) {
        try {
            JSONObject joData = new JSONObject(data);
            int errno = joData.optInt("errno", -1);
            if (-1 == errno) {
                //Toast.makeText(SdkUserBaseActivity.this, data, Toast.LENGTH_LONG).show();
                return true;
            }
        } catch (Exception e) {}
        return false;
    }
	@Override
	public void loginEUApp(EUSDKConfigParameters config,
			EUSDKListener listener, Context context) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void rechargeEUApp(EUSDKConfigParameters config, String item,
			double price, int count, String order, String url, Map obligate,
			Activity activity, EUSDKListener listener) {
		// TODO Auto-generated method stub
		
	}

    
 
	
	
}
