package com.common.android;


/**
 * 
 * 
 * Listener that notifies when an inventory query operation completes.
 * 
 */
public interface OnQueryListener {
	/**
	 * Called to notify that an inventory query operation completed.
	 * 
	 * @param result
	 *            The result of the operation.
	 * @param inv
	 *            The inventory.
	 */
	public void onQueryInventoryFinished(IapResult result, Inventory inv);
}
