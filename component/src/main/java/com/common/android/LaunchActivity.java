package com.common.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.common.android.gcm.GCMInstance;
import com.common.android.iap_amazon.Purchase;
import com.common.android.iap_googleplay.IapHelper;
import com.common.android.iap_googleplay.PurchaseDatabase;
import com.common.android.jni.MoreGamesActivityForJNI;
import com.common.android.jni.STSystemFunction;
import com.common.android.permission.PermissionHelper;
import com.common.android.permission.PermissionJNI;
import com.common.android.utils.Utils;


public abstract class LaunchActivity extends AppCompatActivity {
	public static final String TAG = "LaunchActivity";
	public static final String ACTION_INTENT_RATEUS = "com.android.porting.common.rateus";
	private static final int THREADINTERVAL = 5;
	private static final String SP_FLURRY = "sharedPre_flurry";
	protected static final String STR_FLURRT_KEY = "Flurry_Key";
	protected static final String STR_ISFIRSTTIME = "isFirstTime";
	protected static final String STR_ISRATE = "isRate";
	protected static final String STR_INSTALLATIONTIME = "installationTime";
	protected static final String STR_LAUNCHSESSIONS = "launchSessions";
	protected static final String STR_DAY1 = "day1";
	protected static final String STR_DAY2 = "day2";
	protected static final String STR_DAY3 = "day3";
	protected static final String STR_DAY7 = "day7";
	protected static final String STR_DAY14 = "day14";
	protected static final String STR_DAY21 = "day21";
	public static final String STR_SESSIONS_PLAYED = "Sessions Played";
	protected static final String STR_DAY28 = "day28";
	public static final String STR_SNEDERID = "GCM_SenderId";
	public static final String STR_NEWSBLAST_SERVICE = "NewsBlastService";
	public static final String ACTION_LIFR_CIRCLE_FILTER = "com.ssc.broadcast.life_cricle";
	private String flurryKey;
	private SharedPreferences sharedPre;
	/**
	 * 程序是否是第一次运行：从用户安装之后算起，如果APP被手动清理数据，会重新判断
	 */
	protected boolean isFirstTimeRunning;
	/**
	 * 用户从安装到现在，总的启动次数，从用户安装之后算起，如果APP被手动清理数据，会重新计数
	 */
	protected int launchSessions;

	private long startTime;
	private int rateUsTime = 120;
	private boolean isRate;
	private boolean isRunning;
	private AlertDialog rateDialog;
	private int paltformCode;
	private Handler _handler = new Handler();
	private GCMInstance gcm;

	/**
	 * 受Google Play服务器管理的商品类型
	 */
	public static final String SKU_TYPE_MANAGE = "Managed";
	/**
	 * 不受Google Play服务器管理的商品类型，即消耗品
	 */
	public static final String SKU_TYPE_UNMANAGE = "Unmanaged";

	protected IapHelper mHelper;
	/**
	 * 用于保存正在进行交易的sku及其类型，如果是Unmanaged类型的，在交易成功后需要 进行consume
	 */
	private Map<String, String> cacheSkus;
	private OnPurchaseListener purchaseListener;
	private OnConsumeListener consumeListener;
	private OnQueryListener queryListener;
	private OnSetupListener setupListener;
	public static Activity actInstance;
	private boolean setupPrepared;
	private boolean mBNativeInited;
	private boolean purchaseNative, consumeNative, queryNative;
	private Purchase amazonPurchase;
	private boolean isDebug;
	private long rateusOpenTime;
	private String lastPurchaseKey;
	private Runnable rateUsRunnable = new Runnable() {
		@Override
		public void run() {
			int _runTime = getRunTime();
			if (_runTime >= rateUsTime && !isRate) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showRateUsDialog();
					}
				});
			}
			if (!isRate && _handler != null)
				_handler.postDelayed(this, THREADINTERVAL * 1000);
		}
	};

	public enum IAPFlurryCode {
		RESULT_OK, RESULT_SERVICE_UNAVAILABLE, RESULT_NETWORKERROR, RESULT_USER_CANCELED, RESULT_ERROR;

		// Converts from an ordinal value to the ResponseCode
		public static IAPFlurryCode valueOf(int index) {
			IAPFlurryCode[] values = IAPFlurryCode.values();
			if (index < 0 || index >= values.length) {
				return RESULT_ERROR;
			}
			return values[index];
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		paltformCode = getPlatformCode();
		sharedPre = getSharedPreferences(SP_FLURRY, MODE_PRIVATE);
		isFirstTimeRunning = sharedPre.getBoolean(STR_ISFIRSTTIME, true);
		isRate = isRate();
		startTime = System.currentTimeMillis();
		actInstance = this;
		setupPrepared = false;
		cacheSkus = new HashMap<String, String>();
		
//		Analytics.getInstance(this).setup(getPlatformCode());
//		Analytics.getInstance(this).setDebug(getDebugMode());
		if (isFirstTimeRunning) {
			sharedPre.edit().putBoolean(STR_ISFIRSTTIME, false).commit();
		}
		if (paltformCode == PlatformCode.GOOGLEPLAY) {
			init_IAP_GP();
		} else {
			init_IAP_Amazon();
		}
		init_RateUs();
		init_GCM();

		onEvent_day();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isRunning = true;

		//HomeKeyWatcher.getInstance(this).startWathch();
		Intent intent = new Intent(ACTION_LIFR_CIRCLE_FILTER);
		intent.putExtra("life_circle", "onResume");
		sendBroadcast(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		isRunning = false;

		Intent intent = new Intent(ACTION_LIFR_CIRCLE_FILTER);
		intent.putExtra("life_circle", "onPause");
		sendBroadcast(intent);
		//HomeKeyWatcher.getInstance(this).stopWathch();
	}


	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(ACTION_LIFR_CIRCLE_FILTER);
		intent.putExtra("life_circle", "onStart");
		sendBroadcast(intent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Intent intent = new Intent(ACTION_LIFR_CIRCLE_FILTER);
		intent.putExtra("life_circle", "onStop");
		sendBroadcast(intent);
	}
	/**
	 * 获取当前APP的PlatformCode
	 * 
	 * @return
	 */
	public abstract int getPlatformCode();

	public abstract boolean getDebugMode();

	/**
	 * 获取当前APP需要初始化的统计类型 
	 * 
	 * @return
	 */
	public abstract int getAnalyticsCode();
	/**
	 * IAP
	 * Library中默认是开启了FlurryEvent的，如果项目对IAP的Event做了单独处理，可以调用此方法关闭Library中的Event
	 * 
	 * @param enable
	 *            true:开启IAP的event。false：关闭IAP的event
	 */
	public abstract boolean enableEvent();



	private void init_IAP_GP() {
		mHelper = new IapHelper(this);
		mHelper.enableDebugLogging(getDebugMode());
		mBNativeInited = false;
	}

	private void init_IAP_Amazon() {
		amazonPurchase = Purchase.getInstance(this, getDebugMode());
	}

	/**
	 * 初始化 GCM
	 */
	protected void init_GCM() {
		String senderId = Utils.getMetaData(this, STR_SNEDERID);
		if (senderId != null && !"".equals(senderId)) {
			gcm = new GCMInstance(this, senderId, this.getClass());
			gcm.registerGCM();
		} else
			Log.w(TAG, "GCM  sender id is null");

	}

	protected void init_ADM() {

	}

	protected void init_RateUs() {
//		if (!isRate && Utils.checkNetwork(this)) {
//			_handler.postDelayed(rateUsRunnable, THREADINTERVAL * 1000);
//		}
	}

	public void setPlatformCode(int PlatformCode) {
		this.paltformCode = PlatformCode;
	}

	/**
	 * 关闭正在显示的Rate Us Dialog
	 */
	public void closeRateUsDialog() {
		if (rateDialog != null && rateDialog.isShowing()) {
			rateDialog.dismiss();
			rateDialog = null;
		}
	}

	public void showRateUsDialog() {
		Intent intent = new Intent(ACTION_INTENT_RATEUS);
		sendBroadcast(intent);

		if (!isRunning
				|| (rateDialog != null && rateDialog.isShowing()))
			return;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.str_rate_title);
		builder.setMessage(R.string.str_rate_content);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.str_rate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				isRate = true;
				sharedPre.edit().putBoolean(STR_ISRATE, isRate).commit();
				String uri = "https://play.google.com/store/apps/details?id="
						+ getPackageName();
				if (paltformCode == PlatformCode.GOOGLEPLAY) {
					uri = "market://details?id=" + getPackageName();
				} else if (paltformCode == PlatformCode.AMAZON) {
					uri = "https://www.amazon.com/gp/mas/dl/android?p="
							+ getPackageName();
				}
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(uri));
				startActivity(intent);

				

			}
		});
		builder.setNegativeButton(R.string.str_later, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
//				isRate = true;

			}
		});
		rateDialog = builder.create();
		rateDialog.show();
		rateusOpenTime = System.currentTimeMillis();

	}

	/**
	 * 在游戏运行多久后开始弹出rate us dialog，默认时间为120秒
	 * 
	 * @param time
	 *            单位：秒
	 */
	public void setRateUsTime(int time) {
		rateUsTime = time;
	}

	/**
	 * 用户是否已经Rate
	 * 
	 * @return
	 */
	public boolean isRate() {
		return sharedPre.getBoolean(STR_ISRATE, false);
	}

	/**
	 * 获取当前Activity从启动到现在的运行时间 单位：秒
	 * 
	 * @return
	 */
	public int getRunTime() {
		long now = System.currentTimeMillis();
		return (int) ((now - startTime) / 1000);
	}

	/**
	 * 获取App的安装时间
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public long getInstallationTime() {
		int version = android.os.Build.VERSION.SDK_INT;
		long firstInstallTime = 0l;
		try {
			PackageManager pm = getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(),
					0);
			if (version <= 8)// 系统版本低于2.3
			{
				// 系统版本低于2.3的。没有firstInstallTime方法，所以在取程序的安装时间，就用程序的sharedPre中保存的STR_INSTALLATIONTIME值
				if (isFirstTimeRunning) {
					firstInstallTime = System.currentTimeMillis();
					sharedPre.edit()
							.putLong(STR_INSTALLATIONTIME, firstInstallTime)
							.commit();
				} else {
					firstInstallTime = sharedPre.getLong(STR_INSTALLATIONTIME,
							System.currentTimeMillis());
				}
			} else {
				firstInstallTime = packageInfo.firstInstallTime;// 应用第一次安装的时间
				// Log.i("",""+DateUtils.formatDateTime(this, firstInstallTime,
				// DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return firstInstallTime;
	}

	/**
	 * 启动一个FlurrySession,一般这个方法不需要手动调用
	 * debug模式下，数据发送方式会以普通http发送
	 * 在正式版本中，会自动切换为https方式
	 */
//	protected void onStartSession() {
//		this.flurryKey = Utils.getMetaData(this, STR_FLURRT_KEY);
//		if(getDebugMode()){
//			Log.i(TAG, "========flurry key======>>"+flurryKey);
//		}
//		
//		if (flurryKey != null && !"".equals(flurryKey)) {
//			FlurryAgent.init(this, flurryKey);
//			FlurryAgent.onStartSession(this);
//			if (enableEvent())
//				onEvent_day();
//		}
//	}

	/**
	 * Flurry Event :记录用户从安装后的第一天到第七天的启动状况
	 */
	protected void onEvent_day() {
		long nowTime = System.currentTimeMillis();
		long installation = getInstallationTime();

		long interval = nowTime - installation;

		float interval_hours = interval / 1000f / 60f / 60f;

		launchSessions = sharedPre.getInt(STR_LAUNCHSESSIONS, 0);
		launchSessions += 1;
		sharedPre.edit().putInt(STR_LAUNCHSESSIONS, launchSessions).commit();

		if (interval_hours >= 24 && interval_hours < 48
				&& !sharedPre.getBoolean(STR_DAY1, false))// Day1
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);


		} else if (interval_hours >= 48 && interval_hours < 72
				&& !sharedPre.getBoolean(STR_DAY2, false))// Day2
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);

			sharedPre.edit().putBoolean(STR_DAY2, true).commit();
		} else if (interval_hours >= 72 && interval_hours < 96
				&& !sharedPre.getBoolean(STR_DAY3, false))// Day3
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);

			sharedPre.edit().putBoolean(STR_DAY3, true).commit();
		} else if (interval_hours >= 168 && interval_hours < 192
				&& !sharedPre.getBoolean(STR_DAY7, false))// Day7
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);

			sharedPre.edit().putBoolean(STR_DAY7, true).commit();
		} else if (interval_hours >= 336 && interval_hours < 360
				&& !sharedPre.getBoolean(STR_DAY14, false))// Day14
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);

			sharedPre.edit().putBoolean(STR_DAY14, true).commit();
		} else if (interval_hours >= 504 && interval_hours < 528
				&& !sharedPre.getBoolean(STR_DAY21, false))// Day21
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);

			sharedPre.edit().putBoolean(STR_DAY21, true).commit();
		} else if (interval_hours >= 672 && interval_hours < 696
				&& !sharedPre.getBoolean(STR_DAY28, false))// Day28
		{
			HashMap<String, String> attr = new HashMap<String, String>();
			attr.put(STR_SESSIONS_PLAYED, "" + launchSessions);

			sharedPre.edit().putBoolean(STR_DAY28, true).commit();
		}

	}

	@Override
	protected void onDestroy() {
		endSession();
		
		Intent intent = new Intent(ACTION_LIFR_CIRCLE_FILTER);
		intent.putExtra("life_circle", "onDestroy");
		sendBroadcast(intent);
		
		isRate = true;
		if (gcm != null)
			gcm.onDestory();
		gcm = null;
		if (mBNativeInited)
			nativeFinalize();

		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;

		if (amazonPurchase != null)
			amazonPurchase.destory();
		amazonPurchase = null;
		
		super.onDestroy();
	}

	/**
	 * 如果不是通过finish或者系统正常退出方式退出APP，在退出之前需要手动调用此方法
	 * 
	 */
	public void endSession(){

		if(getDebugMode()){
			
		}
	}



	public void onStartSetupBilling() {
		if (getPlatformCode() == PlatformCode.GOOGLEPLAY)
			mHelper.startSetup(_setupListener);
		else {
			amazonPurchase = Purchase.getInstance(this, getDebugMode());
			amazonPurchase.onStartSetupBilling(_setupListener);
		}
	}

	public void setSetupBillingListener(OnSetupListener listener) {
		this.setupListener = listener;
	}

	public void setupNativeEnvironment() {
		mBNativeInited = true;
		nativeInit();

		// 当确定使用JNI模式时候，同时初始STSystemFunction类和MoreGames Activity相关参数

		STSystemFunction.setup(this);
		MoreGamesActivityForJNI.PLATFORM = getPlatformCode();
		MoreGamesActivityForJNI.DEBUG_INFO = getDebugMode();
		PermissionJNI.setup(this);
	}

	/**
	 * 设置IAP的Debug模式，在debug模式下，会在后台答应IAP交易的详细信息，方便调试
	 * 
	 * @param isDebug
	 */
	public void setDebugMode(boolean isDebug) {
		if (mHelper != null)
			mHelper.enableDebugLogging(isDebug);

	}

	public static Object getInstance() {
		return actInstance;
	}

	/**
	 * 
	 * 购买单件商品
	 * 
	 * @param sku
	 *            所要购买的商品ID
	 * @param skuType
	 *            商品类型：Managed 或者 Unmanaged
	 * @param purchaseListener
	 *            购买结果的对调接口
	 */
	public void onPurchase(String sku, String skuType,
			OnPurchaseListener purchaseListener) {
		String extraStr = android.provider.Settings.Secure.ANDROID_ID;
		lastPurchaseKey=sku;
		cacheSkus.put(sku, skuType);
		this.purchaseListener = purchaseListener;
		if (!setupPrepared) {
			if (purchaseListener != null) {
				purchaseListener
						.onIabPurchaseFinished(
								new IapResult(
										ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY,
										IapHelper
												.getResponseDesc(ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY)),
								null);
			}
			if (purchaseNative) {
				purchaseFailed(ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY);
				purchaseNative = false;
			}
			return;
		}

		if (Utils.checkNetwork(this)) {
			if (getPlatformCode() == PlatformCode.GOOGLEPLAY) {
				mHelper.launchPurchaseFlow(this, sku, 1001, finishedListener,
						extraStr);
			} else if (getPlatformCode() == PlatformCode.AMAZON
					&& amazonPurchase != null) {
				PurchaseBean p = new PurchaseBean(sku);
				p.mItemType = skuType;
				amazonPurchase.singlePurchase(p, finishedListener);
			}
		} else {
			if (purchaseListener != null) {
				purchaseListener
						.onIabPurchaseFinished(
								new IapResult(
										ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,
										IapHelper
												.getResponseDesc(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)),
								null);
			}
			if (purchaseNative) {
				purchaseFailed(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR);
				purchaseNative = false;
			}
			
			if (enableEvent()) {
				HashMap<String, String> attr = new HashMap<String, String>();
				attr.put("Purchase Result",
						sku + "," + IAPFlurryCode.RESULT_NETWORKERROR.toString());

			}
//			Analytics.getInstance(LaunchActivity.this).doAnalytics(AnalyticsEvent.EVENT_IAP, "\"" + sku + ","
//					+ IAPFlurryCode.RESULT_NETWORKERROR.toString() + "\"");
			
		}
		
		
	}

	/**
	 * 提供给C++接口调用购买接口的接口,功能等同于{@link #onPurchase}
	 * 
	 * @param sku
	 * @param skuType
	 * @param callback
	 */
	public void onPurchase_Native(String sku, String skuType) {
		purchaseNative = true;
		lastPurchaseKey=sku;
		onPurchase(sku, skuType, null);

	}

	/**
	 * 提供给C++ 调用的查询接口，功能等同于功能等同于{@link #onQuery}
	 * 
	 * @param isLocal
	 * @param callback
	 */
	public void onQuery_Native(boolean isLocal) {
		queryNative = true;
		onQuery(isLocal, null);

	}

	private native void nativeInit();

	private native void nativeFinalize();

	public native void querySuccess(String[] inv);

	public native void queryFailed(int result);

	public native void purchaseFailed(int result);

	public native void purchaseSuccess(String inv);

	public native void consumeSuccess(String inv);

	public native void consumeFailed(int result, String inv);

	/**
	 * google in-app环境setup后返回的结果,由Native端实现
	 * 
	 * @param bSuccess
	 */
	public native void NativeSetupFinished(boolean bSuccess);

	/**
	 * 查询用户已经后买的商品 在IAP V3中没有单独的restore，如果要使用restore功能，直接将参数设置为false即可
	 * 
	 * 
	 * 注：Umaneged类型的商品是不能被查询到的。被Consume后的Managed类型商品，在用户清除手机数据或者重新安装
	 * APP后也是不能被查询到的。
	 * 
	 * @param isLocal
	 *            是否优先查询本地数据。为true：如果本地有数据，直接返回本地数据，否则从服务器上读取；false：直接从服务器上读取，
	 *            忽略本地数据，在Debug模式，且为True的情况下，将只查询本地数据
	 */
	public void onQuery(boolean isLocal, OnQueryListener listener) {
		this.queryListener = listener;
		if (!setupPrepared) {
			if (listener != null) {
				listener.onQueryInventoryFinished(
						new IapResult(
								ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY,
								IapHelper
										.getResponseDesc(ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY)),
						null);
			}
			if (queryNative) {
				queryFailed(ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY);
				queryNative = false;
			}
			return;
		}
		if (isLocal) {// 查询本地数据
			if (getPlatformCode() == PlatformCode.GOOGLEPLAY) {
				PurchaseDatabase db = new PurchaseDatabase(this);
				Cursor cursor = db.queryAllPurchasedItems();
				Inventory inv = new Inventory();
				while (cursor.moveToNext()) {
					// skus.add(new Purchase(cursor.getString(0)));
					inv.addPurchase(new PurchaseBean(cursor.getString(0)));
				}
				cursor.close();
				db.close();

				if (!mHelper.isDebugLog() && inv.getAllPurchases().isEmpty()) {
					if (Utils.checkNetwork(this))
						mHelper.queryInventoryAsync(_queryListener);
					else {
						if (listener != null) {
							listener.onQueryInventoryFinished(
									new IapResult(
											ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,
											IapHelper
													.getResponseDesc(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)),
									null);
						}
						if (queryNative) {
							queryFailed(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR);
							queryNative = false;
						}
					}
				} else// 本地数据不为空，直接返回
				{
					if (listener != null) {
						listener.onQueryInventoryFinished(new IapResult(
								ResponseCode.BILLING_RESPONSE_RESULT_OK, null),
								inv);
					}
					if (queryNative) {
						querySuccess(convertInventory(inv.getAllPurchases()));
						queryNative = false;
					}
				}
			} else {
				if (Utils.checkNetwork(this))
					amazonPurchase.query(_queryListener);
				else {
					if (listener != null) {
						listener.onQueryInventoryFinished(
								new IapResult(
										ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,
										IapHelper
												.getResponseDesc(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)),
								null);
					}
					if (queryNative) {
						queryFailed(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR);
						queryNative = false;
					}
				}
			}
		} else {// 向服务器请求数据
			if (Utils.checkNetwork(this)) {
				if (getPlatformCode() == PlatformCode.GOOGLEPLAY) {
					mHelper.queryInventoryAsync(_queryListener);
				} else if (getPlatformCode() == PlatformCode.AMAZON
						&& amazonPurchase != null) {
					amazonPurchase.restore(_queryListener);
				} else {
					if (listener != null) {
						listener.onQueryInventoryFinished(
								new IapResult(
										ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,
										IapHelper
												.getResponseDesc(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)),
								null);
					}
					if (queryNative) {
						queryFailed(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR);
						queryNative = false;
					}
				}
			} else {
				if (listener != null) {
					listener.onQueryInventoryFinished(
							new IapResult(
									ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,
									IapHelper
											.getResponseDesc(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)),
							null);
				}
				if (queryNative) {
					queryFailed(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR);
					queryNative = false;
				}
			}
		}
	}

	private String[] convertInventory(List<PurchaseBean> purs) {
		String[] returnValues = null;
		if (purs != null) {
			returnValues = new String[purs.size()];
			for (int i = 0; i < purs.size(); i++) {
				returnValues[i] = purs.get(i).getSku();
			}
		}
		return returnValues;
	}

	/**
	 * =======注：慎用此方法=======
	 * 
	 * 向Google Play服务器请求解除商品的owned状态，当商品被解除了owned状态后，用户就可以再次重复购买。
	 * 但同时此商品也不能再提供Restore功能，所以一般只有消耗类商品才进行consume。
	 * 在购买消耗类商品时，程序已经默认进行了consume，不需要手动consume，所以只有在出现特殊情况，后台没有consume的情况下，
	 * 才需要手动调用此方法。
	 * 
	 * @param purchases
	 *            需要consume的商品
	 */
	public void onConsumePurchase(PurchaseBean purchase,
			OnConsumeListener listener) {
		if (!setupPrepared) {
			if (listener != null) {
				listener.onConsumeFinished(
						null,
						new IapResult(
								ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY,
								IapHelper
										.getResponseDesc(ResponseCode.BILLING_RESPONSE_BILLING_NOT_READY)));
			}
			return;
		}
		if (Utils.checkNetwork(this)) {
			mHelper.consumeAsync(purchase, listener);
		} else {
			if (listener != null) {
				listener.onConsumeFinished(
						null,
						new IapResult(
								ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,
								IapHelper
										.getResponseDesc(ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)));
			}
		}
	}

	public void onConsumePurchase_native(String sku) {
		consumeNative = true;
		PurchaseBean pur = new PurchaseBean(sku);
		onConsumePurchase(pur, null);

	}

	/**
	 * 设置public key。必须在进行后买操作之前进行
	 * 
	 * @param publicKey
	 */
	public void setPublicKey(String publicKey) {
		if (mHelper != null) {
			mHelper.setPublicKey(publicKey);
		} else {
			Log.e(this.getClass().toString(), "mHelper is null!");
		}

	}

	/**
	 * 检测用户当前的设备环境是否支持IAP
	 * 
	 * @return true为支持，false为不支持
	 */
	public boolean isIapSuppored() {
		if (getPlatformCode() == PlatformCode.GOOGLEPLAY) {
			if (mHelper.mSetupDone && mHelper.IAPSuppored) {
				return true;
			}
		} else if (getPlatformCode() == PlatformCode.AMAZON) {
			if (amazonPurchase != null) {
				return amazonPurchase.isAvailable;
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1001) {
			if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
				super.onActivityResult(requestCode, resultCode, data);
				PermissionHelper.getInstance().checkPermissionsGranted(requestCode);
			}

		}
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		Log.d("LaunchActivity", "-----onRequestPermissionsResult-------");
		PermissionHelper.getInstance().notifyPermissionsChange(permissions, grantResults);
	}

	private OnPurchaseListener finishedListener = new OnPurchaseListener() {

		@Override
		public void onIabPurchaseFinished(IapResult result,
				PurchaseBean purchase) {
			if (result != null) {
				// 判断状态
				if (result.isSuccess()) {
					// 如果sku为unmanaged类型，需要进行consume
					if (getPlatformCode() == PlatformCode.GOOGLEPLAY
							&& !mHelper.isDebugLog()
							&& cacheSkus.get(purchase.getSku()).equals(
									SKU_TYPE_UNMANAGE)) {
						onConsumePurchase(purchase, _consumeListener);
					} else {
						cacheSkus.remove(purchase.getSku());
						// 向数据库中新增一条记录
						PurchaseDatabase db = new PurchaseDatabase(
								LaunchActivity.this);
						db.updatePurchasedItem(purchase.getSku(), 1);
						db.close();
						if (purchaseListener != null)
							purchaseListener.onIabPurchaseFinished(result,
									purchase);
						if (purchaseNative) {
							purchaseSuccess(purchase.getSku());
							purchaseNative = false;
						}
					}
					if (enableEvent()) {
						HashMap<String, String> attr = new HashMap<String, String>();
						attr.put("Purchase Result",
								purchase.getSku() + "," + IAPFlurryCode.RESULT_OK.toString());

					}
//					Analytics.getInstance(LaunchActivity.this).doAnalytics(AnalyticsEvent.EVENT_IAP,
//							"\"" + purchase.getSku() + ","
//									+ IAPFlurryCode.RESULT_OK.toString() + "\"");

				} else {
					if (purchaseListener != null)
						purchaseListener
								.onIabPurchaseFinished(result, purchase);
					if (purchaseNative) {
						purchaseFailed(result.getResponse());
						purchaseNative = false;
					}
					String resultStr = null;
					if (result.getResponse() == ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)
						resultStr = IAPFlurryCode.RESULT_NETWORKERROR
								.toString();
					else if (result.getResponse() == ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE)
						resultStr = IAPFlurryCode.RESULT_SERVICE_UNAVAILABLE
								.toString();
					else if (result.getResponse() == ResponseCode.BILLING_RESPONSE_RESULT_USER_CANCELED)
						resultStr = IAPFlurryCode.RESULT_USER_CANCELED
								.toString();
					else
						resultStr = IAPFlurryCode.RESULT_ERROR.toString();
					//发送Flurry Event
					String sku = "";
					if (purchase != null)
						sku = purchase.getSku();
					else{
						sku=lastPurchaseKey;
					}
					if (enableEvent()) {
						HashMap<String, String> attr = new HashMap<String, String>();
						attr.put("Purchase Result", sku + "," + resultStr);

					}
					

				}

			} else {
				if (purchaseListener != null)
					purchaseListener.onIabPurchaseFinished(new IapResult(
							ResponseCode.BILLING_RESPONSE_RESULT_ERROR, null),
							purchase);
				if (purchaseNative) {
					purchaseFailed(ResponseCode.BILLING_RESPONSE_RESULT_ERROR);
					purchaseNative = false;
				}
			}
		}

	};

	private OnConsumeListener _consumeListener = new OnConsumeListener() {

		@Override
		public void onConsumeFinished(PurchaseBean purchase, IapResult result) {
			if (result != null && result.isSuccess()) {
				cacheSkus.remove(purchase.getSku());
				if (purchaseListener != null)
				{
					purchaseListener.onIabPurchaseFinished(result, purchase);
				}
				if (consumeListener != null)
					consumeListener.onConsumeFinished(purchase, result);
				if (consumeNative) {
					consumeSuccess(purchase.getSku());
					consumeNative = false;
				}
				if (purchaseNative) {
					purchaseSuccess(purchase.getSku());
					purchaseNative = false;
				}
			} else {
				if (purchaseListener != null)
					purchaseListener.onIabPurchaseFinished(result, purchase);
				if (consumeListener != null)
					consumeListener.onConsumeFinished(purchase, result);
				if (consumeNative) {
					consumeFailed(
							null == result ? ResponseCode.BILLING_RESPONSE_RESULT_ERROR
									: result.getResponse(), purchase.getSku());
					consumeNative = false;
				}
				if (purchaseNative) {
					purchaseFailed(result.getResponse());
					purchaseNative = false;
				}
			}
		}

	};

	private OnQueryListener _queryListener = new OnQueryListener() {

		@Override
		public void onQueryInventoryFinished(IapResult result, Inventory inv) {
			String resultStr = "";
			if (result.getResponse() == ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR)
				resultStr = IAPFlurryCode.RESULT_NETWORKERROR.toString();
			else if (result.getResponse() == ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE)
				resultStr = IAPFlurryCode.RESULT_SERVICE_UNAVAILABLE.toString();
			else if (result.getResponse() == ResponseCode.BILLING_RESPONSE_RESULT_USER_CANCELED)
				resultStr = IAPFlurryCode.RESULT_USER_CANCELED.toString();
			else if(result.getResponse() == ResponseCode.BILLING_RESPONSE_RESULT_OK)
				resultStr = IAPFlurryCode.RESULT_OK.toString();
			else
				resultStr = IAPFlurryCode.RESULT_ERROR.toString();

			// 更新数据库记录
			if (result.isSuccess()) {
				List<PurchaseBean> skus = null;
				if (inv != null)
					skus = inv.getAllPurchases();
				if (skus != null && !skus.isEmpty()) {
					PurchaseDatabase db = new PurchaseDatabase(
							LaunchActivity.this);
					for (PurchaseBean s : skus) {
						if (s.getPurchaseState() == 2)// 用户退款,从数据库中删除记录
						{
							db.updatePurchasedItem(s.getSku(), 0);
						} else {
							db.updatePurchasedItem(s.getSku(), 1);

						}
					}
					db.close();
				}

				if (queryNative) {
					if (inv != null && inv.getAllPurchases() != null)
						querySuccess(convertInventory(inv.getAllPurchases()));
					else
						querySuccess(null);
					queryNative = false;

				}
			} else {  
				if (queryNative) {
					queryFailed(result.getResponse());
					queryNative = false;
				}
			}
			if (queryListener != null)
				queryListener.onQueryInventoryFinished(result, inv);

		}

	};

	private OnSetupListener _setupListener = new OnSetupListener() {
		public void onIabSetupFinished(IapResult result) {
			setupPrepared = true;
			if (setupListener != null)
				setupListener.onIabSetupFinished(result);

			if (mBNativeInited)
				NativeSetupFinished(result.isSuccess());
		}
	};



}
