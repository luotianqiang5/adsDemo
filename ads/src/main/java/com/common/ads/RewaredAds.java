package com.common.ads;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


/**
 * Created by luotianqiang1 on 16/9/2.
 */
public class RewaredAds extends AdsPlatform implements RewardedVideoAdListener,Application.ActivityLifecycleCallbacks {

	public interface RewaredAdsListener extends AdsListener {
		void onRewarded(String var,int amount,boolean isSkip);
	}

	private String APP_ID;
	private String ZONE_ID;
    private RewardedVideoAd mRewardedVideoAd = null;
	public  RewaredAds(Activity var, String app_id,String zone_id){
		super(var);
		APP_ID = app_id;
		ZONE_ID = zone_id;
		contextActivry.getApplication().registerActivityLifecycleCallbacks(this);
	}

	private void initConfig() {
		if(mRewardedVideoAd == null){
			mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(contextActivry);
			mRewardedVideoAd.setRewardedVideoAdListener(this);
		}
	}

	@Override
	public void preload() {
		contextActivry.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (isLoaded()) {
					if (listener != null) {
						listener.onLoadedSuccess(RewaredAds.this);
					}
					if(isAutoShow)
						show();
				}else {
					initConfig();
					mRewardedVideoAd.loadAd(APP_ID,new AdRequest.Builder().build());
				}
			}
		});
	}

	@Override
	public boolean show() {
		if(FullScreenAds.isFullScreenAdsShowing()) {
			return true;
		}else if(isLoaded()){
			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					initConfig();
					mRewardedVideoAd.show();
				}
			});
			return true;
		}else {
			preload();
			return false;
		}
	}


	@Override
	public  void destroy(){

	}

	@Override
	public  int getAdsType(){
		return AdsType.REWARD;
	}

	@Override
	public void onRewardedVideoAdLeftApplication() {

	}

	@Override
	public void onRewardedVideoAdClosed() {
		FullScreenAds.setFullScreenAdsShowing(false);
		if(listener != null)
			listener.onAdsClosed(this);
	}

	@Override
	public void onRewardedVideoAdFailedToLoad(int errorCode) {
		isLoad  = false;
		if(listener != null) {
			listener.onLoadedFail(this);
		}
	}

	@Override
	public void onRewardedVideoAdLoaded() {
		isLoad  = true;
		if(listener != null) {
			listener.onLoadedSuccess(this);
			if (isAutoShow)
				show();
		}
	}

	@Override
	public void onRewardedVideoAdOpened() {
		FullScreenAds.setFullScreenAdsShowing(true);
		if(listener != null)
			listener.onAdsOpened(this);
	}

	@Override
	public void onRewarded(RewardItem reward) {
		if(listener != null && listener instanceof  RewaredAdsListener) {
				((RewaredAdsListener) listener).onRewarded("rewarded", reward.getAmount(),false);
		}
	}

	@Override
	public void onRewardedVideoStarted() {

	}



	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
		if(activity == contextActivry && mRewardedVideoAd != null)
			mRewardedVideoAd.resume();
	}

	@Override
	public void onActivityPaused(Activity activity) {
		if(activity == contextActivry&& mRewardedVideoAd != null)
			mRewardedVideoAd.pause();
	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}
}
