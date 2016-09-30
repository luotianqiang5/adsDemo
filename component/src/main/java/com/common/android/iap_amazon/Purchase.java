package com.common.android.iap_amazon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.amazon.inapp.purchasing.BasePurchasingObserver;
import com.amazon.inapp.purchasing.GetUserIdResponse;
import com.amazon.inapp.purchasing.GetUserIdResponse.GetUserIdRequestStatus;
import com.amazon.inapp.purchasing.Offset;
import com.amazon.inapp.purchasing.PurchaseResponse;
import com.amazon.inapp.purchasing.PurchaseUpdatesResponse;
import com.amazon.inapp.purchasing.PurchasingManager;
import com.amazon.inapp.purchasing.Receipt;
import com.common.android.IapResult;
import com.common.android.Inventory;
import com.common.android.LaunchActivity;
import com.common.android.LaunchActivity.IAPFlurryCode;
import com.common.android.OnPurchaseListener;
import com.common.android.OnQueryListener;
import com.common.android.OnSetupListener;
import com.common.android.PurchaseBean;
import com.common.android.ResponseCode;
import com.common.android.iap_googleplay.IapHelper;
import com.common.android.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Purchase {
	public final static int MSG_INIT = 9001;
	public final static int MSG_PURCHASE = 9002;
	public final static int MSG_RESTORE = 9003;
	private static String str_iapKey = "iapKey";
	private boolean isDebug = false;
	private LaunchActivity context;
	private String userId;
	List<PurchaseBean> catalogs;
	public PurchaseBean catalog;// 当前购买的CatalogEntry,仅调用singlePurchase方法时有效
	public boolean isAvailable;
	public static final String DB_INITIALIZED = "db_initialized";
	public static final String PREFILE = "purchasePreFile";
	private UIHandler uiHandler;
	private boolean isRegister = false;
	private int oper;
	private static Purchase purchase=null;
	private OnSetupListener setupListener;
	private OnPurchaseListener purchaseListener;
	private OnQueryListener queryListener;
	/**
	 * 
	 * @param context
	 * @param isDebug
	 *            是否开启沙箱模式 ，true为开启，false为不开启 注：在应用发布时，沙箱模式必须关闭，否则用户将不能正常使用
	 */
	private Purchase(LaunchActivity context, boolean isDebug) {
		this.isDebug = isDebug;
		this.isAvailable = isDebug;
		this.context = context;
		catalogs = new ArrayList<PurchaseBean>();
		uiHandler = new UIHandler(context.getMainLooper());
		

	}
	
	/**
	 * 获取一个Purchase对象
	 * @param context
	 * @param isDebug 是否开启Sanbox模式
	 * @return
	 */
	public static Purchase getInstance(LaunchActivity context, boolean isDebug)
	{
		if(purchase==null)
			purchase=new Purchase(context, isDebug);
		
		return purchase;
	}
	
	public void onStartSetupBilling(OnSetupListener lis)
	{
		setupListener=lis;
		if (!isRegister&&uiHandler!=null) {
			uiHandler.sendEmptyMessage(MSG_INIT);
		}else
		{
			IapResult r=new IapResult(isAvailable?0:3, IapHelper.getResponseDesc(isAvailable?0:3));
			setupListener.onIabSetupFinished(r);
		}
	}

	



	private void sendMSG_error(String catelogId, int code,int type) {
		IapResult result=new IapResult(code, IapHelper.getResponseDesc(code));
		if (type==0&&purchaseListener != null)
		{
			purchaseListener.onIabPurchaseFinished(result, null);
		}else if(type==1&&queryListener!=null)
		{
			queryListener.onQueryInventoryFinished(result, null);
		}
		
		//增加FlurryEvent
		if (oper == MSG_PURCHASE&&!isDebug) {
			Map<String, String> attr = new HashMap<String, String>();
			switch (code) {
			case ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
				attr.put("Purchase Result", IAPFlurryCode.RESULT_SERVICE_UNAVAILABLE.toString());
				break;
			case ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR:
				attr.put("Purchase Result", IAPFlurryCode.RESULT_NETWORKERROR.toString());
				break;
			case ResponseCode.BILLING_RESPONSE_RESULT_USER_CANCELED:
				attr.put("Purchase Result", IAPFlurryCode.RESULT_USER_CANCELED.toString());
				break;
			default:
				attr.put("Purchase Result", IAPFlurryCode.RESULT_ERROR.toString());
				break;

			}

		}
	}

	private void sendMSG_success(List<PurchaseBean> datas,int type) {
		
		IapResult result=new IapResult(ResponseCode.BILLING_RESPONSE_RESULT_OK, IapHelper.getResponseDesc(ResponseCode.BILLING_RESPONSE_RESULT_OK));
		if (type==0&&purchaseListener != null)
		{
			if(datas!=null&&datas.size()>0)
				purchaseListener.onIabPurchaseFinished(result, datas.get(0));
			else
				purchaseListener.onIabPurchaseFinished(result, null);
				
		}else if(type==1&&queryListener!=null)
		{
			if(datas!=null&&datas.size()>0)
			{
				Inventory inv=new Inventory();
				for(PurchaseBean p:datas)
				{
					inv.addPurchase(p);
				}
				
				queryListener.onQueryInventoryFinished(result, inv);
			}else
			{
				queryListener.onQueryInventoryFinished(result, null);
			}
		}
		if (oper == MSG_PURCHASE&&!isDebug) {
			Map<String, String> attr = new HashMap<String, String>();
			attr.put("Purchase Result", IAPFlurryCode.RESULT_OK.toString());

		}
		
	}

	/**
	 * 恢复历史购买信息。无论本地是否有购买缓存记录，此操作都会向Amazon后台服务器重新请求数据，且本地记录与服务器记录不相符时，以返回的最新纪录为准
	 * 故此操作耗时较长，一般在app第一次启动时调用 注:仅恢复Managed类型的产品
	 */
	public void restore(OnQueryListener queryListener) {
		oper=MSG_RESTORE;
		this.queryListener=queryListener;
		if (Utils.checkNetwork(context)) {
			if (isAvailable) {
				catalogs.clear();
				//	PurchasingManager.initiatePurchaseUpdatesRequest(Offset.BEGINNING);
				uiHandler.sendEmptyMessage(MSG_RESTORE);
			} else {
				sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE,1);
			}
		} else {
			sendMSG_error("", ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,1);
		}

	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * 查询历史购买信息，但本地有缓存记录是，查询本地缓存，如果本地没有缓存【app第一次启动时】，会向后台服务器查询 .一般在应用每次启动时调用
	 */
	public void query(OnQueryListener queryListener) {
		this.queryListener=queryListener;
		SharedPreferences prefs = context.getSharedPreferences(Purchase.PREFILE, Context.MODE_PRIVATE);
		boolean initialized = prefs.getBoolean(Purchase.DB_INITIALIZED, false);
		if (initialized) {
			PurchaseDatabase database = new PurchaseDatabase(context);
			List<PurchaseBean> datas = database.queryAll();
			database.close();
			sendMSG_success(datas,1);
		} else {
			if (isAvailable) {

				//				PurchasingManager.initiatePurchaseUpdatesRequest(Offset.BEGINNING);
				uiHandler.sendEmptyMessage(MSG_RESTORE);
			} else {
				sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE,1);
			}
		}

	}

	/**
	 * 购买单件商品
	 * 
	 * @param catalog
	 */
	public void singlePurchase(PurchaseBean catalog,OnPurchaseListener purchaseListener) {
		this.purchaseListener=purchaseListener;
		oper=MSG_PURCHASE;
		if (isDebug)
			Log.i("", "isAvailable:" + isAvailable + "  isDebug:" + isDebug);
		this.catalog = catalog;
		if (Utils.checkNetwork(context)) {
			if (isAvailable) {
				catalogs.clear();
				//	PurchasingManager.initiatePurchaseRequest(catalog.catalogId);
				Message m = new Message();
				Bundle b = new Bundle();
				b.putString(str_iapKey, this.catalog.getSku());
				m.setData(b);
				m.what = MSG_PURCHASE;
				uiHandler.sendMessage(m);
			} else {
				sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE,0);
			}
		} else {
			sendMSG_error("", ResponseCode.BILLING_RESPONSE_NETWORRK_ERROR,0);
		}

	}

	private void inserData(List<PurchaseBean> catalogs) {
		PurchaseDatabase database = new PurchaseDatabase(context);
		database.insert(catalogs);
		database.close();
		SharedPreferences prefs = context.getSharedPreferences(Purchase.PREFILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(Purchase.DB_INITIALIZED, true);
		edit.commit();
	}

	private void inserData(PurchaseBean catalog) {
		PurchaseDatabase database = new PurchaseDatabase(context);
		database.insert(catalog);
		database.close();
	}

	public void destory() {
		if(catalogs!=null)
			catalogs.clear();
		if(uiHandler!=null)
			uiHandler=null;
		catalogs=null;
		setupListener=null;
		purchase=null;
		purchaseListener=null;
		
	}

	@SuppressLint("HandlerLeak")
	public class UIHandler extends Handler {
		public UIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				PurchasingManager.registerObserver(new MyPurchaseObserver(context));
				PurchasingManager.initiateGetUserIdRequest();
				isRegister = true;
				break;
			case MSG_PURCHASE:
				
				String iapKey = msg.getData().getString(str_iapKey);
				PurchasingManager.initiatePurchaseRequest(iapKey);
				break;
			case MSG_RESTORE:
				
				PurchasingManager.initiatePurchaseUpdatesRequest(Offset.BEGINNING);
				break;
			}

		}

	}

	private class MyPurchaseObserver extends BasePurchasingObserver {
		public MyPurchaseObserver(Context context) {
			super(context);

		}

		@Override
		public void onSdkAvailable(boolean isSandboxMode) {
			super.onSdkAvailable(isSandboxMode);
			
			// 测试状态isAvailable必须为true
			if (isDebug) {
				isAvailable = true;
			} else {
				isAvailable = isSandboxMode ? false : true;
			}
			if(setupListener!=null)
			{
				int result=!isAvailable?ResponseCode.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:ResponseCode.BILLING_RESPONSE_RESULT_OK;
				
				IapResult r=new IapResult(result, IapHelper.getResponseDesc(result));
				setupListener.onIabSetupFinished(r);
			}
		}

		@Override
		public void onGetUserIdResponse(final GetUserIdResponse getUserIdResponse) {
			if (getUserIdResponse.getUserIdRequestStatus() == GetUserIdRequestStatus.SUCCESSFUL) {
				userId = getUserIdResponse.getUserId();
			} else {
				userId = "";
			}
		}

		@Override
		public void onPurchaseResponse(PurchaseResponse response) {
			super.onPurchaseResponse(response);
			switch (response.getPurchaseRequestStatus()) {
			case ALREADY_ENTITLED:
				inserData(catalog);
				catalogs.add(catalog);
				sendMSG_success(catalogs,0);
				break;
			case SUCCESSFUL:
				if (catalog.getSku().equals(response.getReceipt().getSku())) {
					inserData(catalog);
					catalogs.add(catalog);
					sendMSG_success(catalogs,0);
				} else {
					sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_ERROR,0);
				}
				break;
			case INVALID_SKU:
				if (context == null) {
				}
				;
				if (uiHandler == null) {
				}
				;
				sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE,0);
				break;
			case FAILED:
				sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_ERROR,0);
				break;

			}

		}

		@Override
		public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse response) {
			super.onPurchaseUpdatesResponse(response);
			if (response.getUserId().equals(userId)) {
				switch (response.getPurchaseUpdatesRequestStatus()) {
				case SUCCESSFUL:
					for (final Receipt receipt : response.getReceipts()) {
						//
						PurchaseBean p=new PurchaseBean(receipt.getSku());
						
						p.mToken=receipt.getPurchaseToken();
						switch (receipt.getItemType()) {
						case ENTITLED:
							p.mItemType=Managed.MANAGED.toString();
							break;
						case CONSUMABLE:
							p.mItemType=Managed.UNMANAGED.toString();
							break;
						case SUBSCRIPTION:
							// 订阅类型商品，暂时没这功能。。。。
							p.mItemType=Managed.SUBSCRIPTION.toString();
							break;
						}
						catalogs.add(p);

					}
					Offset newOffset = response.getOffset();
					// 判断是否还有为返回的数据
					if (response.isMore()) {
						PurchasingManager.initiatePurchaseUpdatesRequest(newOffset);
					} else {
						sendMSG_success(catalogs,1);
						if (catalogs != null && catalogs.size() > 0) {
							inserData(catalogs);
						}
					}
					break;
				case FAILED:
					sendMSG_error("", ResponseCode.BILLING_RESPONSE_RESULT_ERROR,1);
					break;
				}
			}
		}
	}
}
