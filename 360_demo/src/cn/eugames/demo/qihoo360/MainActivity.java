package cn.eugames.demo.qihoo360;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.eugames.extension.common.EUSDKConfigParameters;
import cn.eugames.extension.common.EUSDKListener;
import cn.eugames.extension.utils.ConfigUtils;
import cn.eugames.extension.utils.RestResult;
import cn.eugames.extension.utils.Utils;
import cn.eugames.platform.EUSDKUnify;

public class MainActivity extends Activity {

	private EUSDKUnify sdk;
	private String token = "cce02373-2db0-48f2-aa02-892b5bcac8dc";
	private String notify_url = "http://api.mobile.gyyx.cn/api/DemoGwsGameServerS";
	private TextView tvToken, tvMethods, tvFinger;
	private String userId = null;
	private Map<String, String> extendMap = new HashMap();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvToken = (TextView) findViewById(R.id.tv_token);
		tvMethods = (TextView) findViewById(R.id.tv_methods);
		tvFinger = (TextView) findViewById(R.id.tv_finger);
		EUSDKUnify.getInstance().init(MainActivity.this, initListener);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	private EUSDKListener initListener = new EUSDKListener() {
		@Override
		public void onException(Exception arg0) {
		}

		@Override
		public void onError(Bundle arg0) {
			Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onComplete(Bundle arg0) {
			Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onCancel() {
		}
	};

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btn_eugames_login:
			EUSDKUnify.getInstance().login(MainActivity.this, loginListener);
			break;

		case R.id.btn_eugames_logout:
			EUSDKUnify.getInstance()
					.logout(MainActivity.this, loginOutListener);
			break;

		case R.id.btn_user_center:
			EUSDKUnify.getInstance().showUserCenter(this, null);
			break;
		case R.id.btn_eugames_pay:
			recharge();
			break;
		case R.id.btn_finger:
			EUSDKUnify.getInstance().getEULifeFingerprint(new EUSDKListener() {
				@Override
				public void onCancel() {
				}

				@Override
				public void onComplete(Bundle bundle) {
					tvFinger.setText("指纹:" + bundle.getString("finger_print"));
				}

				@Override
				public void onError(Bundle bundle) {
					Toast.makeText(MainActivity.this,
							bundle.getString("err_message"), Toast.LENGTH_LONG)
							.show();
				}

				@Override
				public void onException(Exception arg0) {
				}
			});
			break;
		case R.id.btn_upload_login_log:
			extendMap.put("roleId", "testRoleId");
			extendMap.put("roleName", "testRoleName");
			extendMap.put("roleLevel", "testRoleLevel");
			extendMap.put("serverId", "testServerId");
			extendMap.put("serverName", "testServerName");
			extendMap.put("userId", userId);

			EUSDKUnify.getInstance().postEnterGameLog(extendMap, this);
			break;
		case R.id.btn_required_method:
			String[] a = new String[] { "recharge", "usercenter", "logout",
					"showFloatWindow", "addictionPrevention",
					"realNameRegister", "checkUpdate" };
			boolean[] methods = EUSDKUnify.getInstance().hasMethods(a);
			String s = "";
			for (int i = 0; i < methods.length; i++) {
				s = s + a[i] + ":" + methods[i] + "--";
			}

			tvMethods.setText(s);
			break;

		case R.id.btn_eugames_real_name:
			EUSDKUnify.getInstance().realNameRegister(userId,
					new EUSDKListener() {
						@Override
						public void onException(Exception arg0) {
						}

						@Override
						public void onError(Bundle arg0) {
						}

						@Override
						public void onComplete(Bundle arg0) {
						}

						@Override
						public void onCancel() {
						}
					});
			break;
		case R.id.btn_eugames_addiction:
			EUSDKUnify.getInstance().addictionPrevention(userId,
					new EUSDKListener() {
						@Override
						public void onException(Exception arg0) {
						}

						@Override
						public void onError(Bundle arg0) {
							Toast.makeText(MainActivity.this, "未成年", 0).show();
						}

						@Override
						public void onComplete(Bundle arg0) {
							Toast.makeText(MainActivity.this, "已成年 ", 0).show();
						}

						@Override
						public void onCancel() {
							Toast.makeText(MainActivity.this, "无该数据", 0).show();
						}
					});
			break;
		case R.id.btn_upload_updated_log:

			extendMap.put("updateDataId", "updateDataId");
			extendMap.put("updateProgress", "updateProgress");
			extendMap.put("updateIsSuccess", "true");
			EUSDKUnify.getInstance().postUpdatedGameLog(extendMap,
					MainActivity.this);
			break;
		case R.id.btn_upload_updating_log:
			extendMap.put("updateDataId", "updateDataId");
			EUSDKUnify.getInstance().postUpdatingGameLog(extendMap,
					MainActivity.this);

		default:
			break;
		}
	}

	private EUSDKListener loginListener = new EUSDKListener() {
		@Override
		public void onException(Exception arg0) {
		}

		@Override
		public void onError(Bundle bundle) {
			Toast.makeText(MainActivity.this, bundle.getString("err_message"),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onComplete(Bundle bundle) {
			System.out.println(bundle.getString("token"));
			tvToken.setText(bundle.toString());
			final String token = bundle.getString("token");
			new Thread() {

				public void run() {
					try {
						EUSDKConfigParameters config = config = ConfigUtils
								.refineConfigMsg(MainActivity.this);
						HashMap<String, String> header = new HashMap<String, String>();
						header.put("client_id", config.getClientId());
						// header.put("extend_id", config.getExtend_id());
						header.put("token", token);
						header.put("timestamp", String.valueOf(System
								.currentTimeMillis() / 1000));
						// "http://api.mobile.gyyx.cn/api/GatewayOrder/"
						header.put("sign", Utils.sign("/api/ChargeUser/?"
								+ Utils.signString(header),
								"c9b1d6a6527e4fdelyjlefddcc0e1ff3", "UTF-8"));

						/**
						 * c9b1d6a6527e4fde99f0efddcc0e1ff3
						 * c9b1d6a6527e4fdelyjlefddcc0e1ff3
						 */
						header.put("sign_type", Utils.SIGN_TYPE);
						// 此请求方式已经更新
						RestResult restResult = Utils.euApiRequest("GET",
								"http://api.mobile.gyyx.cn/api/ChargeUser/",
								Utils.encodeQueryString(header));
						if (restResult.getContent() == null) {
							Utils.logD("请求服务器失败,未返回状态码");

						}
						if (restResult.getStatusCode() == 200) {
							Utils.logD("请求服务器成功");
							JSONObject jj = null;

							jj = new JSONObject(restResult.getContent());
							userId = jj.getString("user_id");

						} else {
							Utils.logD("请求服务器失败，返回状态码非200");

						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				};
			}.start();
		}

		@Override
		public void onCancel() {
		}
	};
	private EUSDKListener loginOutListener = new EUSDKListener() {
		@Override
		public void onException(Exception arg0) {
			Toast.makeText(MainActivity.this, "用户注销成功", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onError(Bundle arg0) {
			Toast.makeText(MainActivity.this, "用户注销成功", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onComplete(Bundle arg0) {
			Toast.makeText(MainActivity.this, "注销回调:用户注销成功", Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(MainActivity.this, "用户取消注销", Toast.LENGTH_LONG)
					.show();
		}
	};
	private EUSDKListener rechargeListener = new EUSDKListener() {
		@Override
		public void onException(Exception arg0) {
		}

		@Override
		public void onError(Bundle arg0) {
			Toast.makeText(MainActivity.this,
					"回调:" + arg0.getString("err_message"), Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onComplete(Bundle arg0) {
			String result = "支付回调成功:支付成功，艺游SDK生成的订单号是 "
					+ arg0.getString("gyyx_order_no");
			Toast.makeText(MainActivity.this, result, 0).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(MainActivity.this, "回调：用户取消了支付", 0).show();
		}
	};

	public void recharge() {
		String item = ((EditText) findViewById(R.id.edit_eugames_item))
				.getText().toString();
		if (TextUtils.isEmpty(item)) {
			Toast.makeText(getApplicationContext(), "物品名称 不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String order = ((EditText) findViewById(R.id.edit_eugames_orderno))
				.getText().toString();
		if (TextUtils.isEmpty(order)) {
			Toast.makeText(getApplicationContext(), "订单号 不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String amountNum = ((EditText) findViewById(R.id.edit_eugames_goods_code))
				.getText().toString();
		if (TextUtils.isEmpty(amountNum)) {
			Toast.makeText(getApplicationContext(), "数量不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (!isNumeric(amountNum)) {
			Toast.makeText(getApplicationContext(), "购买数量应该全为数字！",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String singlePrice = ((EditText) findViewById(R.id.edit_eugames_singleprice))
				.getText().toString();
		if (TextUtils.isEmpty(singlePrice)) {
			Toast.makeText(getApplicationContext(), "单价不能为空！",
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (amountNum.length() > 9) {
			Toast.makeText(MainActivity.this, "数量超过允许范围", 0).show();
			return;
		}

		if (singlePrice.length() > 9) {
			Toast.makeText(MainActivity.this, "单价超过允许范围", 0).show();
			return;
		}

		if (userId == null) {
			Toast.makeText(this, "userid不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		Map obligate = new HashMap();
		obligate.put("privatedata", "it is a test");
		EUSDKUnify.getInstance().recharge(MainActivity.this, userId, item,
				Double.parseDouble(singlePrice), Integer.parseInt(amountNum),
				order, notify_url, obligate, rechargeListener);
	}

	public boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		EUSDKUnify.getInstance().exit(MainActivity.this, new EUSDKListener() {
			@Override
			public void onException(Exception arg0) {
			}

			@Override
			public void onError(Bundle arg0) {
			}

			@Override
			public void onComplete(Bundle arg0) {
				MainActivity.this.finish();
			}

			@Override
			public void onCancel() {
			}
		});
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EUSDKUnify.getInstance().resume(this);
	};

	@Override
	protected void onPause() {
		super.onPause();
		EUSDKUnify.getInstance().pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EUSDKUnify.getInstance().destory();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
