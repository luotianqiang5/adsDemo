package com.common.android;

/**
 * Callback that notifies when a multi-item consumption operation finishes.
 */
public interface OnConsumeListener {
	

	/**
	 * Called to notify that a consumption of multiple items has finished.
	 * 
	 * @param purchases
	 *            The purchases that were (or were to be) consumed.
	 * @param results
	 *            The results of each consumption operation, corresponding
	 *            to each sku.
	 */
	public void onConsumeFinished(PurchaseBean purchase, IapResult result);
}