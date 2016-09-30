package com.common.android.newsblast;

public interface NewsBlastListener {

	/**
	 * 褰撲粠鏈嶅姟鍣ㄤ笂鍙楀埌娑堟伅鍚庤皟鐢ㄦ鍑芥暟
	 */
	public void onMessage(NewsBean message);

	/**
	 * 褰揇ialog鍏抽棴鏃惰皟鐢ㄦ鍑芥暟
	 */
	public void onClose();
	
	/**
	 * 浣咲ialog鍏抽棴骞朵笖璺宠浆鍒版祻瑙堝櫒鏃惰皟鐢ㄦ鍑芥暟
	 */
	public void onRedirectAndClose();

	/**
	 * 浣哊ews Blast鍑虹幇浠讳綍寮傚父鏃惰皟鐢ㄦ鍑芥暟
	 * 
	 */
	public void onError(ErrorCode code);

}
