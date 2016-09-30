package com.common.android.facebook;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.common.android.LaunchActivity;
import com.common.android.facebook.FacebookHandler.IFacebookHandlerListener;

import java.io.IOException;
import java.io.InputStream;

public class FacebookSender {

	private final static long POST_THRESHOLD = 2000;

	private FacebookHandler mFacebookHandler = null;
	private IFacebookHandlerListener mFacebookHandlerListener = null;
	private LaunchActivity mActivity;
	private long mLastPost = 0;
	private Bundle savedInstanceState;

	public FacebookSender(LaunchActivity activity, Bundle savedInstanceState) {
		mActivity = activity;
		this.savedInstanceState = savedInstanceState;
	}

	public void setFacebookHandlerListener(IFacebookHandlerListener listener) {
		mFacebookHandlerListener = listener;
	}

	public void onResume() {
		if (null != mFacebookHandler)
			mFacebookHandler.onResume();
	}

	public void onPause() {
		if (null != mFacebookHandler)
			mFacebookHandler.onPause();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mFacebookHandler)
			mFacebookHandler.onActivityResult(requestCode, resultCode, data);
	}

	public void onSaveInstanceState(Bundle outState) {
		if (null != mFacebookHandler)
			mFacebookHandler.onSaveInstanceState(outState);
	}

	public void postStatusUpdate(final boolean bNativeCall, final int tag,
			final String text) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());
		mFacebookHandler.postStatusUpdate(text);
		

	}

	public void postPhotoFromFile(final boolean bNativeCall, final int tag,
			final String fileName) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		Bitmap postImg = BitmapFactory.decodeFile(fileName);
		mFacebookHandler.postPhoto(postImg);

	}

	public void postRichContentFile(final boolean bNativeCall, final int tag,
			final String message, final String link, final String fileName) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		Bitmap postImg = BitmapFactory.decodeFile(fileName);
		mFacebookHandler.postRichContent(message, link, postImg);
		

	}

	public void postPhotoFromDrawable(final boolean bNativeCall, final int tag,
			final int imgId) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		Bitmap postImg = BitmapFactory.decodeResource(mActivity.getResources(),
				imgId);
		mFacebookHandler.postPhoto(postImg);
		

	}

	public void postRichContentDrawable(final boolean bNativeCall,
			final int tag, final String message, final String link,
			final int imgId) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		Bitmap postImg = BitmapFactory.decodeResource(mActivity.getResources(),
				imgId);
		mFacebookHandler.postRichContent(message, link, postImg);
		

	}

	public void postPhotoFromAsset(final boolean bNativeCall, final int tag,
			final String fileName) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		AssetManager asm = mActivity.getAssets();
		Bitmap postImg = null;
		InputStream is = null;
		try {
			is = asm.open(fileName);
			postImg = BitmapFactory.decodeStream(is);
		} catch (IOException ex) {
			postImg = null;
		} finally {
			try {
				if (null != is)
					is.close();
			} catch (Exception e) {
			}
		}

		mFacebookHandler.postPhoto(postImg);
		

	}

	public void postRichContentAsset(final boolean bNativeCall, final int tag,
			final String message, final String link, final String assetFileName) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, bNativeCall);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		AssetManager asm = mActivity.getAssets();
		Bitmap postImg = null;
		InputStream is = null;
		try {
			is = asm.open(assetFileName);
			postImg = BitmapFactory.decodeStream(is);
		} catch (IOException ex) {
			postImg = null;
		} finally {
			try {
				if (null != is)
					is.close();
			} catch (Exception e) {
			}
		}

		mFacebookHandler.postRichContent(message, link, postImg);
		

	}

	/**
	 * 由FacebookBridge 这边调用
	 * 
	 */
	public void postRichContentDialog(int tag,String message, String link, String mpic,String name,String mCaption) {
		if (System.currentTimeMillis() - mLastPost < POST_THRESHOLD)
			return;
		mLastPost = System.currentTimeMillis();

		if (null != mFacebookHandler) {
			mFacebookHandler = null;
		}

		mFacebookHandler = new FacebookHandler(mActivity, false,
				savedInstanceState);
		mFacebookHandler.setListener(mFacebookHandlerListener);
		mFacebookHandler.setTag(tag);
		mFacebookHandler.onCreate(mActivity.getIntent().getExtras());

		mFacebookHandler.postRichContentDialog(message, link, mpic,name,mCaption);
		

	}
}