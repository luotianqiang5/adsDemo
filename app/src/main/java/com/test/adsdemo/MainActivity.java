package com.test.adsdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.chartboost.sdk.Chartboost;
import com.common.ads.AdsListener;
import com.common.ads.AdsManager;
import com.common.ads.AdsPlatform;
import com.common.ads.AdsType;
import com.common.android.LaunchActivity;


public class MainActivity extends LaunchActivity  {

	final private String APP_ID = "app65e37f7ecffd4e939b";
	final private String ZONE_ID = "vz6492a0aa094044d081";

	final private String charboostApp_id = "57bd010b04b01674f37fdc55";
	final private  String chartboostApp_Signature = "6d6412f15b41220709f6c2510057fcaf230a0c52";

	final private String adUiId = "ca-mb-app-pub-5545962412010602/5331530639";
	final private String banneradUiId = "ca-mb-app-pub-5545962412010602/6808238519";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AdsManager.getInstance(this).setup(AdsType.REWARD | AdsType.BANNER);
		AdsManager.getInstance(this).getAds(AdsType.REWARD).setListener(Adslis);
		AdsManager.getInstance(this).getAds(AdsType.BANNER).setListener(Adslis);

		this.findViewById(R.id.preload_banner).setOnClickListener(lis);
		this.findViewById(R.id.show_banne).setOnClickListener(lis);
		this.findViewById(R.id.preload_rewared).setOnClickListener(lis);
		this.findViewById(R.id.show_rewared).setOnClickListener(lis);

		this.findViewById(R.id.show_banne).setEnabled(false);
		this.findViewById(R.id.show_rewared).setEnabled(false);

	}

	AdsListener Adslis = new AdsListener() {
		@Override
		public void onLoadedSuccess(final  AdsPlatform var) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(var.getAdsType() == AdsType.BANNER)
						findViewById(R.id.show_banne).setEnabled(true);
					else if(var.getAdsType() == AdsType.REWARD)
						findViewById(R.id.show_rewared).setEnabled(true);
				}
			});

		}

		@Override
		public void onLoadedFail(AdsPlatform var) {
			if(var.getAdsType() == AdsType.BANNER)
				findViewById(R.id.show_banne).setEnabled(false);
			else if(var.getAdsType() == AdsType.REWARD)
				findViewById(R.id.show_rewared).setEnabled(false);
		}

		@Override
		public void onAdsOpened(AdsPlatform var) {

		}

		@Override
		public void onAdsClosed(AdsPlatform var) {

		}

		@Override
		public void onAdsClicked(AdsPlatform var) {

		}

		@Override
		public void onOtherEvent(AdsPlatform var, String tag, Object... data) {

		}
	};

	View.OnClickListener lis = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.preload_banner:
					//Uri uri = Uri.parse("market://details?id=com.crazycampmedia.android_forestdoctor");
					//Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
					//MainActivity.this.startActivity(myAppLinkToMarket);
					AdsManager.getInstance(MainActivity.this).preLoadAds(AdsType.BANNER);
					break;
				case R.id.show_banne:
					AdsManager.getInstance(MainActivity.this).showAds(AdsType.BANNER);
					break;
				case R.id.preload_rewared:
					AdsManager.getInstance(MainActivity.this).preLoadAds(AdsType.REWARD);
					break;
				case R.id.show_rewared:
					AdsManager.getInstance(MainActivity.this).showAds(AdsType.REWARD);
					break;
			}
		}
	};




	@Override
	public void onBackPressed() {
		if(!Chartboost.onBackPressed())
			super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public int getPlatformCode() {
		return 32;
	}

	@Override
	public boolean getDebugMode() {
		return false;
	}

	@Override
	public int getAnalyticsCode() {
		return 0;
	}

	@Override
	public boolean enableEvent() {
		return false;
	}
}
