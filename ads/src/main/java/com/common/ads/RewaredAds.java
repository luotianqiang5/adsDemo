package com.common.ads;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyV4VCAd;
import com.jirbo.adcolony.AdColonyV4VCListener;
import com.jirbo.adcolony.AdColonyV4VCReward;


/**
 * Created by luotianqiang1 on 16/9/2.
 */
public class RewaredAds extends AdsPlatform implements AdColonyAdAvailabilityListener, AdColonyV4VCListener,AdColonyAdListener,Application.ActivityLifecycleCallbacks {

	public interface RewaredAdsListener extends AdsListener {
		void onRewarded(String var,int amount,boolean isSkip);
	}

	private String APP_ID;
	private String ZONE_ID;
    private AdColonyV4VCAd adColonyV4VCAd;
	public  RewaredAds(Activity var, String app_id,String zone_id){
		super(var);
		APP_ID = app_id;
		ZONE_ID = zone_id;
		contextActivry.getApplication().registerActivityLifecycleCallbacks(this);
	}

	private void initConfig() {
		if(!AdColony.isConfigured()){
			AdColony.configure(contextActivry, "version:10,store:google", APP_ID, ZONE_ID);
			AdColony.addAdAvailabilityListener(this);
			AdColony.addV4VCListener(this);
		}
		 adColonyV4VCAd = (new AdColonyV4VCAd()).withListener(RewaredAds.this);
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
					if(adColonyV4VCAd == null)
						adColonyV4VCAd = (new AdColonyV4VCAd()).withListener(RewaredAds.this);

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
					adColonyV4VCAd = (new AdColonyV4VCAd()).withListener(RewaredAds.this);
					adColonyV4VCAd.show();
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
	public void onAdColonyAdAvailabilityChange(boolean b, String s) {
		isLoad  = b;
		if(listener != null) {
			if(b) {
				listener.onLoadedSuccess(this);
				if(isAutoShow)
					show();
			}
//			else
//				listener.onLoadedFail(this);
		}
	}

	@Override
	public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
		FullScreenAds.setFullScreenAdsShowing(false);
		if(adColonyAd.skipped() || adColonyAd.canceled()) {
			if(listener != null && listener instanceof RewaredAdsListener) {
				((RewaredAdsListener)listener).onRewarded("", -1, true);
			}
			if(listener != null)
				listener.onAdsClosed(this);
		}else if(adColonyAd.shown()){
			if(listener != null)
				listener.onAdsClosed(this);
		}
	}

	@Override
	public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
		FullScreenAds.setFullScreenAdsShowing(true);
		if(listener != null)
			listener.onAdsOpened(this);
	}

	@Override
	public void onAdColonyV4VCReward(AdColonyV4VCReward adColonyV4VCReward) {
		if(adColonyV4VCReward.success()) {
			if(listener != null && listener instanceof  RewaredAdsListener) {
				((RewaredAdsListener) listener).onRewarded(adColonyV4VCReward.name(), adColonyV4VCReward.amount(),false);
			}
		}
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
		AdColony.resume(activity);
	}

	@Override
	public void onActivityPaused(Activity activity) {
		if(activity == contextActivry)
		AdColony.pause();
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
