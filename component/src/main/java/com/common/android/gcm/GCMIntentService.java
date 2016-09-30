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
package com.common.android.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.common.android.utils.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() throws Exception {
		super(GCMInstance.sendId);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		ServerUtilities.registerGCM(context, registrationId);
		GCMInstance.onRegisterFinished(true, registrationId);
	}

	@Override
	protected void onRegisteredFailed() {
		GCMInstance.onRegisterFinished(false, null);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregisterGCM(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// title message status icon date

		generateNotification(context, intent);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
			Log.i(TAG, "Received deleted messages notification");
		// notifies user
	}

	@Override
	public void onError(Context context, String errorId) {
			Log.e(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
			Log.e(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private void generateNotification(Context context, Intent intent) {

//		String pkName = context.getPackageName();
//		int icon = 0;
//		try {
//			icon = context.getPackageManager().getPackageInfo(pkName, 0).applicationInfo.icon;
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		long when = System.currentTimeMillis();
//		String message = intent.getStringExtra("message");
//		String title = intent.getStringExtra("title");
//		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notification = new Notification(icon, title, when);
//		// 设置通知铃声为系统默认铃声
//		notification.defaults |= Notification.DEFAULT_SOUND;
//		// LED显示为系统默认颜色
//		notification.defaults |= Notification.DEFAULT_LIGHTS;
//		Intent notificationIntent = new Intent(context, GCMInstance.targetClass);
//		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//
//
//		//notification.setLatestEventInfo(context, title, message, pIntent);
//		// 设置点击后取消通知
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		notificationManager.notify(0, notification);

	}

}
