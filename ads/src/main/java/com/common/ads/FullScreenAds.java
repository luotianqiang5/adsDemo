package com.common.ads;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by luotianqiang1 on 16/9/5.
 */
public class FullScreenAds {
	  protected static boolean fullScreenAdsShowing;

	public static boolean isFullScreenAdsShowing() {
		return fullScreenAdsShowing;
	}

	public static void setFullScreenAdsShowing(final  boolean fullScreenAdsShowing) {
		if(fullScreenAdsShowing) {
			FullScreenAds.fullScreenAdsShowing = true;
		}
		else {
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					FullScreenAds.fullScreenAdsShowing = false;
				}
			}, 300L);
		}
	}
}
