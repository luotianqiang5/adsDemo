package com.common.ads;

import android.app.Activity;


/**
 * Created by luotianqiang1 on 16/9/2.
 */
public abstract class AdsPlatform {
	protected Activity contextActivry;
	protected AdsListener listener;
	protected boolean isAutoShow;

	public AdsPlatform(Activity var) {
		contextActivry = var;
	}

	public abstract void preload();

	public abstract boolean show();

	public abstract boolean isLoaded();

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

}
