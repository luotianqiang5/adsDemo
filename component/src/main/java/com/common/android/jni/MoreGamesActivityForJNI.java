package com.common.android.jni;

import com.common.android.PlatformCode;
import com.common.android.R;
import com.common.android.utils.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MoreGamesActivityForJNI extends Activity {
	private static final String TAG = "MoreGamesActivityForJNI";
	private static final int PROGRESS_ID = 1;
	private Dialog mProgressDialog;
	private ProgressBar title_progress;
	private WebView mWebView;

	/** 用来构造字符串的一个{@link java.lang.StringBuilder}实例 */
	protected static StringBuilder Cache_StringBuilder = new StringBuilder();

	public static int PLATFORM = PlatformCode.AMAZON;
	public static boolean DEBUG_INFO = true;

	// 按每个不同的Studio设置
	public static int ACTIVITY_LAYOUT_ID = -1;
	public static int WEBVIEW_ID = -1;
	public static int TITLE_PROGRESS_ID = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(ACTIVITY_LAYOUT_ID);

		mWebView = (WebView) findViewById(WEBVIEW_ID);
		mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		if (TITLE_PROGRESS_ID == -1)
			title_progress = null;
		else
			title_progress = (ProgressBar) findViewById(TITLE_PROGRESS_ID);

		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(PluginState.ON);
		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);

		mWebView.loadUrl(moreGames());
		mWebView.setWebViewClient(new VedioWebViewClient());
	}

	/**
	 * 清空字符串缓冲{@link #cache_StringBuilder}
	 */
	protected static void cleanStringCache() {
		Cache_StringBuilder.delete(0, Cache_StringBuilder.length());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mProgressDialog != null && mProgressDialog.isShowing())
			dismissDialog(PROGRESS_ID);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mWebView.canGoBack())
				mWebView.goBack();
			else{
				
				MoreGamesActivityForJNI.this.finish();
			}
				

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_ID:
			mProgressDialog = showProgressDialog(MoreGamesActivityForJNI.this,
					getString(R.string.more_game_progress_dialog_waiting));
			return mProgressDialog;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * @return 生成MoreGame页面所需要的链接，并直接转发到相应Acticity
	 */
	private String moreGames() {
		PackageInfo info = null;
		PackageManager pm = getApplicationContext().getPackageManager();
		try {
			info = pm.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (info == null)
			return null;

		cleanStringCache();
		Cache_StringBuilder.append(info.packageName);
		int last = Cache_StringBuilder.lastIndexOf(".");

		String appName = Cache_StringBuilder.substring(last + 1);
		String model = Build.MODEL.replace(" ", "_");

		cleanStringCache();

		Cache_StringBuilder.append(Utils.getMetaData(this, "studioService")).append("/more/").append(appName)
				.append("/").append(getResources().getConfiguration().locale.getLanguage()).append("/")
				.append(Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID)).append("/?model=")
				.append(model).append("&sysVer=").append(Build.VERSION.RELEASE).append("&bundleVer=")
				.append(info.versionName).append("&bundleID=").append(info.packageName).append("&platform=")
				.append(PLATFORM);

		if (DEBUG_INFO)
			Log.i(TAG, "index url: " + Cache_StringBuilder.toString());

		return Cache_StringBuilder.toString();
	}

	private class VedioWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (DEBUG_INFO)
				Log.i(TAG, "url: " + url);

			if (url != null) {
				if (url.startsWith("market://"))
					view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				else
					view.loadUrl(url);

				return true;
			}

			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				dismissDialog(PROGRESS_ID);
			}
			if (title_progress != null)
				title_progress.setVisibility(View.GONE);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (!isFinishing()) {
				showDialog(PROGRESS_ID);
				if (title_progress != null)
					title_progress.setVisibility(View.VISIBLE);
			}

		}

	}

	private static Dialog showProgressDialog(final Context context, String dialogMess) {
		Dialog customDialog = new Dialog(context, R.style.CustomDialogStyle);
		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View loadingDialog = View.inflate(context, R.layout.jni_more_game_progressbar, null);

		TextView loadText = (TextView) loadingDialog.findViewById(R.id.load_text);
		loadText.setText(dialogMess);
		customDialog.setContentView(loadingDialog);
		return customDialog;
	}

}
