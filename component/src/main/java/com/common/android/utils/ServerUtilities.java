/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.common.android.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.common.android.gcm.GCMInstance;
import com.google.android.gcm.GCMRegistrar;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {
	private static final String TAG = "ServerUtilities";

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 * @deprecated Use {@link #registerGCM(Context,String)} instead
	 */
	public static boolean register(final Context context, final String regId) {
		return registerGCM(context, regId);
	}

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
	public static boolean registerGCM(final Context context, final String regId) {
		String serverUrl = Utils.getMetaData(context, "studioService") + "/android_push/register";
		try {
			post(context, serverUrl, regId);
			GCMRegistrar.setRegisteredOnServer(context, true);
			
			GCMRegistrar.setSenderId(context,GCMInstance.sendId,GCMInstance.targetClass);
			return true;
		} catch (IOException e) {
			Log.e("", "register GCM Error");

		}
		return false;
	}

	/**
	 * Unregister this account/device pair within the server.
	 * @deprecated Use {@link #unregisterGCM(Context,String)} instead
	 */
	public static void unregister(final Context context, final String regId) {
		unregisterGCM(context, regId);
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregisterGCM(final Context context, final String regId) {
		String serverUrl = Utils.getMetaData(context, "studioService") + "/android_push/unregister";
		try {
			post(context, serverUrl, regId);
			GCMRegistrar.setRegisteredOnServer(context, false);
		} catch (IOException e) {
			Log.e("", "unregistering device (regId = " + regId + ") Error!");
		}
	}
	
	public static Map<String,String> getAppInfo(Context context, String regId)
	{
		
		String pkName = context.getPackageName();
		String pkVersionCode = "";
		try {
			pkVersionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode + "";

		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("reg_id", regId);
		params.put("package_name", pkName);
		params.put("package_ver", pkVersionCode);
		return params;
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param params
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static void post(Context context, String endpoint, String regId) throws IOException {
		String pkName = context.getPackageName();
		String pkVersionCode = "";
		try {
			pkVersionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode + "";

		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("reg_id", regId);
		params.put("package_name", pkName);
		params.put("package_ver", pkVersionCode);
		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		}catch(Exception e)
		{
			Log.e(TAG, "register Error!");
		}finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	/**
	 * 以Post方式向服务器发送数据
	 * @param url
	 * @param args
	 * @return
	 */
	public static int doPost(String _url,Map<String,String>args)
	{
		int status=0;
		URL url;
		try {
			url = new URL(_url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + _url);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		if(args!=null)
		{
			Iterator<Entry<String, String>> iterator = args.entrySet().iterator();
			// constructs the POST body using the parameters
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}
		}
		String body = bodyBuilder.toString();
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		}catch(Exception e)
		{
			Log.e(TAG, "register Error!");
		}finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		return status;
	}
	
	/**
	 * 以get方式发送数据
	 * @param _url
	 * @param args
	 * @return
	 */
	public static int doGet(String _url)
	{
		int status=0;
		
		HttpURLConnection conn = null;
		try {
			URL url = new URL(_url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(2000);
			conn.setRequestProperty("accept", "text/xml;text/html");
			conn.connect();
			conn.getInputStream();
			status = conn.getResponseCode();
			if(status!=200)
			{
				Log.e("", "Conllection Error with:"+status);
			}
		} catch (Exception e) {
//						e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		return status;
	}
	
}
