package com.common.ads;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;



/**
 * Created by luotianqiang1 on 16/9/6.
 */
public class BannerAds extends AdsPlatform implements Application.ActivityLifecycleCallbacks{
	String  adUnitId;
	private AdView 	admobView;
	protected ViewGroup adViewContainer;
	protected RelativeLayout adViewPanel;
	protected FrameLayout.LayoutParams adViewLayoutParams;
	protected boolean isActive;
    protected boolean isLoad;

	private AdListener admobAdListener = new AdListener(){
		@Override
		public void onAdClosed() {
			super.onAdClosed();
			if(listener != null)
				listener.onAdsClosed(BannerAds.this);
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			super.onAdFailedToLoad(errorCode);
			isLoad = false;
			if(listener != null)
				listener.onLoadedFail(BannerAds.this);
		}

		@Override
		public void onAdLeftApplication() {
			super.onAdLeftApplication();
		}

		@Override
		public void onAdOpened() {
			super.onAdOpened();
			if(listener != null)
				listener.onAdsOpened(BannerAds.this);
		}

		@Override
		public void onAdLoaded() {
			super.onAdLoaded();
			isLoad = true;
			if(isAutoShow)
				show();
			if(listener != null)
				listener.onLoadedSuccess(BannerAds.this);
		}
	};

	private void initBanner () {
		admobView = new AdView(contextActivry);
		admobView.setAdUnitId(adUnitId);
		admobView.setAdSize(AdSize.SMART_BANNER);
		admobView.setAdListener(admobAdListener);
		adViewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM);
		if(adViewPanel == null)
			adViewPanel = new RelativeLayout(contextActivry);
		adViewPanel.setBackgroundColor(Color.WHITE);
		adViewPanel.getBackground().setAlpha(128);
		adViewPanel.setVisibility(View.INVISIBLE);

	}

	public BannerAds(Activity var1,String adUnitId){
		super((var1));
		this.adUnitId = adUnitId;
		contextActivry.getApplication().registerActivityLifecycleCallbacks(this);
		adViewContainer = (ViewGroup)contextActivry.findViewById(android.R.id.content);
		initBanner();
	}
	@Override
	public void preload() {
		synchronized (this){
			contextActivry.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(admobView == null)
						initBanner();
					AdRequest adRequest = (new AdRequest.Builder()).build();

					try {
						admobView.loadAd(adRequest);

					}catch (Exception var) {
						if(listener != null)
							listener.onLoadedFail(BannerAds.this);
					}
				}
			});
		}
	}

	@Override
	public boolean show() {
		isActive = true;
		synchronized (this) {
			if(admobView != null &&admobView.getParent() == null && contextActivry != null) {
				contextActivry.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(adViewPanel != null) {
							adViewPanel.addView(admobView);
							adViewContainer.addView(adViewPanel);
							adViewPanel.setLayoutParams(adViewLayoutParams);
							if(isLoaded())
								adViewPanel.setVisibility(View.VISIBLE);
						}
					}
				});

				if(listener != null)
					listener.onAdsOpened(BannerAds.this);
			}else if(admobView != null && contextActivry != null){
				contextActivry.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(adViewPanel != null)
							adViewPanel.setVisibility(View.VISIBLE);
					}
				});
			}
		}
		return true;
	}

	@Override
	public boolean isLoaded() {
		return isLoad;
	}

	@Override
	public void destroy() {
		isActive = false;
		remove();
		if(admobView != null)
			admobView.destroy();
	}


	public void setVisible(boolean bVisible) {
		this.isActive = bVisible;

		synchronized(this) {
			final int visibility = bVisible?View.VISIBLE:View.INVISIBLE;
			if(this.admobView != null && contextActivry!= null) {
				contextActivry.runOnUiThread(new Runnable() {
					public void run() {
						if (adViewPanel != null) {
							adViewPanel.setVisibility(visibility);
						}

					}
				});
			}

		}
	}

	public void setLayout(LayoutParams layout) {
		if(layout != null) {
			this.adViewLayoutParams = layout;
			this.adViewLayoutParams.width = LayoutParams.MATCH_PARENT;
			contextActivry.runOnUiThread(new Runnable() {
				public void run() {
					if (adViewPanel != null) {
						adViewPanel.setLayoutParams(adViewLayoutParams);
					}

				}
			});
		}

	}

	public View getAdView() {
		return this.adViewPanel;
	}

	public void remove() {

		synchronized(this) {
			if(this.admobView != null && this.admobView.getParent() != null && contextActivry != null) {
				contextActivry.runOnUiThread(new Runnable() {
					public void run() {
						if(adViewPanel != null) {
							adViewPanel.removeView(admobView);
							if(adViewPanel.getParent() != null) {
								adViewContainer.removeView(adViewPanel);
							}
						}

					}
				});
				if(listener != null) {
					listener.onAdsClosed(this);
				}
			}

		}
	}

	@Override
	public int getAdsType() {
		return AdsType.BANNER;
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
		//if(admobView != null)
		//	admobView.resume();
	}

	@Override
	public void onActivityPaused(Activity activity) {
		//if(admobView != null)
		//	admobView.pause();
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
