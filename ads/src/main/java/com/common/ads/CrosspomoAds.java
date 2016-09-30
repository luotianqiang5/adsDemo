package com.common.ads;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

/**
 * Created by luotianqiang1 on 16/9/5.
 */
public class CrosspomoAds extends AdsPlatform implements Application.ActivityLifecycleCallbacks{
	private String appId;
	private String appSignature;
	private boolean isDismissIntersiticalCallbacked;
	private boolean isCrossporoAdsShowing;
	ChartboostDelegate delegate = new ChartboostDelegate() {
		public boolean shouldDisplayInterstitial(String location) {
			return true;
		}

		public void didCacheInterstitial(String location) {
			isLoad = true;
			if(listener != null)
				listener.onLoadedSuccess(CrosspomoAds.this);

		}

		public void didFailToLoadInterstitial(String location, CBImpressionError error) {
			//isLoad = false;
			if(listener != null)
				listener.onLoadedFail(CrosspomoAds.this);
		}

		public void didDismissInterstitial(String location) {
			isLoad = false;
			isDismissIntersiticalCallbacked = true;
			FullScreenAds.setFullScreenAdsShowing(false);
			if(listener != null)
				listener.onAdsClosed(CrosspomoAds.this);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					isCrossporoAdsShowing = false;
				}
			}, 300L);
			preload();
		}

		public void didCloseInterstitial(String location) {


		}

		public void didClickInterstitial(String location) {


			if(listener != null)
				listener.onAdsClicked(CrosspomoAds.this);
		}

		public void didDisplayInterstitial(String location) {

			FullScreenAds.setFullScreenAdsShowing(true);
		    isCrossporoAdsShowing = true;
			if(listener != null) {
				listener.onAdsOpened(CrosspomoAds.this);
			}
		}
	};

	private void initConfig() {
		Chartboost.startWithAppId(contextActivry,appId,appSignature);
		Chartboost.setLoggingLevel(CBLogging.Level.ALL);
		Chartboost.setDelegate(this.delegate);
		Chartboost.setImpressionsUseActivities(true);
		Chartboost.setShouldRequestInterstitialsInFirstSession(true);
		Chartboost.setAutoCacheAds(false);
		Chartboost.setShouldDisplayLoadingViewForMoreApps(false);
		Chartboost.onCreate(contextActivry);
	}

	public CrosspomoAds(Activity var1,String appId,String appSignature){
		super(var1);
		contextActivry.getApplication().registerActivityLifecycleCallbacks(this);
		this.appId = appId;
		this.appSignature = appSignature;
		initConfig();
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {
		if(activity == contextActivry)
			Chartboost.onStart(activity);
	}

	@Override
	public void onActivityResumed(Activity activity) {
		if(activity == contextActivry) {
			Chartboost.onResume(activity);
			if(this.isCrossporoAdsShowing && Chartboost.getDelegate() != null && !this.isDismissIntersiticalCallbacked) {
				Chartboost.getDelegate().didDismissInterstitial(CBLocation.LOCATION_DEFAULT);
			}
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
		if(activity == contextActivry) {
			Chartboost.onPause(activity);
			if(this.isLoad) {
				this.isDismissIntersiticalCallbacked = false;
			}
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {
		if(activity == contextActivry) {
			Chartboost.onStop(activity);
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		if(activity == contextActivry) {
			Chartboost.onDestroy(activity);
		}
		destroy();
	}

	@Override
	public void preload() {

			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
				}
			});

	}

	@Override
	public boolean show() {
		if(FullScreenAds.isFullScreenAdsShowing())
		return true;
		else if(isLoaded()&&contextActivry != null){
			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
				}
			});
			return true;
		}else
			return false;
	}


	@Override
	public void destroy() {

	}

	@Override
	public int getAdsType() {
		return AdsType.CROSS;
	}
}
