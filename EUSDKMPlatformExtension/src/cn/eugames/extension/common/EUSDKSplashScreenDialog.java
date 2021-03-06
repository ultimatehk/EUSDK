package cn.eugames.extension.common;

import cn.eugames.extension.utils.RHelper;
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class EUSDKSplashScreenDialog extends Dialog {

	private ImageView image;

	public EUSDKSplashScreenDialog(Context context, final InitListener initListener,int imgID) {
		super(context,RHelper.getStyleResIDByName(context, "gydialog"));
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(RHelper.getLayoutResIDByName(context, "layout_splash"));
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		image = (ImageView) this.findViewById(RHelper.getIdResIDByName(
				context, "img_gysplash"));
		image.setBackgroundResource(imgID);
		Animation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				EUSDKSplashScreenDialog.this.cancel();
				initListener.OnSuccess();
			}
		});
		image.startAnimation(alphaAnimation);
	}

	public interface InitListener {
		public void OnSuccess();
	}

}
