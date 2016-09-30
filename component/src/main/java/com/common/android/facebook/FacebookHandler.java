/**
 * @Author          liujian
 * @Version         1.0
 * @Date            2013-4-12
 * 
 * @Decription: FacebookHandler used to handler the connection with facebook
 */

package com.common.android.facebook;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.SharedPreferencesTokenCachingStrategy;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookHandler {

	private final static int MSG_TAG_TIMEOUT = 1;
	private final static int POST_TIMEOUT = 30000; // 30s is the timeout
													// threshold

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");

	private Session mCurrentSession;
	private Session.StatusCallback sessionStatusCallback;
	private SharedPreferencesTokenCachingStrategy mTokenCache;
	private int mHandlerTag;
	private boolean mBNativeCall;

	// status(post content)
	private String mStrPost = null;
	// picture(post content)
	private Bitmap mImgPost = null;
	// rich content(post content)
	private String mRichMessage;
	private String mRichLink;
	private Bitmap mRichContentImg = null;
	private String mPicture;
	private String mName;
	private String mCaption;

	private boolean mBTimeout;
	private Activity mActivity;
	private Handler mPostHandler;

	public interface IFacebookHandlerListener {
		void onPostStatus(final boolean bNative, final int tag,
				final boolean bSuccess, FacebookRequestError error);

		void onPostPhoto(final boolean bNative, final int tag,
				final boolean bSuccess, FacebookRequestError error);

		void onPostRichContent(final boolean bNative, final int tag,
				final boolean bSuccess, FacebookRequestError error);

		void onPostTimeOut(final boolean bNative, final int tag);
	}

	private IFacebookHandlerListener mListener = null;
	private UiLifecycleHelper uiHelper;

	public FacebookHandler(Activity activity, boolean bNativeCall) {
		mActivity = activity;
		mHandlerTag = -1;
		mBNativeCall = bNativeCall;
		mBTimeout = false;

		sessionStatusCallback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPostHandler = new Handler() {
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case MSG_TAG_TIMEOUT:
							mBTimeout = true;
							onPostTimeOut(mHandlerTag);
							break;
						default:
							break;
						}
					}
				};
			}
		});

	}

	public FacebookHandler(Activity activity, boolean bNativeCall,
			Bundle savedInstanceState) {
		mActivity = activity;
		mHandlerTag = -1;
		mBNativeCall = bNativeCall;
		mBTimeout = false;

		sessionStatusCallback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};

		uiHelper = new UiLifecycleHelper(activity, sessionStatusCallback);
		uiHelper.onCreate(savedInstanceState);

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPostHandler = new Handler() {
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case MSG_TAG_TIMEOUT:
							mBTimeout = true;
							onPostTimeOut(mHandlerTag);
							break;
						default:
							break;
						}
					}
				};
			}
		});

	}

	public void setTag(int tag) {
		mHandlerTag = tag;
	}

	public void onCreate(Bundle savedInstanceState) {
		initSession(savedInstanceState);
	}

	public void setListener(IFacebookHandlerListener listener) {
		mListener = listener;
	}

	private void initSession(Bundle savedInstanceState) {
		if (null != mCurrentSession) {
			mCurrentSession.close();
			mCurrentSession = null;
		}

		if (null == savedInstanceState) {
			mCurrentSession = new Session.Builder(mActivity)
					.setTokenCachingStrategy(mTokenCache).build();
			mCurrentSession.addCallback(sessionStatusCallback);
		} else {
			mCurrentSession = Session.restoreSession(mActivity, mTokenCache,
					sessionStatusCallback, savedInstanceState);
            //修正Android 5.*上savedInstanceState不为null的问题
            if(mCurrentSession==null)
            {
                mCurrentSession = new Session.Builder(mActivity)
                .setTokenCachingStrategy(mTokenCache).build();
                mCurrentSession.addCallback(sessionStatusCallback);
            }
		}
	}

	public void onResume() {
		if (mCurrentSession != null) {
			mCurrentSession.addCallback(sessionStatusCallback);
		}
		if (uiHelper != null) {
			uiHelper.onResume();
		}
	}

	public void onPause() {
		if (mCurrentSession != null) {
			mCurrentSession.removeCallback(sessionStatusCallback);
		}
		if (uiHelper != null) {
			uiHelper.onPause();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mCurrentSession != null) {
			mCurrentSession.onActivityResult(mActivity, requestCode,
					resultCode, data);
		}
		if (uiHelper != null) {
			uiHelper.onActivityResult(requestCode, resultCode, data,
					new FacebookDialog.Callback() {
						@Override
						public void onError(
								FacebookDialog.PendingCall pendingCall,
								Exception error, Bundle data) {
							Log.e("Activity", String.format("Error: %s",
									error.toString()));
						}

						@Override
						public void onComplete(
								FacebookDialog.PendingCall pendingCall,
								Bundle data) {
							Log.i("Activity", "Success!");
						}
					});
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		Session.saveSession(mCurrentSession, outState);
		if (uiHelper != null) {
			uiHelper.onSaveInstanceState(outState);
		}
	}

	private void loginForPublish() {
		if (hasPublishPermission())
			return;
		Session.OpenRequest openRequest = new Session.OpenRequest(mActivity);
		openRequest.setPermissions(PERMISSIONS);
		openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
		mCurrentSession.openForPublish(openRequest);
	}

	public void postStatusUpdate(final String text) {
		postStart();
		if (mBTimeout)
			return;

		if (hasPublishPermission()) {
			mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
			mPostHandler.sendEmptyMessageDelayed(MSG_TAG_TIMEOUT, POST_TIMEOUT);

			Request request = Request.newStatusUpdateRequest(mCurrentSession,
					text, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
							if (mBTimeout)
								return;

							mStrPost = null;
							if (null == response.getError()) {
								onPostStatus(mHandlerTag, true , response.getError());
							} else {
								onPostStatus(mHandlerTag, false , response.getError());
							}
						}
					});
			request.executeAsync();
		} else {
			mStrPost = text;
			loginForPublish();
		}
	}

	public void postPhoto(final Bitmap img) {
		if (null == img)
			onPostPhoto(mHandlerTag, false , null);

		postStart();
		if (mBTimeout)
			return;

		if (hasPublishPermission()) {
			mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
			mPostHandler.sendEmptyMessageDelayed(MSG_TAG_TIMEOUT, POST_TIMEOUT);

			Request request = Request.newUploadPhotoRequest(mCurrentSession,
					img, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
							if (mBTimeout)
								return;

							mImgPost = null;
							if (null == response.getError()) {
								onPostPhoto(mHandlerTag, true, response.getError());
							} else {
								onPostPhoto(mHandlerTag, false, response.getError());
							}
						}
					});
			request.executeAsync();
		} else {
			mImgPost = img;
			loginForPublish();
		}
	}

	public void postRichContent(final String message, final String link,
			final Bitmap img) {
		if (null == img)
			onPostRichContent(mHandlerTag, false , null);

		postStart();
		if (mBTimeout)
			return;

		if (hasPublishPermission()) {
			mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
			mPostHandler.sendEmptyMessageDelayed(MSG_TAG_TIMEOUT, POST_TIMEOUT);

			Bundle params = new Bundle();
			if (null != message)
				params.putString("message", message + link);

			byte[] data = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();
			params.putByteArray("photo", data);

			Request request = new Request(mCurrentSession, "me/photos", params,
					HttpMethod.POST);
			request.setCallback(new Request.Callback() {
				@Override
				public void onCompleted(Response response) {
					mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
					if (mBTimeout)
						return;

					mRichContentImg = null;
					if (null == response.getError()) {
						onPostRichContent(mHandlerTag, true, response.getError());
					} else {
						onPostRichContent(mHandlerTag, false, response.getError());
					}
				}
			});
			request.executeAsync();
		} else {
			mRichContentImg = img;
			mRichLink = link;
			mRichMessage = message;
			loginForPublish();
		}
	}

	/**
	 * 杩�竟���璇锋��ヨ���acebook��dialog
	 */
	public void postRichContentDialog(final String message, final String link,
			String mpicture, String name, String caption) {

		postStart();
		if (mBTimeout)
			return;

		if (hasPublishPermission()) {

			Bundle params = new Bundle();
			params.putString("name", mName);
			params.putString("caption", mCaption);
			params.putString("description", message);
			params.putString("link", link);
			params.putString("picture", mPicture);

			WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(mActivity,
					mCurrentSession, params)).setOnCompleteListener(
					new OnCompleteListener() {

						@Override
						public void onComplete(Bundle values,
								FacebookException error) {
							if (error == null) {
								// When the story is posted, echo the success
								// and the post Id.
								final String postId = values
										.getString("post_id");
								if (postId != null) {
									Toast.makeText(mActivity,
											"Posted story, id: " + postId,
											Toast.LENGTH_SHORT).show();
								} else {
									// User clicked the Cancel button
									Toast.makeText(mActivity,
											"Publish cancelled",
											Toast.LENGTH_SHORT).show();
								}
							} else if (error instanceof FacebookOperationCanceledException) {
								// User clicked the "x" button
								Toast.makeText(mActivity, "Publish cancelled",
										Toast.LENGTH_SHORT).show();
							} else {
								// Generic, ex: network error
								Toast.makeText(mActivity,
										"Error posting story",
										Toast.LENGTH_SHORT).show();
							}
						}

					}).build();
			feedDialog.show();
		} else {
			mPicture = mpicture;
			mName = name;
			mCaption = caption;
			mRichLink = link;
			mRichMessage = message;
			loginForPublish();
		}

	}

	/**
	 * ���杩�釜�规��ュ����瑕���ㄧ��规�
	 * 
	 * @param session
	 * @param state
	 * @param exception
	 */
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (session != mCurrentSession)
			return;

		if (state.isOpened()) {
			if (hasPublishPermission()) {
				if (null != mStrPost) {
					postStatusUpdate(mStrPost);
				} else if (null != mImgPost) {
					postPhoto(mImgPost);
				} else if (mPicture != null) {
					postRichContentDialog(mRichMessage, mRichLink, mPicture,
							mName, mCaption);
				} else if (null != mRichContentImg) {
					postRichContent(mRichMessage, mRichLink, mRichContentImg);
				}
			}
		} else if (state.isClosed()) {
			mPostHandler.removeMessages(MSG_TAG_TIMEOUT);
		}
	}

	private boolean mBStartConfirm = false;

	private void postStart() {
		if (mBStartConfirm) {
			return;
		}

		mBStartConfirm = true;
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mActivity, "Post start", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private boolean hasPublishPermission() {
		return mCurrentSession != null && mCurrentSession.isOpened()
				&& mCurrentSession.getPermissions().contains("publish_actions");
	}

	void onPostStatus(int tag, boolean bSuccess, FacebookRequestError error) {
		if (null != mListener)
			mListener.onPostStatus(mBNativeCall, tag, bSuccess , error);
	}

	void onPostPhoto(int tag, boolean bSuccess, FacebookRequestError error) {
		if (null != mListener)
			mListener.onPostPhoto(mBNativeCall, tag, bSuccess, error);
	}

	void onPostRichContent(int tag, boolean bSuccess, FacebookRequestError error) {
		if (null != mListener)
			mListener.onPostRichContent(mBNativeCall, tag, bSuccess, error);
	}

	void onPostTimeOut(int tag) {
		if (null != mListener)
			mListener.onPostTimeOut(mBNativeCall, tag);
	}

	private void loginForRead() {
		Session.OpenRequest openRequest = new Session.OpenRequest(mActivity);
		openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		openRequest.setRequestCode(Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE);
		mCurrentSession.openForRead(openRequest);
	}

	private boolean hasOpened() {
		return mCurrentSession != null && mCurrentSession.isOpened();
	}
}