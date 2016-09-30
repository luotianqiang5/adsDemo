package com.common.android.newsblast;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.common.android.LaunchActivity;
import com.common.android.utils.UDID;
import com.common.android.utils.Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NewsBlast {
	public static boolean debug;
	private static final String TAG = "NewsBlast";
	public static final String STR_DOMAIN = "studioService";
	private static final String STR_PREFERENCE = "newsblast";
	private static final String STR_VIEWED = "viewed";
	private LaunchActivity context;
	private NewsBlastListener listener;
	private String domain;
	private String appBundleId;
	private String bundleId;
	private SharedPreferences preference;
	private boolean breakOff;
	private NewsBean news = null;
	private Handler handler;
	private int platformCode;
	private boolean firstRequest;
	private long onMessageTime;


	public NewsBlast(LaunchActivity _context, int platformCode) {
		this.context = _context;
		this.platformCode = platformCode;
		firstRequest=false;
		debug = false;
		breakOff = false;
		preference = context.getSharedPreferences(STR_PREFERENCE, Context.MODE_PRIVATE);
		bundleId = context.getPackageName();
		appBundleId = bundleId.substring(bundleId.lastIndexOf(".") + 1);
		domain = createServerPath();
		handler = new Handler(context.getMainLooper()) {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0) {
					
					if (news != null) {
						preference.edit().putString(STR_VIEWED, Utils.repeatFilter(preference.getString(STR_VIEWED, "") + "," + news.getId())).commit();
						if (listener != null) {
							listener.onMessage(news);
						}
						if (!breakOff) {
							onMessageTime=System.currentTimeMillis();
							

							Builder builder = new AlertDialog.Builder(context).setTitle(news.getTitle()).setMessage(news.getContent());
							final String link = news.getLink();
							if (link != null && !link.equals("")) {
								builder.setPositiveButton("OK", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

										handler.sendEmptyMessage(1);

										if (listener != null)
											listener.onRedirectAndClose();
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(link));
										context.startActivity(i);
										HashMap<String, String> atrr = new HashMap<String, String>();
										atrr.put("newsblast", "\"OK,"+(System.currentTimeMillis()-onMessageTime)+" \"");

										
									}
								});
								builder.setNegativeButton("Cancel", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										if (listener != null)
											listener.onClose();
										HashMap<String, String> atrr = new HashMap<String, String>();
										atrr.put("newsblast", "\"Cancel,"+(System.currentTimeMillis()-onMessageTime)+" \"");
										//AnalyticsCenter.getInstace().sendEvent(AnalyticsEvent.EVENT_NEWS_BLAST_CLICKED, atrr);
									}
								});
							} else {
								builder.setPositiveButton("OK", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										if (listener != null)
											listener.onClose();
										
										HashMap<String, String> atrr = new HashMap<String, String>();
										atrr.put("newsblast", "\"OK,"+(System.currentTimeMillis()-onMessageTime)+" \"");
										//AnalyticsCenter.getInstace().sendEvent(AnalyticsEvent.EVENT_NEWS_BLAST_CLICKED, atrr);
									}
								});
							}

							builder.create().show();
						}
					}else
					{
						if (listener != null) {
							listener.onMessage(news);
						}
					}
				}else if(msg.what==1)
				{
					postCount();
				}
			};
		};

	}

	private void postCount() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String str_url = createServerPath() + "&id="+news.getId() ;

				HttpURLConnection conn = null;
				try {
					URL url = new URL(str_url);
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(20000);
					conn.setRequestProperty("accept", "text/xml;text/html");
					conn.connect();
					conn.getInputStream();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
			}
		}).start();
	}

	private String createServerPath() {
		String url="";
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			url = bundle.getString(STR_DOMAIN);
		} catch (NameNotFoundException e) {
			if (debug)
				e.printStackTrace();
		}
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		try {
			url = url + "/news/" + appBundleId + "/" + config.locale.getLanguage() + "/" + UDID.getUDID(context) + "/?" + "sysVer=" + android.os.Build.VERSION.RELEASE
					+ "&bundleVer=" + context.getPackageManager().getPackageInfo(bundleId, PackageManager.GET_CONFIGURATIONS).versionName + "&bundleID=" + bundleId
					+ "&news_viewed=" + preference.getString(STR_VIEWED, "") + "&platform=" + platformCode;
			if (debug)
				Log.i(TAG, "domain:" + url);
		} catch (NameNotFoundException e) {
			if (listener != null)
				listener.onError(ErrorCode.unknowError);
			if (debug)
				e.printStackTrace();
			return null;
		}
		return url;
	}


	public void doNewsBalst() {
		if(!firstRequest)
			new getMSGThread().start();
	}
	

	public void doNewsBlast_always()
	{
		new getMSGThread().start();
	}

	public class getMSGThread extends Thread {

		@Override
		public void run() {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			try {
				SAXParser parser = factory.newSAXParser();
				XMLParser xmlhandler = new XMLParser();
				parser.parse(domain, xmlhandler);
				news = xmlhandler.getNews();
				firstRequest=true;

			} catch (Exception e) {
				news=null;
				Log.e("NewsBlast", "doNewsBalst Error!" + domain);
				if (debug)
					e.printStackTrace();
				if (listener != null)
					listener.onError(ErrorCode.networkError);
			}
			handler.sendEmptyMessage(0);
		}
	};


	public void breakOff() {
		breakOff = true;
	}
	

	public void continueNews()
	{
		breakOff = false;
	}

	public void registerListener(NewsBlastListener listener) {
		this.listener = listener;
	}
	/*
	 * private AsyncTask<String, String, NewsBean> asyncTask = new
	 * AsyncTask<String, String, NewsBean>() {
	 * 
	 * @Override protected void onPostExecute(final NewsBean result) {
	 * super.onPostExecute(result); if (result == null) { // Log.i("",
	 * "new is null");
	 * 
	 * } else if (!breakOff) { if (listener != null) {
	 * listener.onMessage(result); } Builder builder = new
	 * AlertDialog.Builder(context
	 * ).setTitle(result.getTitle()).setMessage(result.getContent()); if
	 * (result.getLink() != null && !result.getLink().equals("")) {
	 * builder.setPositiveButton("OK", new OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { if
	 * (listener != null) listener.onRedirectAndClose(); Intent i = new
	 * Intent(Intent.ACTION_VIEW); i.setData(Uri.parse(result.getLink()));
	 * context.startActivity(i);
	 * 
	 * } }); builder.setNegativeButton("Cancel", new OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { if
	 * (listener != null) listener.onClose(); } }); } else {
	 * builder.setPositiveButton("OK", new OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { if
	 * (listener != null) listener.onClose();
	 * 
	 * } }); }
	 * 
	 * builder.create().show(); }
	 * 
	 * }
	 * 
	 * @Override protected NewsBean doInBackground(String... params) { NewsBean
	 * news = null; SAXParserFactory factory = SAXParserFactory.newInstance();
	 * try { SAXParser parser = factory.newSAXParser(); XMLParser handler = new
	 * XMLParser(); parser.parse(domain, handler); news = handler.getNews();
	 * 
	 * } catch (Exception e) { // e.printStackTrace(); if (listener != null)
	 * listener.onError(ErrorCode.networkError); }
	 * 
	 * if (news != null) { preference.edit().putString(STR_VIEWED,
	 * preference.getString(STR_VIEWED, "") + "," + news.getId()).commit(); }
	 * return news; }
	 * 
	 * };
	 */
}
