package com.common.ads;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adcolony.sdk.*;
/**
 * Created by luotianqiang1 on 16/9/2.
 */
public class RewaredAds extends AdsPlatform  {

	public interface RewaredAdsListener extends AdsListener {
		void onRewarded(String var,int amount,boolean isSkip);
	}

	boolean isConfig = false;
	private String APP_ID;
	private String ZONE_ID;
	private AdColonyInterstitial adcad = null;
	AdColonyReward adReward = null;
	private boolean isPreloading = false;
	private AdColonyInterstitialListener adclistener = new AdColonyInterstitialListener() {
		@Override
		public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
			if(isEqualsZoneId(adColonyInterstitial.getZoneID())) {
				isPreloading = false;
			adcad = adColonyInterstitial;
		isLoad  = true;
		if(listener != null) {

			listener.onLoadedSuccess(RewaredAds.this);
			if (isAutoShow)
				show();
		}
		}
		}

		/** Ad request was not filled */
		@Override
		public void onRequestNotFilled( AdColonyZone zone )
		{
			if(isEqualsZoneId(zone.getZoneID())) {
				isPreloading = false;
				isLoad = false;
				if(listener != null)
					listener.onLoadedFail(RewaredAds.this);
			}
		}

		/** Ad opened, reset UI to reflect state change */
		@Override
		public void onOpened( AdColonyInterstitial ad )
		{
			if(isEqualsZoneId(ad.getZoneID())){
				adReward = null;
						FullScreenAds.setFullScreenAdsShowing(true);
		if(listener != null)
			listener.onAdsOpened(RewaredAds.this);
			}
		}

		/** Request a new ad if ad is expiring */
		@Override
		public void onExpiring( AdColonyInterstitial ad )
		{
			if(isEqualsZoneId(ad.getZoneID())){
				isLoad = false;
				adcad = null;
			}
		}

		public void onClosed(AdColonyInterstitial ad) {
			if (isEqualsZoneId(ad.getZoneID())) {
				adcad = null;
				isLoad = false;
				FullScreenAds.setFullScreenAdsShowing(false);
				if (adReward == null|| !adReward.success()) {
					if (listener != null && listener instanceof RewaredAdsListener) {
						((RewaredAdsListener) listener).onRewarded("", -1, true);
					}

				}
				if (listener != null)
					listener.onAdsClosed(RewaredAds.this);
			}
		}
	};
	private AdColonyAdOptions ad_options;
	public  RewaredAds(Activity var, String app_id,String zone_id){
		super(var);
		APP_ID = app_id;
		ZONE_ID = zone_id;
	}

	private  boolean isEqualsZoneId(String str){
		return ZONE_ID.equals(str);
	}

	private void initConfig() {
		if(!isConfig){
			isConfig = true;
			ad_options = new AdColonyAdOptions()
					.enableConfirmationDialog(false)
					.enableResultsDialog(false);
			AdColony.setRewardListener( new AdColonyRewardListener()
			{
				@Override
				public void onReward( AdColonyReward reward )
				{
					if(isEqualsZoneId(reward.getZoneID())){
						RewaredAds.this.adReward = reward;
								if(reward.success()) {
			if(listener != null && listener instanceof  RewaredAdsListener) {
				((RewaredAdsListener) listener).onRewarded(reward.getRewardName(), reward.getRewardAmount(),false);
			}
		}
					}
				}
			} );

		}
	}

	@Override
	public void preload() {
		if(!isPreloading) {
			isPreloading = true;
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
						initConfig();
						if (adcad == null || adcad.isExpired()) {
							AdColony.requestInterstitial(ZONE_ID, adclistener, ad_options);
						}
					}
				}
			});
		}
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
					adcad.show();
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


}
