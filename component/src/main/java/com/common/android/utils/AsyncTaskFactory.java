package com.common.android.utils;


import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author LiYang
 * @version 1.00
 * @copyright 2009-2012, Chengdu Tianfu Software Park Co., Ltd.
 * @CreateTime 2012-5-18 上午11:00:42 <br/>
 * @Description AsyncTaskFactory.java,<br/>
 * @History ..Editor..... Time................ Mantis No......Operation....Description......<br/>
 * 
 **/
public class AsyncTaskFactory {
	private final static String TAG = "AsyncTaskFactory # init";

	public static class AsyncResult {
		public Bitmap bmp;
		public int tag;
	}

	/**
	 * 说明：异步线程中的操作
	 * 
	 * @author liyang
	 * 
	 */
	public static interface IProgressCallback {
		public void progressCallback(AsyncResult result);
	}

	/**
	 * 说明 ： 异步线程执行完后会调用此方法
	 * 
	 * @author liyang
	 * 
	 */
	public static interface IResultCallback {
		public void resultCallback(AsyncResult result);
	}

	private class AsyncDownLoaderTask extends android.os.AsyncTask<String, Integer, AsyncResult> {
		/** synchronous Callback */
		private IResultCallback _resultCallback;
		/** ProgressCallback */
		private IProgressCallback _progressCallback;

		@Override
		protected AsyncResult doInBackground(String... params) {
			final AsyncResult result = new AsyncResult();

			if (_progressCallback != null)
				_progressCallback.progressCallback(result);
			return result;
		}

		@Override
		protected void onPostExecute(AsyncResult _result) {
			if (_resultCallback != null)
				_resultCallback.resultCallback(_result);
		}

		/**
		 * 方法功能描述
		 * 
		 * @param values
		 */

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * 
		 * 方法功能描述
		 * 
		 * @param proCallback
		 *            耗时操作的类 ，实现IProgressCallback接口
		 * @param callback
		 *            结果处理类，实现IResultCallback接口
		 * @param params
		 *            参数
		 */
		public final void execute(IProgressCallback proCallback, IResultCallback callback, String... params) {
			_progressCallback = proCallback;
			_resultCallback = callback;
			execute(params);
		}

	}

	private AsyncTaskFactory() {
	}

	public static final AsyncTaskFactory getNewInstance() {
		return new AsyncTaskFactory();
	}

	public final void addSyncTask(IProgressCallback proCallback, IResultCallback resCallback, String... params) {
		new AsyncDownLoaderTask().execute(proCallback, resCallback, params);
	}

}
