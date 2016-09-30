package com.common.android.jni;

/**
 * @Author          liujian
 * @Version         1.0
 * @Date            2013-3-26
 * 
 * @Description: utility used to retrieve system info
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

public class SystemInfo {

	public static int getScreenWidth(final Context ctx) {
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight(final Context ctx) {
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static int getDensityDpi(final Context ctx) {
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		return dm.densityDpi;
	}

	public static float getDensity(final Context ctx) {
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		return dm.density;
	}

	public static String getScreenSize(final Context ctx) {
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		return new String(dm.widthPixels + "," + dm.heightPixels);
	}

	public static String getCookies(final Context ctx) {
		String ret = "";
		// ret += (Configuration.HEADER_LANGUAGE + "=" +
		// SystemInfo.getLanguage(ctx) + ",");
		// ret += (Configuration.HEADER_DEVICE_MODEL + "=" +
		// SystemInfo.getModel() + ",");
		// ret += (Configuration.HEADER_SCREEN_SIZE + "=" +
		// SystemInfo.getScreenSize(ctx) + ",");
		// ret += (Configuration.HEADER_PLATFORM + "=" + "Android");

		return ret;
	}

	public static String getLanguage(final Context ctx) {
		return ctx.getResources().getConfiguration().locale.getLanguage();
	}

	public static String getModel() {
		return Build.MODEL;
	}

	public static String getAppVersion(final Context ctx) {
		try {
			PackageManager manager = ctx.getPackageManager();
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getOSLevel() {
		return Build.VERSION.SDK_INT;
	}

	public static String getOSVersion() {
		return Build.VERSION.RELEASE;
	}

	public final static int SAFE_WEBVIEW_INTERCEPT_OS_LEVEL = 16;
	public final static String SAFE_WEBVIEW_INTERCEPT_OS_VERSION = "4.1";
	private static boolean mSafeWebViewIntercept = false;
	private static boolean mSafeWebViewInterceptInited = false;
	public static boolean isSafeWebViewIntercept() {
		if (!mSafeWebViewInterceptInited) {
			mSafeWebViewInterceptInited = true;
			mSafeWebViewIntercept = (Build.VERSION.SDK_INT >= SAFE_WEBVIEW_INTERCEPT_OS_LEVEL && Build.VERSION.RELEASE
					.compareToIgnoreCase(SAFE_WEBVIEW_INTERCEPT_OS_VERSION) >= 0);
		}

		return mSafeWebViewIntercept;
	}

	private static String mUserAgent = "";
	public static String getWebViewUserAgent() {
		return mUserAgent;
	}
	public static void setWebViewUserAgent(final String userAgent) {
		mUserAgent = userAgent;
	}
}