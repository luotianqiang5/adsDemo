package com.common.ads;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.ads.mediation.admob.AdMobAdapter;
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
	private final Object mLock = new Object();
	private boolean mIsRewardedVideoLoading;
	private boolean isSkip = true;
    private int count = -1;

	public  RewaredAds(Activity var, String app_id,String zone_id){
		super(var);
		APP_ID = app_id;
		ZONE_ID = zone_id;
		contextActivry.getApplication().registerActivityLifecycleCallbacks(this);
		RewardedVideoAd mAd = MobileAds.getRewardedVideoAdInstance(contextActivry);
		mAd.setRewardedVideoAdListener(this);
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
					if (isAutoShow)
						show();
				} else {
					synchronized (mLock) {
						if (!mIsRewardedVideoLoading) {
							isLoad = false;
							mIsRewardedVideoLoading = true;
							RewardedVideoAd mAd = MobileAds.getRewardedVideoAdInstance(contextActivry);
							Bundle extras = new Bundle();
							extras.putBoolean("_noRefresh", true);
							AdRequest adRequest = new AdRequest.Builder()
									.addNetworkExtrasBundle(AdMobAdapter.class, extras)
									.build();
							mAd.loadAd(ZONE_ID, adRequest);
						}
					}
				}
			}
		});
	}

	@Override
	public boolean show() {
		if(FullScreenAds.isFullScreenAdsShowing()) {
			return true;
		}else if(isLoaded()){
			isSkip = true;
			count = -1;
			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					MobileAds.getRewardedVideoAdInstance(contextActivry).show();
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
	public void onRewardedVideoAdLoaded() {
		synchronized (mLock) {
			isLoad = true;
			mIsRewardedVideoLoading = false;
		}
		if(listener != null) {
			listener.onLoadedSuccess(this);
			if(isAutoShow)
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
	public void onRewardedVideoStarted() {

	}

	@Override
	public void onRewardedVideoAdClosed() {
		isLoad = false;
		FullScreenAds.setFullScreenAdsShowing(false);
		if(listener != null) {
			if(listener instanceof  RewaredAdsListener)
				((RewaredAdsListener) listener).onRewarded("Reward", count,isSkip);
			else
				listener.onAdsClosed(this);
			preload();
		}
	}

	@Override
	public void onRewarded(RewardItem rewardItem) {
		isSkip = false;
		count = rewardItem.getAmount();
	}

	@Override
	public void onRewardedVideoAdLeftApplication() {

	}

	@Override
	public void onRewardedVideoAdFailedToLoad(int i) {
		isLoad = false;
		synchronized (mLock) {
			mIsRewardedVideoLoading = false;
		}
		if(listener != null)
			listener.onLoadedFail(this);
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
		if(activity == contextActivry)
			MobileAds.getRewardedVideoAdInstance(contextActivry).resume();
	}

	@Override
	public void onActivityPaused(Activity activity) {
		if(activity == contextActivry)
			MobileAds.getRewardedVideoAdInstance(contextActivry).pause();
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
