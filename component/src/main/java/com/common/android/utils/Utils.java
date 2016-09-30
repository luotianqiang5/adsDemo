package com.common.android.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class Utils {
	public static final String STR_DOMAIN = "studioService";

	/**
	 * 检查当前的网络环境是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return false;
		}
		return true;
	}

	/**
	 * 去除出重复的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String repeatFilter(String str) {
		StringBuffer sb = new StringBuffer();
		if (str != null) {
			List<String> _strs = Arrays.asList(str.split(","));
			HashSet<String> hashSet = new HashSet<String>(_strs);

			List<String> norepeat = new ArrayList<String>(hashSet);
			for (String s : norepeat) {
				sb.append(s);
				sb.append(",");
			}

		}

		return sb.toString();
	}

	public static String getMetaData(Context context, String key) {
		String metaValue = null;
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			Bundle bundle = ai.metaData;
			Object o = bundle.get(key);
			if (o != null)
				metaValue = o.toString();
		} catch (NameNotFoundException e) {
		}
		return metaValue;
	}

	public static boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}

	public static String getTimeZoneStr() {
		int hours = TimeZone.getDefault().getRawOffset() / (60 * 60 * 1000);
		int minutes=Math.abs(TimeZone.getDefault().getRawOffset() % (60 * 60 * 1000));
		minutes=(int)((minutes/(float)(60 * 60 * 1000))*60);
		return  (hours < 0 ? "" : "+") + hours+":"+(minutes == 0 ? "00" : ""+minutes);
	}




	public static boolean sendDataByPost(String _url, String data) {
		if (_url == null || _url.length() < 1 || data == null
				|| data.length() < 1)
			return true;
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL url = new URL(_url);
			HttpURLConnection httpConn = (java.net.HttpURLConnection) url
					.openConnection();
			// 设置通用属性
			httpConn.setRequestProperty("Accept", "*/*");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
			// httpConn.setRequestProperty("Accept-Charset", "GBK");
			// httpConn.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded;charset=GBK");
			// 设置POST方式
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			// 获取HttpURLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.write(data);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应，设置编码方式
			in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream(), "UTF-8"));
			String line;
			// 读取返回的内容
			while ((line = in.readLine()) != null) {
				result += line + "\r\n";
			}
			if (httpConn.getResponseCode() == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (out != null)
					out.close();
				in.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取服务器时间戳
	 * @param strUrl
	 * @return
	 */
	public static long getNetTime(String strUrl) {
		URL url;
		try {
			url = new URL(strUrl);
			URLConnection uc = url.openConnection();// 生成连接对象
			uc.connect(); // 发出连接
			return uc.getDate(); // 取得网站日期时间
		} catch (MalformedURLException e) {
			// e.printStackTrace();
		} catch (IOException e) {
		//	e.printStackTrace();
		}
		return 0l;

	}



	/**
	 * 替换特殊符号：奖单引号，双引号和都好替换为下划线，去除换行符 空格和制表符
	 * @param str
	 * @return
	 */
	public static String replaceSpecialSign(String str){
		 String dest = "";
	        if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            String dest2 = m.replaceAll("");
	            
	            Pattern p2 = Pattern.compile("\"|\'|\\.|,");
	            Matcher m2 = p2.matcher(dest2);
	            dest = m2.replaceAll("_");
	        }
		return dest;
				
	}
	
	/**
	 *去除换行符 空格和制表符
	 * @param str
	 * @return
	 */
	public static String replaceSpecialSign2(String str){
		 String dest = "";
	        if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            dest = m.replaceAll("");
	            
	           
	        }
		return dest;
				
	}
}
