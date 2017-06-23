package com.common.android.jni;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.common.android.LaunchActivity;
import com.common.android.PlatformCode;
import com.common.android.R;
import com.common.android.moregame.MarketMoreGame;
import com.common.android.newsblast.ErrorCode;
import com.common.android.newsblast.NewsBean;
import com.common.android.newsblast.NewsBlast;
import com.common.android.newsblast.NewsBlastListener;
import com.common.android.utils.Utils;


/**
 * 提供给JNI调用Android系统功能
 * 
 * @author Steven.Xc.Tian
 */
public class STSystemFunction {
	private final LaunchActivity stContext;

	private int platformCode;
	private static STSystemFunction mInstance;
	private static boolean isNewsShowing;
	

	/**
	 * 设置环境
	 * 
	 * @param context
	 */
	public static void setup(LaunchActivity context) {
		if (mInstance == null)
			mInstance = new STSystemFunction(context);
	}

	public static STSystemFunction getInstance() {
		return mInstance;
	}

	private STSystemFunction(LaunchActivity context) {
		isNewsShowing=false;
		stContext = context;
		platformCode = ((LaunchActivity) context).getPlatformCode();
		nativeInit();
	}

	public void destroy() {
		nativeDestroy();
		mInstance = null;
	}

	public void sendMailByIntent(String subject, String emailBody) {
		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		myIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		stContext.startActivity(Intent.createChooser(myIntent, stContext.getString(R.string.email_chooser)));
	}

	public void popAlertDialog(final String message) {
		((Activity) stContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					if (stContext != null && !stContext.isFinishing())
						new AlertDialog.Builder(stContext).setMessage(message)
								.setPositiveButton(R.string.com_facebook_dialogloginactivity_ok_button, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
								/* User clicked OK so do some stuff */
									}
								}).setCancelable(false).create().show();
				} catch (Exception e) {

				}

			}
		});
	}

	public void makeToast(final String message) {
		((Activity) stContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(stContext, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void cacheMoreGame() {

		//Chartboost.cacheMoreApps(CBLocation.LOCATION_DEFAULT);
	}

	public void showMoreGame() {
		MarketMoreGame.openMoreGame(stContext);
//		if(Chartboost.hasMoreApps(CBLocation.LOCATION_DEFAULT)){
//			Chartboost.showMoreApps(CBLocation.LOCATION_DEFAULT);
//		}else
//		{
//			((Activity) stContext).runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					Uri uri;
//					String uriPath = Utils.getMetaData(stContext,"MoreGamePage");
//					if(null != uriPath) {
//						uri = Uri.parse(uriPath);
//					} else {
//						uri = Uri.parse("https://play.google.com/store/apps/developer?id=Crazy+Camp+Media");
//					}
//					Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
//					try {
//						stContext.startActivity(myAppLinkToMarket);
//					} catch (ActivityNotFoundException e) {
//
//					}
//				}
//			});
//		}
//		Chartboost.cacheMoreApps(CBLocation.LOCATION_DEFAULT);
	}

	public String getSdCardPath() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return stContext.getFilesDir().getAbsolutePath();
		}
	}

	public void rating() {
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Uri uri;
				if (platformCode == PlatformCode.AMAZON) {
					uri = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + stContext.getPackageName() + "&showAll=1");
				} else {
					uri = Uri.parse("market://details?id=" + stContext.getPackageName());
				}

				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					stContext.startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					((Activity) stContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(stContext, "unable to find market app", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		});
	}

	public void go2MarketDetail(final String packagename) {
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Uri uri;
				if (platformCode == PlatformCode.AMAZON) {
					uri = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + packagename + "&showAll=1");
				} else {
					uri = Uri.parse("market://details?id=" + packagename);
				}

				Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					stContext.startActivity(myAppLinkToMarket);
				} catch (ActivityNotFoundException e) {
					((Activity) stContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(stContext, "unable to find market app", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		});
	}

	public void sendEmailAndAssetPic(final String subject, final String message, final String assetName) {
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				sendIntent.putExtra(Intent.EXTRA_TEXT, message + getPackageLink());
				sendIntent.setType("image/*");
				Uri picUri = Uri.parse("content://" + stContext.getPackageName() + ".AssetContentProvider/" + assetName);
				sendIntent.putExtra(Intent.EXTRA_STREAM, picUri);
				stContext.startActivity(Intent.createChooser(sendIntent, stContext.getString(R.string.email_chooser)));
			}
		});
	}

	public void sendEmailAndFilePic(final String subject, final String message, final String fileName) {
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				sendIntent.putExtra(Intent.EXTRA_TEXT, message + getPackageLink());
				sendIntent.setType("image/*");
				File copyFile = new File(fileName);
				sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(copyFile));
				stContext.startActivity(Intent.createChooser(sendIntent, stContext.getString(R.string.email_chooser)));
			}
		});
	}

	public void contactUs() {
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String contacr_msg = stContext.getString(R.string.contact_msg);
				String app_name = "App Name:" + stContext.getString(R.string.app_name) + "\n";
				String android_version = "Android Version:" + SystemInfo.getOSVersion() + "\n";
				String app_version = "App Version:" + SystemInfo.getAppVersion(stContext) + "\n";
				String device_model = "Device:" + SystemInfo.getModel() + "\n";
				contacr_msg += app_name + device_model + android_version + app_version;
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, stContext.getString(R.string.contact_subject));
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, contacr_msg);
				String[] contacts = { stContext.getString(R.string.contact_address) };
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, contacts);
				emailIntent.setType("plain/text");
				stContext.startActivity(Intent.createChooser(emailIntent, stContext.getString(R.string.email_chooser)));
			}
		});
	}

	public boolean checkNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) stContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return false;
		}
		return true;
	}

	private String getPackageLink() {
		String packageLink = null;
		if (platformCode == PlatformCode.AMAZON) {
			packageLink = "http://www.amazon.com/gp/mas/dl/android?p=" + stContext.getPackageName();
		} else if (platformCode == PlatformCode.GOOGLEPLAY) {
			packageLink = "play.google.com/store/apps/details?id=" + stContext.getPackageName();
		}

		//			String packageName = "\n" + mActivity.getString(R.string.app_name)
		//					+ ":" + packageLink;
		return packageLink;
	}

	public boolean isTabletAvailable() {
		return (stContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	//float densityScale;
	public float densityScale() {
		return (stContext.getResources().getDisplayMetrics().density);
	}

	public void newsBlast() {
		if(isNewsShowing)
			return;
			
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				NewsBlast news=new NewsBlast(stContext,platformCode);
				news.registerListener(new NewsBlastListener() {
					
					@Override
					public void onRedirectAndClose() {
						isNewsShowing=false;
					}
					
					@Override
					public void onMessage(NewsBean message) {
						isNewsShowing=true;
					}
					
					@Override
					public void onError(ErrorCode code) {
						isNewsShowing=false;
					}
					
					@Override
					public void onClose() {
						isNewsShowing=false;
					}
				});
				news.continueNews();
				news.doNewsBlast_always();
			}
		});
	}
	
	public void rateUs() {

		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((LaunchActivity) stContext).showRateUsDialog();
			}
		});

	}

	public void dismissRateUs() {

		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((LaunchActivity) stContext).closeRateUsDialog();
			}
		});
	}
	
	/**
	 * 
	 */
	public void endSession(){
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((LaunchActivity) stContext).endSession();
			}
		});
		
	}
	
	/**
	 * 扫描指定文件夹的文件，解决保存图谱按后，系统不能即使更新相册的问题
	 * @param dir
	 */
	public void refreshDCIM(String _file){
		
		final Uri data = Uri.parse("file://"+_file);
		((Activity) stContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				scanIntent.setData(data);
				stContext.sendBroadcast(scanIntent);
			}
		});
	}
	
	public int listAssetFiles(String path) {
		AssetManager am = stContext.getAssets();
		int ret = 0;
		try {
			ret = am.list(path).length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public boolean isRate()
	{
		return stContext.isRate();
	}

	/**
	 * 获取设备屏幕的旋转角度
	 * @return
	 */
	public int getScreenRotation() {
		int rotation=stContext.getWindowManager().getDefaultDisplay().getRotation();
		return rotation;
	}
	
	/**
	 * 发送自定义flurry event事件
	 * @param eventName
	 * 
	 */
	public void onFlurryEvent(String eventName)
	{
		
	}
	
	private native void nativeInit();

	private native void nativeDestroy();
}
