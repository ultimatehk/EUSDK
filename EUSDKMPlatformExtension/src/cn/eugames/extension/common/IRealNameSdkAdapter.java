/**
 * 实名注册接口，360必接
 * @author huangke
 */
package cn.eugames.extension.common;

public interface IRealNameSdkAdapter {
 void realNameRegister(EUSDKConfigParameters config,String userId,EUSDKListener listener);
 
}
