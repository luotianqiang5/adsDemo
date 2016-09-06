package com.common.ads;


/**
 * Created by luotianqiang1 on 16/9/2.
 */
public interface AdsListener {

	void onLoadedSuccess(AdsPlatform var) ;

	void onLoadedFail(AdsPlatform var);

	void onAdsOpened(AdsPlatform var);

	void onAdsClosed(AdsPlatform var);

	void onAdsClicked(AdsPlatform var);

	void onOtherEvent(AdsPlatform var,String tag, Object... data);

}
