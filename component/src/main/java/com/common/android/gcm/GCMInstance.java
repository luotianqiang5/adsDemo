package com.common.android.gcm;

import android.content.Context;

import com.common.android.utils.ServerUtilities;
import com.google.android.gcm.GCMRegistrar;

public class GCMInstance {
	private static String TAG = "GCMInstance";
	private Context context;
	public static String sendId;
	static String resisterId;
	public static Class<?> targetClass;
	private static GCMRegisterCallBack registerCallBack;

	/**
	 * 
	 * @param context
	 * @param _sendId
	 *            sendId.向PA/PM 索取此号
	 * @param _targetClass
	 *            当接收到消息后，用户点击消息需要启动的Activity。一般为一个App的Main Activity
	 * @param studio
	 *            发布应用的studio，用于生成后台服务器地址
	 * 
	 */
	public GCMInstance(final Context context, String _sendId, Class<?> _targetClass) {
		this.context = context;
		sendId = _sendId;
		targetClass = _targetClass;
		

	}

	/*
	 * 注册一个GCM服务 注：此处返回true仅表示当前设备的环境支持GCM.并不表示注册成功，如果环境支持，也可能注册失败，比如网络错误
	 */
	public boolean registerGCM() {
		if (checkDevice()) {
			resisterId = GCMRegistrar.getRegistrationId(context);
			if (resisterId.equals("")) {

				GCMRegistrar.register(context, GCMInstance.sendId);

			} else {
				onRegisterFinished(true,resisterId);
				if (!GCMRegistrar.isRegistered(context)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							boolean registered = ServerUtilities.registerGCM(context, resisterId);
							if (!registered) {
								GCMRegistrar.unregister(context);
							}

						}
					}).start();
				}
			}
			return true;
		} else {
			onRegisterFinished(false,null);
			return false;
		}
	}
	
	public boolean registerGCM(GCMRegisterCallBack callback)
	{
		registerCallBack=callback;
		return registerGCM();
	}
	
	/**
	 * 在GCM注册完成后调用，不需要手动调用
	 * @param isSuccessful
	 * @param registerId
	 */
	static void  onRegisterFinished(boolean isSuccessful,String registerId)
	{
		if(registerCallBack!=null)
			registerCallBack.onRegisterFinished(isSuccessful, registerId);
	}
	

	/*
	 * 取消一个GCM服务
	 */
	public void unregisterGCM() {
		GCMRegistrar.unregister(context);
	}
	Runnable registerTast = new Runnable() {

		@Override
		public void run() {

			boolean registered = ServerUtilities.registerGCM(context, resisterId);
			if (!registered) {
				GCMRegistrar.unregister(context);
			}
		}
	};

	/**
	 * 检查手机环境是否支持GCM,返回true为支持，false为不支持
	 * 
	 * @return
	 */
	private boolean checkDevice() {

		try {
			GCMRegistrar.checkDevice(context);
			GCMRegistrar.checkManifest(context);
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	public void onDestory() {
		GCMRegistrar.onDestroy(context);
	}

}
