package com.common.android.moregame;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.common.ads.FullScreenAds;

/**
 * Created by luotianqiang on 17/6/21.
 */
public class JSCallJavaInterface {
    Context mContext;

    JSCallJavaInterface(Context c) {
        this.mContext = c;
    }

    @JavascriptInterface
    public void callExit() {
        FullScreenAds.setFullScreenAdsShowing(false);
        Log.i("JSCallJavaInterface", "JSCallJavaInterface.callExit");
        ((Activity)this.mContext).finish();
    }
}
