package com.common.ads;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Created by luotianqiang1 on 16/9/6.
 */
public class AdsUitl {

	public static boolean isTable(Context var){
		return (var.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static String getMetaData(Context var,String key){
		String data = null;
		try {
			ApplicationInfo e = var.getPackageManager().getApplicationInfo(var.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = e.metaData;
			Object o = bundle.get(key);
			if(o != null) {
				data = o.toString();
			}
		} catch (PackageManager.NameNotFoundException var6) {
			;
		}

		return data;
	}
}
