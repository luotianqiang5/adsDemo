package com.common.ads;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by luotianqiang1 on 16/9/5.
 */
public class InterstitialAds extends AdsPlatform {
	private String adUnitId;
	private InterstitialAd admobInterstitial;

	private AdListener admobAdListener = new AdListener() {
		@Override
		public void onAdClosed() {
			super.onAdClosed();
			isLoad = false;
			FullScreenAds.setFullScreenAdsShowing(false);
			if(listener != null)
				listener.onAdsClosed(InterstitialAds.this);
			preload();
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			super.onAdFailedToLoad(errorCode);
			isLoad = false;
			if(listener != null)
				listener.onLoadedFail(InterstitialAds.this);
		}

		@Override
		public void onAdLeftApplication() {
			super.onAdLeftApplication();
		}

		@Override
		public void onAdOpened() {
			super.onAdOpened();
			FullScreenAds.setFullScreenAdsShowing(true);
			if(listener != null)
				listener.onAdsOpened(InterstitialAds.this);
		}

		@Override
		public void onAdLoaded() {
			super.onAdLoaded();
			isLoad = true;
			if(listener != null)
				listener.onLoadedSuccess(InterstitialAds.this);
		}
	};

	void initConfig() {
		if(admobInterstitial == null) {
			admobInterstitial = new InterstitialAd(contextActivry);
			admobInterstitial.setAdUnitId(adUnitId);
			admobInterstitial.setAdListener(admobAdListener);
		}
	}

	public InterstitialAds(Activity var1,String adUnitId){
		super(var1);
		this.adUnitId = adUnitId;
	}

	@Override
	public void preload() {

		contextActivry.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				initConfig();
				if (admobInterstitial != null&& !admobInterstitial.isLoaded()) {
					isLoad = false;
					AdRequest adRequest = new AdRequest.Builder().build();
					try {
						admobInterstitial.loadAd(adRequest);
					} catch (Exception var3) {
						if (listener != null) {
							listener.onLoadedFail(InterstitialAds.this);
						}
					}
				}
			}
		});
	}

	@Override
	public boolean show() {
		if(FullScreenAds.isFullScreenAdsShowing())
			return true;
		else if(admobInterstitial != null && contextActivry != null&&isLoaded()) {
			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
						admobInterstitial.show();
				}
			});
			return true;
		}else {
			preload();
			return false;
		}
	}


	@Override
	public void destroy() {

	}

	@Override
	public int getAdsType() {
		return AdsType.INTERSTITIAL;
	}
}
