package com.common.ads;

import android.app.Activity;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by luotianqiang1 on 16/9/5.
 */
public class InterstitialAds extends AdsPlatform {

	boolean isConfig = false;
	private String ZONE_ID;
	private AdColonyInterstitial adcad = null;

	private boolean isPreloading = false;
	private AdColonyAdOptions ad_options;


	private AdColonyInterstitialListener adclistener = new AdColonyInterstitialListener() {
		@Override
		public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
			if(isEqualsZoneId(adColonyInterstitial.getZoneID())) {
				isPreloading = false;
				adcad = adColonyInterstitial;
				isLoad  = true;
				if(listener != null) {

					listener.onLoadedSuccess(InterstitialAds.this);
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
					listener.onLoadedFail(InterstitialAds.this);
			}
		}

		/** Ad opened, reset UI to reflect state change */
		@Override
		public void onOpened( AdColonyInterstitial ad )
		{
			if(isEqualsZoneId(ad.getZoneID())){
				FullScreenAds.setFullScreenAdsShowing(true);
				if(listener != null)
					listener.onAdsOpened(InterstitialAds.this);
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
				if (listener != null)
					listener.onAdsClosed(InterstitialAds.this);
			}
		}
	};
	private  boolean isEqualsZoneId(String str){
		return ZONE_ID.equals(str);
	}
	void initConfig() {
		if(!isConfig){
			isConfig = true;
			ad_options = new AdColonyAdOptions()
					.enableConfirmationDialog(false)
					.enableResultsDialog(false);

		}
	}

	public InterstitialAds(Activity var1,String adUnitId){
		super(var1);
		this.ZONE_ID = adUnitId;
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
							listener.onLoadedSuccess(InterstitialAds.this);
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
		}else if(isLoaded()&& adcad != null){
			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					initConfig();
					if(adcad != null)
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
	public int getAdsType() {
		return AdsType.INTERSTITIAL;
	}
}
