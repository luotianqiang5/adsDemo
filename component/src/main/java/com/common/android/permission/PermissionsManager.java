//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.common.android.permission;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.common.android.permission.Permissions;
import com.common.android.permission.PermissionsResultAction;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PermissionsManager {
    private static final String TAG = PermissionsManager.class.getSimpleName();
    private final Set<String> mPendingRequests = new HashSet(1);
    private final Set<String> mPermissions = new HashSet(1);
    private final List<PermissionsResultAction> mPendingActions = new ArrayList(1);
    private static PermissionsManager mInstance = null;

    public static PermissionsManager getInstance() {
        if(mInstance == null) {
            mInstance = new PermissionsManager();
        }

        return mInstance;
    }

    private PermissionsManager() {
        this.initializePermissionsMap();
    }

    private synchronized void initializePermissionsMap() {
        Field[] fields = permission.class.getFields();
        Field[] arr$ = fields;
        int len$ = fields.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Field field = arr$[i$];
            String name = null;

            try {
                name = (String)field.get("");
            } catch (IllegalAccessException var8) {
                Log.e(TAG, "Could not access field", var8);
            }

            this.mPermissions.add(name);
        }

    }

    @NonNull
    private synchronized String[] getManifestPermissions(@NonNull Activity activity) {
        PackageInfo packageInfo = null;
        ArrayList list = new ArrayList(1);

        try {
            Log.d(TAG, activity.getPackageName());
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException var9) {
            Log.e(TAG, "A problem occurred when retrieving permissions", var9);
        }

        if(packageInfo != null) {
            String[] permissions = packageInfo.requestedPermissions;
            if(permissions != null) {
                String[] arr$ = permissions;
                int len$ = permissions.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    String perm = arr$[i$];
                    Log.d(TAG, "Manifest contained permission: " + perm);
                    list.add(perm);
                }
            }
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    private synchronized void addPendingAction(@NonNull String[] permissions, @Nullable PermissionsResultAction action) {
        if(action != null) {
            Log.d(TAG, "---------addPendingAction-----action = " + action);
            action.registerPermissions(permissions);
            this.mPendingActions.add(action);
        }
    }

    private synchronized void removePendingAction(@Nullable PermissionsResultAction action) {
        Log.d(TAG, "--------removePendingAction()--------");
        Iterator iterator = this.mPendingActions.iterator();

        while(true) {
            PermissionsResultAction weakRef;
            do {
                if(!iterator.hasNext()) {
                    return;
                }

                weakRef = (PermissionsResultAction)iterator.next();
            } while(weakRef != action && weakRef != null);

            iterator.remove();
        }
    }

    public synchronized boolean hasPermission(@Nullable Context context, @NonNull String permission) {
        return context != null && (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED || !this.mPermissions.contains(permission));
    }

    public synchronized boolean hasAllPermissions(@Nullable Context context, @NonNull String[] permissions) {
        if(context == null) {
            return false;
        } else {
            boolean hasAllPermissions = true;
            String[] arr$ = permissions;
            int len$ = permissions.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String perm = arr$[i$];
                hasAllPermissions &= this.hasPermission(context, perm);
            }

            return hasAllPermissions;
        }
    }

    public synchronized void requestAllManifestPermissionsIfNecessary(@Nullable Activity activity, @Nullable PermissionsResultAction action) {
        if(activity != null) {
            String[] perms = this.getManifestPermissions(activity);
            this.requestPermissionsIfNecessaryForResult(activity, perms, action);
        }
    }

    public synchronized void requestPermissionsIfNecessaryForResult(@Nullable Activity activity, @NonNull String[] permissions, @Nullable PermissionsResultAction action) {
        if(activity == null) {
            Log.d(TAG, "requestPermissionsIfNecessaryForResult,activity is null");
        } else {
            this.addPendingAction(permissions, action);
            if(VERSION.SDK_INT < 23) {
                this.doPermissionWorkBeforeAndroidM(activity, permissions, action);
            } else {
                List permList = this.getPermissionsListToRequest(activity, permissions, action);
                if(permList.isEmpty()) {
                    this.removePendingAction(action);
                } else {
                    String[] permsToRequest = (String[])permList.toArray(new String[permList.size()]);
                    this.mPendingRequests.addAll(permList);
                    String[] arr$ = permsToRequest;
                    int len$ = permsToRequest.length;

                    for(int i$ = 0; i$ < len$; ++i$) {
                        String perm = arr$[i$];
                        this.setShowRequestPermissionTag(activity, perm);
                    }

                    ActivityCompat.requestPermissions(activity, permsToRequest, 1);
                }
            }

        }
    }

    public synchronized void requestPermissionsIfNecessaryForResult(@NonNull Fragment fragment, @NonNull String[] permissions, @Nullable PermissionsResultAction action) {
        FragmentActivity activity = fragment.getActivity();
        if(activity != null) {
            this.addPendingAction(permissions, action);
            if(VERSION.SDK_INT < 23) {
                this.doPermissionWorkBeforeAndroidM(activity, permissions, action);
            } else {
                List permList = this.getPermissionsListToRequest(activity, permissions, action);
                if(permList.isEmpty()) {
                    this.removePendingAction(action);
                } else {
                    String[] permsToRequest = (String[])permList.toArray(new String[permList.size()]);
                    this.mPendingRequests.addAll(permList);
                    fragment.requestPermissions(permsToRequest, 1);
                }
            }

        }
    }

    public synchronized void notifyPermissionsChange(@NonNull String[] permissions, @NonNull int[] results) {
        Log.d(TAG, "-----notifyPermissionsChange-------");
        int size = permissions.length;
        if(results.length < size) {
            size = results.length;
        }

        Log.d(TAG, "-----notifyPermissionsChange------- permissions size = " + size);
        Iterator iterator = this.mPendingActions.iterator();
        Log.d(TAG, "-----notifyPermissionsChange------- permissions mPendingActions = " + this.mPendingActions.toString());

        while(true) {
            while(iterator.hasNext()) {
                PermissionsResultAction n = (PermissionsResultAction)iterator.next();
                Log.d(TAG, "-----notifyPermissionsChange------- action = " + n);

                for(int n1 = 0; n1 < size; ++n1) {
                    if(n == null || n.onResult(permissions[n1], results[n1])) {
                        Log.d(TAG, "-----notifyPermissionsChange------- permission = " + permissions[n1]);
                        Log.d(TAG, "-----notifyPermissionsChange------- result = " + results[n1]);
                        iterator.remove();
                        break;
                    }
                }
            }

            for(int var7 = 0; var7 < size; ++var7) {
                this.mPendingRequests.remove(permissions[var7]);
            }

            return;
        }
    }

    private void doPermissionWorkBeforeAndroidM(@NonNull Activity activity, @NonNull String[] permissions, @Nullable PermissionsResultAction action) {
        Log.d(TAG, "------doPermissionWorkBeforeAndroidM()------");
        String[] arr$ = permissions;
        int len$ = permissions.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String perm = arr$[i$];
            if(action != null) {
                if(!this.mPermissions.contains(perm)) {
                    action.onResult(perm, Permissions.NOT_FOUND);
                } else if(ActivityCompat.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED) {
                    action.onResult(perm, Permissions.DENIED);
                } else {
                    action.onResult(perm, Permissions.GRANTED);
                }
            }
        }

    }

    @NonNull
    private List<String> getPermissionsListToRequest(@NonNull Activity activity, @NonNull String[] permissions, @Nullable PermissionsResultAction action) {
        ArrayList permList = new ArrayList(permissions.length);
        String[] arr$ = permissions;
        int len$ = permissions.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String perm = arr$[i$];
            if(!this.mPermissions.contains(perm)) {
                if(action != null) {
                    Log.d(TAG, "-------getPermissionsListToRequestNOT_FOUND");
                    action.onResult(perm, Permissions.NOT_FOUND);
                }
            } else if(ActivityCompat.checkSelfPermission(activity, perm) !=  PackageManager.PERMISSION_GRANTED) {
                if(!this.mPendingRequests.contains(perm)) {
                    Log.d(TAG, "-------getPermissionsListToRequest-----add to list->" + perm);
                    permList.add(perm);
                }
            } else if(action != null) {
                Log.d(TAG, "-------getPermissionsListToRequestGRANTED");
                action.onResult(perm, Permissions.GRANTED);
            }
        }

        return permList;
    }

    private synchronized void setShowRequestPermissionTag(@Nullable Activity activity, @NonNull String permission) {
        if(activity != null && permission != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
            boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            sharedPreferences.edit().putBoolean(permission, flag).commit();
        }
    }

    public synchronized boolean getShowRequestPermissionTag(@Nullable Activity activity, @NonNull String permission) {
        if(activity != null && permission != null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
            boolean flag = sharedPreferences.getBoolean(permission, false);
            return flag && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        } else {
            return true;
        }
    }

    public synchronized boolean getShowRequestPermissionTag(@Nullable Activity activity, @NonNull String[] permissions) {
        if(activity != null && permissions != null) {
            boolean flag = true;
            String[] arr$ = permissions;
            int len$ = permissions.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String perm = arr$[i$];
                flag &= this.getShowRequestPermissionTag(activity, perm);
            }

            return flag;
        } else {
            return true;
        }
    }
}
