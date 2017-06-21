package com.common.android.permission;

/**
 * Created by luotianqiang on 17/6/21.
 */
public interface PermissionListener {
    void onPermissionGranted(String... var1);

    void onPermissionDenied();
}
