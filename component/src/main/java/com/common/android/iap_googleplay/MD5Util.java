package com.common.android.iap_googleplay;

import java.security.MessageDigest;

public class MD5Util {
	private static String[] hexDigits = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 验证字符串是否一致
	 * 
	 * @param md5Str
	 *            经加密后的字符串
	 * @param inputstr
	 *            未经加密的字符串
	 * @return
	 */
	public static boolean verify(String md5Str, String inputstr) {
		if (md5Str.equals((encodeByMD5(inputstr)))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 对字符串进行MD5 加密
	 * 
	 * @param originstr
	 * @return
	 */
	public static String encodeByMD5(String originstr) {
		if (originstr != null) {
			try {
				//创建具有指定算法名称的信息摘要
				MessageDigest md = MessageDigest.getInstance("MD5");
				//使用指定的字节数组对摘要进行最后的更新，然后完成摘要计算
				byte[] results = md.digest(originstr.getBytes());
				//将得到的字节数组编程字符窜返回
				String resultString = byteArrayToHexString(results);
				return resultString.toUpperCase();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultsb = new StringBuffer();
		int i = 0;
		for (i = 0; i < b.length; i++) {
			resultsb.append(byteToHexString(b[i]));
		}
		return resultsb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n / 16;
		return hexDigits[d1] + hexDigits[d2];
	}
}
