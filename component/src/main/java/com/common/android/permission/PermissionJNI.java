package com.common.android.permission;

import android.app.Activity;

/**
 * Created by luotianqiang on 17/6/21.
 */
public class PermissionJNI {
    private static final String TAG = "PermissionJNI";
    private Activity stContext;
    private static PermissionJNI instance;

    private PermissionJNI(Activity activity) {
        this.stContext = activity;

        try {
            this.nativeInit();
        } catch (UnsatisfiedLinkError var3) {
            ;
        }

    }

    public static void setup(Activity activity) {
        if(instance == null) {
            instance = new PermissionJNI(activity);
        }

    }

    public static PermissionJNI getInstance() {
        return instance;
    }

    public void requestRuntimePermissions(final int requestCode, long permisson) {
        if(this.stContext != null) {
            String[] permissons = PermissionUtils.permissionIndex2Values(permisson);
            PermissionHelper.getInstance().requestPermission(this.stContext, new PermissionListener() {
                public void onPermissionGranted(String... perssions) {
                    try {
                        PermissionJNI.this.nativeNotifyGranted(requestCode, true);
                    } catch (UnsatisfiedLinkError var3) {
                        ;
                    }

                }

                public void onPermissionDenied() {
                    try {
                        PermissionJNI.this.nativeNotifyGranted(requestCode, false);
                    } catch (UnsatisfiedLinkError var2) {
                        ;
                    }

                }
            }, permissons);
        }

    }

    private native void nativeInit();

    private native void nativeNotifyGranted(int var1, boolean var2);
}
