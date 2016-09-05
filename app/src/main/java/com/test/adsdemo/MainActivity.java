package com.test.adsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.common.ads.AdsPlatform;
import com.common.ads.RewaredAds;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements RewaredAds.RewaredAdsListener{

	final private String APP_ID = "app65e37f7ecffd4e939b";
	final private String ZONE_ID = "vz6492a0aa094044d081";

	private Button btn;

	RewaredAds  rewaredAds;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn = (Button)(findViewById(R.id.button));

		rewaredAds = new RewaredAds(this,APP_ID,ZONE_ID);
		rewaredAds.setListener(this);
		rewaredAds.preload();
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rewaredAds.show();
			}
		});
		if(rewaredAds.isLoaded())
			btn.setEnabled(true);
	}

	@Override
	public void onRewarded(String var, int var2, boolean var3) {
		showToast("onRewarded "+var+" "+var2+" "+var3);
	}

	@Override
	public void onLoadedSuccess(AdsPlatform var) {
		showToast("Reward load success ");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btn.setEnabled(true);
			}
		});

	}

	@Override
	public void onLoadedFail(AdsPlatform var) {
		showToast("Reward load fail");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btn.setEnabled(false);
			}
		});
	}

	@Override
	public void onAdsOpened(AdsPlatform var) {
		showToast("Reward open");
	}

	@Override
	public void onAdsClosed(AdsPlatform var) {
		showToast("Reward closed");
	}

	@Override
	public void onAdsClicked(AdsPlatform var) {

	}

	@Override
	public void onOtherEvent(AdsPlatform var, String tag, Objects... data) {

	}

	public void showToast(final String str){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
			}
		});
	}
}
