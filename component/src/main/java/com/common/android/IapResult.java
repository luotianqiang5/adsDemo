/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.android;

import com.common.android.iap_googleplay.IapHelper;

/**
 * Represents the result of an in-app billing operation. A result is composed of
 * a response code (an integer) and possibly a message (String). You can get
 * those by calling {@link #getResponse} and {@link #getMessage()},
 * respectively. You can also inquire whether a result is a success or a failure
 * by calling {@link #isSuccess()} and {@link #isFailure()}.
 */
public class IapResult {
	int mResponse;
	String mMessage;

	public IapResult(int response, String message) {
		mResponse = response;
		if (message == null || message.trim().length() == 0) {
			mMessage = IapHelper.getResponseDesc(response);
		} else {
			mMessage = message + " (response: " + IapHelper.getResponseDesc(response) + ")";
		}
	}

	public int getResponse() {
		return mResponse;
	}

	/**
	 * 获取失败的简要提示信息
	 * @return
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * 操作是否成功,只有当responseCode为IapHelper.BILLING_RESPONSE_RESULT_OK时,操作才为成功,其他都未失败
	 * @return
	 */
	public boolean isSuccess() {
		return mResponse == ResponseCode.BILLING_RESPONSE_RESULT_OK;
	}

	/**
	 * 操作是否失败,当responseCode不为IapHelper.BILLING_RESPONSE_RESULT_OK时,操作失败
	 * 等同于{@link #isSuccess()}
	 * @return
	 */
	public boolean isFailure() {
		return !isSuccess();
	}

	public String toString() {
		return "IabResult: " + getMessage();
	}
}
