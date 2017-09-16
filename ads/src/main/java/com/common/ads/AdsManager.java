package com.common.ads;

import android.app.Activity;
import android.util.Log;

/**
 * Created by luotianqiang1 on 16/9/6.
 */
public class AdsManager {
	static private final String PAD_BANNER_KEY = "Pad Banner";
	static private final String PHONE_BANNER_KEY = "Phone Banner";
	static private final String PAD_INTERSTITIAL_KEY = "Pad Interstitial";
	static private final String PHONE_INTERSTITIAL_KEY = "Phone Interstitial";
    static private final String REWARDED_APPID = "Rewarded AppID";
	static private final String REWARDED_SIGNARURE = "Rewarded Signature";
	static private final String CROSSPROMOTION_APPID = "Crosspromotion AppID";
	static private final String CROSSPROMOTION_SIGNARURE = "Crosspromotion Signature";

	private Activity context;
	static private AdsManager instance;

	CrosspomoAds crosspomoAds;
	InterstitialAds interstitialAds;
	RewaredAds rewaredAds;
	BannerAds bannerAds;


	RewaredAds.RewaredAdsListener rewaredAdsListener = new RewaredAds.RewaredAdsListener() {
		@Override
		public void onRewarded(String var, int amount,boolean isSkip) {
			AdsManager.this.onRewarded(AdsType.REWARD,var, amount,isSkip);
		}

		@Override
		public void onLoadedSuccess(AdsPlatform var) {
			AdsManager.this.onLoadedSuccess(var.getAdsType());
		}

		@Override
		public void onLoadedFail(AdsPlatform var) {
			AdsManager.this.onLoadedFail(var.getAdsType());
		}

		@Override
		public void onAdsOpened(AdsPlatform var) {
			AdsManager.this.onAdsOpened(var.getAdsType());
		}

		@Override
		public void onAdsClosed(AdsPlatform var) {
			AdsManager.this.onAdsClosed(var.getAdsType());
		}

		@Override
		public void onAdsClicked(AdsPlatform var) {
			AdsManager.this.onAdsClicked(var.getAdsType());
		}

		@Override
		public void onOtherEvent(AdsPlatform var, String tag, Object... data) {
			AdsManager.this.onOtherEvent(var.getAdsType(),tag,data);
		}
	};

	AdsListener adsListener = new AdsListener() {
		@Override
		public void onLoadedSuccess(AdsPlatform var) {
			AdsManager.this.onLoadedSuccess(var.getAdsType());
		}

		@Override
		public void onLoadedFail(AdsPlatform var) {
			AdsManager.this.onLoadedFail(var.getAdsType());
		}

		@Override
		public void onAdsOpened(AdsPlatform var) {
			AdsManager.this.onAdsOpened(var.getAdsType());
		}

		@Override
		public void onAdsClosed(AdsPlatform var) {
			AdsManager.this.onAdsClosed(var.getAdsType());
		}

		@Override
		public void onAdsClicked(AdsPlatform var) {
			AdsManager.this.onAdsClicked(var.getAdsType());
		}

		@Override
		public void onOtherEvent(AdsPlatform var, String tag, Object... data) {
			AdsManager.this.onOtherEvent(var.getAdsType(),tag,data);
		}
	};

	native void init();
	native void onRewarded(int adsType,String var, int amount,boolean isSkip);
	native void onLoadedSuccess(int adsType);
	native void onLoadedFail(int adsType);
	native void onAdsOpened(int adsType);
	native void onAdsClosed(int adsType);
	native void onAdsClicked(int adsType);
	native void onOtherEvent(int adsTyp, String tag, Object... data);

	private AdsManager(Activity var) {
		context = var;
	}

	public static AdsManager getInstance(Activity var) {
		if(instance == null)
			instance = new AdsManager(var);
		return instance;
	}

	public void setUpWithJni(int adType){
		init();
		setup(adType);
		if(bannerAds != null)
			bannerAds.setListener(adsListener);
		if(crosspomoAds != null)
			crosspomoAds.setListener(adsListener);
		if(rewaredAds != null)
			rewaredAds.setListener(rewaredAdsListener);
		if(interstitialAds != null)
			interstitialAds.setListener(adsListener);
	}


	public void setup(int adType){
		if((AdsType.INTERSTITIAL&adType) == AdsType.INTERSTITIAL || (AdsType.REWARD & adType) == AdsType.REWARD){
			String appId = AdsUitl.getMetaData(context,REWARDED_APPID);
			String signature = AdsUitl.getMetaData(context,REWARDED_SIGNARURE);
			String key;
			if(AdsUitl.isTable(context))
				key = PAD_INTERSTITIAL_KEY;
			else
				key = PHONE_INTERSTITIAL_KEY;
			String value = AdsUitl.getMetaData(context,key);
			if(value != null&&!value.isEmpty() &&appId != null&&signature != null && !appId.isEmpty() && !signature.isEmpty()) {
				AdcolonyManager.getInstance().config(context,appId,signature,value);
			}
		}

		if((AdsType.BANNER&adType) == AdsType.BANNER) {
			String key;
			if(AdsUitl.isTable(context))
				key = PAD_BANNER_KEY;
			else
			    key = PHONE_BANNER_KEY;
			String value = AdsUitl.getMetaData(context,key);
			if(key != null&&!key.isEmpty()) {
				bannerAds = new BannerAds(context, value);
			}
		}

		if((AdsType.INTERSTITIAL&adType) == AdsType.INTERSTITIAL) {
			String key;
			if(AdsUitl.isTable(context))
				key = PAD_INTERSTITIAL_KEY;
			else
				key = PHONE_INTERSTITIAL_KEY;
			String value = AdsUitl.getMetaData(context,key);
			if(key != null&&!key.isEmpty()) {
				interstitialAds = new InterstitialAds(context, value);
			}
		}

		if((AdsType.CROSS & adType) == AdsType.CROSS) {
			String appId = AdsUitl.getMetaData(context,CROSSPROMOTION_APPID);
			String signature = AdsUitl.getMetaData(context,CROSSPROMOTION_SIGNARURE);
			if(appId != null&&signature != null && !appId.isEmpty() && !signature.isEmpty()) {
				crosspomoAds = new CrosspomoAds(context, appId, signature);
			}

		}

		if((AdsType.REWARD & adType) == AdsType.REWARD) {
			String appId = AdsUitl.getMetaData(context,REWARDED_APPID);
			String signature = AdsUitl.getMetaData(context,REWARDED_SIGNARURE);
			if(appId != null&&signature != null && !appId.isEmpty() && !signature.isEmpty()) {
				rewaredAds = new RewaredAds(context, appId, signature);
			}
		}
	}

	public void preLoadAllAds() {
		preLoadAds(AdsType.BANNER);
		preLoadAds(AdsType.REWARD);
		preLoadAds(AdsType.CROSS);
		preLoadAds(AdsType.INTERSTITIAL);
	}

	public void preLoadAds(int adType){
		switch (adType){
			case AdsType.BANNER:
				if(bannerAds != null)
					bannerAds.preload();
				break;
			case AdsType.REWARD:
				if(rewaredAds != null)
					rewaredAds.preload();
				break;
			case AdsType.CROSS:
				if(crosspomoAds != null)
					crosspomoAds.preload();
				break;
			case AdsType.INTERSTITIAL:
				if(interstitialAds != null)
					interstitialAds.preload();
		}
	}

	public boolean showAds(int adType) {
		boolean flag = false;
		switch (adType){
			case AdsType.BANNER:
				if(bannerAds != null)
					flag =bannerAds.show();
				break;
			case AdsType.REWARD:
				if(rewaredAds != null)
					flag =rewaredAds.show();
				break;
			case AdsType.CROSS:
				if(crosspomoAds != null)
					flag =crosspomoAds.show();
				break;
			case AdsType.INTERSTITIAL:
				if(interstitialAds != null)
					flag =interstitialAds.show();
		}
		return flag;
	}

	public void destory() {
		if(bannerAds != null)
			bannerAds.destroy();
		if(crosspomoAds != null)
			crosspomoAds.destroy();
		if(rewaredAds != null)
			rewaredAds.destroy();
		if(interstitialAds != null)
			interstitialAds.destroy();
	}

	public void rewmoveAds(int adType) {
		switch (adType) {
			case AdsType.BANNER:
				if (bannerAds != null)
					bannerAds.remove();
				break;
		}
	}

	public void setVisible(int adType, boolean visible) {
		switch(adType) {
			case AdsType.BANNER:
				if(bannerAds != null) {
					bannerAds.setVisible(visible);
				}
				break;
		}

	}

	public AdsPlatform getAds(int adsType) {
		switch (adsType) {
			case AdsType.BANNER:
				return bannerAds;
			case AdsType.CROSS:
				return crosspomoAds;
			case AdsType.INTERSTITIAL:
				return	 interstitialAds;
			case AdsType.REWARD:
				return  rewaredAds;
		}
		return null;
	}

public boolean isFullScreenShowing(){
return  FullScreenAds.isFullScreenAdsShowing();
}

	void setAutoShow(int adsTye, boolean bIsAuto){
		AdsPlatform ads = getAds(adsTye);
		if(ads != null)
			ads.setAutoShow(bIsAuto);
	}

	boolean isAutoShow(int adsTye){
		AdsPlatform ads = getAds(adsTye);
		if(ads != null)
			return ads.getIsAutoShow();
		else
			return false;
	}
	void remove(int adsTye){
		switch(adsTye) {
			case AdsType.BANNER:
				if(bannerAds != null) {
					bannerAds.remove();
				}
				break;
		}
	}

	boolean isPreloaded(int adsTye) {
		AdsPlatform ads = getAds(adsTye);
		if(ads != null)
			return ads.isLoaded();
		else
			return false;
	}
}
