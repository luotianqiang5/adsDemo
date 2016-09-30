package com.common.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * [新]用户生成设备的唯一识别码（UDID） 生成规则是： 获取设备的Wifi地址和Android ID，组合成一个字符串，取SHA1码
 * 则获取wifi地址。由于部分设备可能没有wifi
 * 这种情况就获取系统的Android_id.因为Android_id在刷机后会变化，所以一般情况下不使用，这里只作为最后的备选方案
 * 正常情况下，Android设备都会包含SIM卡或者wifi二者之一
 * 
 * @author liuhailong
 * 
 */
public class UDID {
	private static final String TAG = "UDID";

	

	public static String getUDID(Context context) {
		String str_udid=getWifiMAC(context)+getAndroidId(context);
		return SHA1(str_udid);
		//老的UDID生成方法，有bug
		/*String udid = getMEID(context);
		if (udid == null) {
			udid = getWifiMAC(context);
		}
		if (udid == null) {
			udid = getAndroidId(context);
		}
		if (udid != null) {
			return SHA1(udid);
		} else {
			return null;
		}
*/
	}

	public static String getMEID(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getWifiMAC(Context context) {
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			macAddress = info.getMacAddress();
		}
		return macAddress;
	}

	public static String getAndroidId(Context context) {

		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	/*
	 * 获取当前app的session id，此ID要保证唯一性，由bundle id+当前系统时间+UUID组合后取SHA-1
	 * 
	 */
	public static String getSessionId(Context context,String bundleId){
		String str_session=bundleId+System.currentTimeMillis()+getUDID(context);
		return SHA1(str_session);
	}

	/**
	 * 对字符串进行sha1加密
	 * 
	 * @param str
	 * @return
	 */
	public static String SHA1(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			return bytes2Hex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

}
