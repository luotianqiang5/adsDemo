package com.common.android.gcm;

public interface GCMRegisterCallBack {
	
	/**
	 * 
	 * @param isSuccessful  注册是否成功
	 * @param registerId  注册ID
	 */
	public void onRegisterFinished(boolean isSuccessful,String registerId);

}
