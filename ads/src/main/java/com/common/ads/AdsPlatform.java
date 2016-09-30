package com.common.ads;

import android.app.Activity;


/**
 * Created by luotianqiang1 on 16/9/2.
 */
public abstract class AdsPlatform {
	protected Activity contextActivry;
	protected AdsListener listener;
	protected boolean isAutoShow;
	protected boolean isLoad;
	public AdsPlatform(Activity var) {
		contextActivry = var;
	}

	public abstract void preload();

	public abstract boolean show();

	public  boolean isLoaded(){
		return isLoad;
	}

	public abstract void destroy();

	public abstract int getAdsType();

	public void setListener(AdsListener listener) {
		this.listener = listener;
	}

	public AdsListener getListener() {
		return listener;
	}

	public void setAutoShow(boolean flag) {
		isAutoShow = flag;
	}
	public boolean getIsAutoShow(){return isAutoShow;}

}
