package cn.eugames.extension.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class ProgressbarHelper {
	private static ProgressbarHelper loginProgressbar;
	ProgressBar progressBar;
	private WindowManager wm;
	private Context mContext;
	private boolean isShowing = false;
	private WindowManager.LayoutParams wmParams;
	Handler mHandler;

	private ProgressbarHelper(Activity activity) {
		mContext = activity;
		progressBar = new ProgressBar(activity);

		int orientation = activity.getRequestedOrientation();
		// progressBar.setMax(100);
		// progressBar.sets
		wm = ((WindowManager) activity.getSystemService("window"));
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2010;
		wmParams.format = 1;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.flags = wmParams.FLAG_NOT_FOCUSABLE;
		int width = (int) (60 * getScleX(activity));
		int screenWidth = 0, screenHeight = 0;
		DisplayMetrics display = activity.getResources().getDisplayMetrics();
		screenWidth = display.widthPixels;
		screenHeight = display.heightPixels;
		if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
				|| orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			if (screenWidth < screenHeight) {
				int temp = screenHeight;
				screenHeight = screenWidth;
				screenWidth = temp;
			}
		}
		wmParams.x = (screenWidth - width) / 2;
		wmParams.y = (screenHeight - width) / 2;
		wmParams.width = width;
		wmParams.height = width;
	}

	private float getScleX(Context context) {
		DisplayMetrics display = context.getResources().getDisplayMetrics();
		int screenWidth = display.widthPixels;
		int screenHeight = display.heightPixels;
		if (screenWidth > screenHeight) {
			screenWidth = screenHeight;
		}
		float scaleX = screenWidth / 320.0F;
		return scaleX;
	}

	public static ProgressbarHelper getInstance(Activity activity) {
		if (loginProgressbar == null) {
			loginProgressbar = new ProgressbarHelper(activity);
		}
		return loginProgressbar;
	}

	public void showProgressbar() {
		if (isShowing) {
			return;
		}
		wm.addView(progressBar, wmParams);
		isShowing = true;
	}

	public void hideProgressbar() {
		if (isShowing) {
			wm.removeView(progressBar);
			isShowing = false;
		}
	}
}