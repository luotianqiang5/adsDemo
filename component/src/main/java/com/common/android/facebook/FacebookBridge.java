package com.common.android.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.common.android.LaunchActivity;
import com.common.android.facebook.FacebookHandler.IFacebookHandlerListener;

public class FacebookBridge {

	private FacebookSender mFacebookSender;
	private LaunchActivity mActivity;
	private boolean mNativeInited;

	private static FacebookBridge m_Instance = null;

	public static FacebookBridge getInstance() {
		if (null == m_Instance)
			m_Instance = new FacebookBridge();
		return m_Instance;
	}

	private FacebookBridge() {
	}

	public void setup(LaunchActivity activity, IFacebookHandlerListener postListener,
			boolean bNativeInit, Bundle savedInstanceState) {
		mActivity = activity;
		mNativeInited = bNativeInit;
		mFacebookSender = new FacebookSender(mActivity, savedInstanceState);
		mFacebookSender.setFacebookHandlerListener(postListener);

		if (mNativeInited)
			nativeInit();
	}

	public void setFacebookHandlerListener(IFacebookHandlerListener postListener) {
		if (null != mFacebookSender) {
			mFacebookSender.setFacebookHandlerListener(postListener);
		}
	}

	public void destroy() {
		if (mNativeInited)
			nativeFinalize();
	}

	public void onResume() {
		if (null != mFacebookSender)
			mFacebookSender.onResume();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mFacebookSender)
			mFacebookSender.onActivityResult(requestCode, resultCode, data);
	}

	public void onPause() {
		if (null != mFacebookSender)
			mFacebookSender.onPause();
	}

	public void onSaveInstanceState(Bundle outState) {
		if (null != mFacebookSender)
			mFacebookSender.onSaveInstanceState(outState);
	}

	/**
	 * normal post interfaces
	 * 
	 * @description post result will call method of listener
	 */
	public void postStatusUpdate(final int tag, final String text) {
		if (null != mFacebookSender)
			mFacebookSender.postStatusUpdate(false, tag, text);
	}

	public void postPhotoFromAsset(final int tag, final String fileName) {
		if (null != mFacebookSender)
			mFacebookSender.postPhotoFromAsset(false, tag, fileName);
	}

	public void postPhotoFromDrawable(final int tag, final int imgId) {
		if (null != mFacebookSender)
			mFacebookSender.postPhotoFromDrawable(false, tag, imgId);
	}

	public void postPhotoFromFile(final int tag, final String fileName) { // fileName
																			// must
																			// be
																			// full
																			// path
		if (null != mFacebookSender)
			mFacebookSender.postPhotoFromFile(false, tag, fileName);
	}

	public void postRichContentFile(final int tag, final String message,
			final String link, final String fileName) {
		if (null != mFacebookSender)
			mFacebookSender.postRichContentFile(false, tag, message, link,
					fileName);
	}

	public void postRichContentDrawable(final int tag, final String message,
			final String link, final int imgId) {
		if (null != mFacebookSender)
			mFacebookSender.postRichContentDrawable(false, tag, message, link,
					imgId);
	}

	public void postRichContentAsset(final int tag, final String message,
			final String link, final String assetFileName) {
		if (null != mFacebookSender)
			mFacebookSender.postRichContentAsset(false, tag, message, link,
					assetFileName);
	}

	/**
     * 调用FacebookSender那边的方法来发送请求
     *
     */
	public void postRichContentDialog(final int tag, final String message,
			final String link, String mpic,String name,String mCaption) {
		if (null != mFacebookSender)
			mFacebookSender.postRichContentDialog(tag,message,link,mpic,name,mCaption);
	}

	/**
	 * native post interfaces
	 * 
	 * @description post result will call Native Callback
	 */
	public void NativePostStatusUpdate(final int tag, final String text) {
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postStatusUpdate(true, tag, text);
	}

	public void NativePostPhotoFromAsset(final int tag, final String fileName) {
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postPhotoFromAsset(true, tag, fileName);
	}

	public void NativePostPhotoFromDrawable(final int tag, final int imgId) {
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postPhotoFromDrawable(true, tag, imgId);
	}

	public void NativePostPhotoFromFile(final int tag, final String fileName) { // fileName
																				// must
																				// be
																				// full
																				// path
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postPhotoFromFile(true, tag, fileName);
	}

	public void NativePostRichContentFile(final int tag, final String message,
			final String link, final String fileName) {
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postRichContentFile(true, tag, message, link,
					fileName);
	}

	public void NativePostRichContentDrawable(final int tag,
			final String message, final String link, final int imgId) {
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postRichContentDrawable(true, tag, message, link,
					imgId);
	}

	public void NativePostRichContentAsset(final int tag, final String message,
			final String link, final String assetFileName) {
		if (false == mNativeInited)
			return;
		if (null != mFacebookSender)
			mFacebookSender.postRichContentAsset(true, tag, message, link,
					assetFileName);
	}

	// native interfaces
	// setup environment
	private native void nativeInit();

	// destroy
	private native void nativeFinalize();

	// notify the result of post
	public native void nativeOnPostStatus(int tag, boolean bSuccess);

	public native void nativeOnPostPhoto(int tag, boolean bSuccess);

	public native void nativeOnPostRichContent(int tag, boolean bSuccess);

	public native void nativeOnPostTimeOut(int tag);
}