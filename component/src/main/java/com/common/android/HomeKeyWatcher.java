package com.common.android;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.List;

/**
 * 监听设备的Home键点击情况 注意，如果当前APP在后台运行，HomeKeyTouchListener不会接收监听事件
 * 
 * @author Haven
 *
 */
public class HomeKeyWatcher {
	private String tag = "HomeKeyWatcher";
	private static HomeKeyWatcher instance;
	private LaunchActivity context;
	private HomeKeyTouchListener listener;
	private KeyTouchRecevier keyTouchRecevier;

	public interface HomeKeyTouchListener {
		public void onHomeKeyClick();

		public void onHomeKeyLongTouch();

	}

	private HomeKeyWatcher(LaunchActivity context) {
		this.context = context;
	}

	public static HomeKeyWatcher getInstance(LaunchActivity context) {
		if (instance == null)
			instance = new HomeKeyWatcher(context);
		return instance;
	}

	public void registerListener(HomeKeyTouchListener listener) {
		this.listener = listener;

	}

	/**
	 * 注册Home键监听器
	 */
	public void startWathch() {
		if (keyTouchRecevier == null) {
			keyTouchRecevier = new KeyTouchRecevier();

		}
		if (context != null && keyTouchRecevier != null) {
			IntentFilter filter = new IntentFilter(
					Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			filter.setPriority(Integer.MAX_VALUE); 
			context.registerReceiver(keyTouchRecevier, filter);
		} else {
			Log.e(tag, "context or keyTouchRecevier is null");
		}
	}

	/**
	 * 移除Home键监听器
	 */
	public void stopWathch() {
		if (keyTouchRecevier != null && context != null) {
			context.unregisterReceiver(keyTouchRecevier);
		} else {
			Log.e(tag, "context or keyTouchRecevier is null");
		}
	}

	/**
	 * 判断的当前App是否在前台运行
	 * 
	 * @return
	 */
	private boolean isTopApp() {
		String topPackageName = null;
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(android.content.Context.ACTIVITY_SERVICE));
		List<RunningTaskInfo> topTask= activityManager.getRunningTasks(1);
		if (topTask != null) {
			ComponentName f = topTask.get(0).topActivity;
			topPackageName = f.getPackageName();
		}
		
		String packageName = context.getPackageName();
		if(topPackageName != null&& packageName !=null && topPackageName.equalsIgnoreCase(packageName))
		{
			return true;
		}
		return false;
	}

	private class KeyTouchRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().endsWith(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				
				String reason = intent.getStringExtra("reason");
				if (reason != null) {

					
						if (reason.equals("homekey")) {
							// 短按home键

							if (listener != null) 
								listener.onHomeKeyClick();
						} else if (reason.equals("recentapps")) {
							// 长按home键
							if (listener != null) 
								listener.onHomeKeyLongTouch();
						}
				}
			}

		}
	}
}
