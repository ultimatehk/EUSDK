package cn.eugames.extension.common;

import android.os.Bundle;

public interface EUSDKListener {
		public void onCancel();

		public void onComplete(Bundle arg0);

		public void onError(Bundle arg0);

		public void onException(Exception arg0);
}
