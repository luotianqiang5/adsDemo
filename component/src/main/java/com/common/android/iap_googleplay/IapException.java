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

package com.common.android.iap_googleplay;

import com.common.android.IapResult;

/**
 * Exception thrown when something went wrong with in-app billing.
 * An IabException has an associated IabResult (an error).
 * To get the IAB result that caused this exception to be thrown,
 * call {@link #getResult()}.
 */
public class IapException extends Exception {
    IapResult mResult;

    public IapException(IapResult r) {
        this(r, null);
    }
    public IapException(int response, String message) {
        this(new IapResult(response, message));
    }
    public IapException(IapResult r, Exception cause) {
        super(r.getMessage(), cause);
        mResult = r;
    }
    public IapException(int response, String message, Exception cause) {
        this(new IapResult(response, message), cause);
    }

    /** Returns the IAB result (error) that this exception signals. */
    public IapResult getResult() { return mResult; }
}