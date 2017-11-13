package com.common.android.moregame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.common.ads.FullScreenAds;
import com.common.android.R;
import com.common.android.jni.MoreGamesActivityForJNI;
import com.common.android.utils.Utils;

public class MarketMoreGame extends AppCompatActivity {

    private static String loadUrl = "https://play.google.com/store/apps/developer?id=Crazy+Camp+Media";
    private WebView mWebView;
    private Dialog mProgressDialog;
    public static void openMoreGame(final Context parent,String url){
        if(parent != null){
            loadUrl = url;
            FullScreenAds.setFullScreenAdsShowing(true);
            ((Activity)parent).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(parent, MarketMoreGame.class);
                    parent.startActivity(intent);
                }
            });

        }
    }

    public static void openMoreGame(final Context parent){
        if(parent != null){
           String loadUrl = Utils.getMetaData(parent, "severUrl") +"/moregames/?platform="+32+"&bundleId=";
            PackageInfo info = null;
            PackageManager pm = parent.getApplicationContext().getPackageManager();
            try {
                info = pm.getPackageInfo(parent.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {

            }

            if (info != null) {
                loadUrl = loadUrl + info.packageName;
            }
            openMoreGame(parent, loadUrl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mWebView = new WebView(this);
        this.mWebView .setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        this.mWebView.setScrollbarFadingEnabled(false);
        this.setContentView(this.mWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setCacheMode(-1);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        mWebView.loadUrl(loadUrl);
        mWebView.setWebViewClient(new VedioWebViewClient());
        this.mWebView.addJavascriptInterface(new JSCallJavaInterface(this), "JSCallJavaInterface");

    }

    protected void onPause() {
        super.onPause();
        if(this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
            this.dismissDialog(1);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mWebView.canGoBack())
                mWebView.goBack();
            else{
                FullScreenAds.setFullScreenAdsShowing(false);
                MarketMoreGame.this.finish();
            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case 1:
                this.mProgressDialog = showProgressDialog(this, this.getString(R.string.more_game_progress_dialog_waiting));
                return this.mProgressDialog;
            default:
                return super.onCreateDialog(id);
        }
    }


    private void showShutdownDlg(String msg) {
        if(this.mWebView != null) {
            this.mWebView.loadUrl("about:blank");
        }
        Activity activity = this;
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(msg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FullScreenAds.setFullScreenAdsShowing(false);
                    try {
                        MarketMoreGame.this.finish();
                    } catch (Exception e) {

                    }
                }
            });
            builder.create().show();
        } catch (Exception e) {

        }
    }

    private static Dialog showProgressDialog(Context context, String dialogMess) {
        Dialog customDialog = new Dialog(context, R.style.CustomDialogStyle);
        customDialog.requestWindowFeature(1);
        View loadingDialog = View.inflate(context, R.layout.jni_more_game_progressbar, (ViewGroup)null);
        TextView loadText = (TextView)loadingDialog.findViewById(R.id.load_text);
        loadText.setText(dialogMess);
        customDialog.setContentView(loadingDialog);
        return customDialog;
    }

    private class VedioWebViewClient extends WebViewClient {
        private VedioWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(MoreGamesActivityForJNI.DEBUG_INFO) {
                Log.i("MoreGamesActivityForJNI", "url: " + url);
            }

            if(url != null) {
                if(url.startsWith("http")) {
                    view.loadUrl(url);
                } else {
                    try {
                        view.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    } catch (ActivityNotFoundException var4) {
                        Log.e("MoreGamesActivityForJNI", "There are no clients installed.");
                        Toast.makeText(MarketMoreGame.this, "There are no clients installed", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            } else {
                return false;
            }
        }

        public void onPageFinished(WebView view, String url) {
            if(MarketMoreGame.this.mProgressDialog != null && MarketMoreGame.this.mProgressDialog.isShowing()) {
                MarketMoreGame.this.dismissDialog(1);
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(!MarketMoreGame.this.isFinishing()) {
                MarketMoreGame.this.showDialog(1);

            }

        }

        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            MarketMoreGame.this.showShutdownDlg("Error occured, please check network");
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            MarketMoreGame.this.showShutdownDlg("Error occured, please check network");
        }
    }
}
