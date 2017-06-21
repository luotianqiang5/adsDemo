//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.common.android.permission;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
//import com.common.android.analyticscenter.listener.UnityMessageListener;
import com.common.android.permission.PermissionListener;
import com.common.android.permission.PermissionUtils;
import com.common.android.permission.PermissionsManager;
import com.common.android.permission.PermissionsResultAction;

public class PermissionHelper {
    private static String CALENDAR = "Calendar";
    private static String READ_CALENDAR = "Read Calendar";
    private static String WRITE_CALENDAR = "Write Calendar";
    private static String CAMERA = "Camera";
    private static String CONTACTS = "Contacts";
    private static String READ_CONTACTS = "Read Contacts";
    private static String WRITE_CONTACTS = "Write Contacts";
    private static String GET_ACCOUNTS = "Get Accounts";
    private static String LOCATION = "Location";
    private static String ACCESS_FINE_LOCATION = "Access Fine Location";
    private static String ACCESS_COARSE_LOCATION = "Access Coarse Location";
    private static String MICROPHONE = "Microphone";
    private static String RECORD_AUDIO = "Record Audio";
    private static String PHONE = "Phone";
    private static String READ_PHONE_STATE = "Read Phone State";
    private static String CALL_PHONE = "Call Phone";
    private static String READ_CALL_LOG = "Read Call Log";
    private static String WRITE_CALL_LOG = "Write Call Log";
    private static String ADD_VOICEMAIL = "Add Voicemail";
    private static String USE_SIP = "Use SIP";
    private static String PROCESS_OUTGOING_CALLS = "Process Outgoing Calls";
    private static String BODY_SENSORS = "Body Sensors";
    private static String SMS = "SMS";
    private static String SEND_SMS = "Send SMS";
    private static String READ_SMS = "Read SMS";
    private static String RECEIVE_SMS = "Receive SMS";
    private static String RECEIVE_WAP_PUSH = "Receive Wap Push";
    private static String RECEIVE_MMS = "Receive MMS";
    private static String READ_CELL_BROADCASTS = "Read Cell Broadcasts";
    private static String STORAGE = "Storage";
    private static String READ_EXTERNAL_STORAGE = "Read External Storage";
    private static String WRITE_EXTERNAL_STORAGE = "Write External Storage";
    private PermissionListener permissionListener;
    private static PermissionHelper instance;
    private Activity activity;
    private String[] permissions;
    public static final int REQ_FLAG = 1;
    private String gameObjectName;
  //  private UnityMessageListener unityMessageListener;
    private Activity context;

    public PermissionHelper() {
    }

    public static PermissionHelper getInstance() {
        if(instance == null) {
            instance = new PermissionHelper();
        }

        return instance;
    }

    public Activity getContext() {
        return this.context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public synchronized void requestPermission(final Activity activity, final PermissionListener permissionListener, final String... permissions) {
        this.activity = activity;
        this.permissionListener = permissionListener;
        this.permissions = permissions;
        if(this.needShowGuideUI(activity, permissions)) {
            this.showGuidUI(activity, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PermissionHelper.this.gotoPermissionSettingActivity();
                }
            }, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(permissionListener != null) {
                        permissionListener.onPermissionDenied();
                    }

                }
            }, permissions);
        } else {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(activity, permissions, new PermissionsResultAction() {
                public void onGranted() {
                    if(permissionListener != null) {
                        permissionListener.onPermissionGranted(permissions);
                    }

                }

                public void onDenied(String permission) {
                    PermissionHelper.this.showGuidUI(activity, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionHelper.this.gotoPermissionSettingActivity();
                        }
                    }, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(permissionListener != null) {
                                permissionListener.onPermissionDenied();
                            }

                        }
                    }, permissions);
                }
            });
        }
    }

    protected synchronized void showGuidUI(final Activity activity, final OnClickListener okListener, final OnClickListener cancelListener, String... permissions) {
        if(permissions != null) {
              String message = "You need to grant access to ";
            String[] msg = permissions;
            int len$ = permissions.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String permission = msg[i$];
                String strTmp = getPermissionStr(permission);
                if(!message.contains(strTmp) && strTmp != null) {
                    message = message + strTmp + ",";
                }
            }

            if(message.endsWith(",")) {
                int var11 = message.lastIndexOf(",");
                message = message.substring(0, var11) + " to use this feature.";
            }
            final String ms = message;
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    (new Builder(activity)).setTitle("Attention!").setMessage(ms).setPositiveButton("Setting", okListener).setNegativeButton("Cancel", cancelListener).setCancelable(false).create().show();
                }
            });
        }

    }

    private static String getPermissionStr(String permission) {
        return !"android.permission.READ_CALENDAR".equals(permission) && !"android.permission.WRITE_CALENDAR".equals(permission)?("android.permission.CAMERA".equals(permission)?CAMERA:(!"android.permission.READ_CONTACTS".equals(permission) && !"android.permission.WRITE_CONTACTS".equals(permission) && !"android.permission.GET_ACCOUNTS".equals(permission)?(!"android.permission.ACCESS_COARSE_LOCATION".equals(permission) && !"android.permission.ACCESS_FINE_LOCATION".equals(permission)?("android.permission.RECORD_AUDIO".equals(permission)?MICROPHONE:(!"android.permission.READ_PHONE_STATE".equals(permission) && !"android.permission.CALL_PHONE".equals(permission) && !"android.permission.READ_CALL_LOG".equals(permission) && !"android.permission.WRITE_CALL_LOG".equals(permission) && !"com.android.voicemail.permission.ADD_VOICEMAIL".equals(permission) && !"android.permission.USE_SIP".equals(permission) && !"android.permission.PROCESS_OUTGOING_CALLS".equals(permission)?("android.permission.BODY_SENSORS".equals(permission)?BODY_SENSORS:(!"android.permission.SEND_SMS".equals(permission) && !"android.permission.READ_SMS".equals(permission) && !"android.permission.RECEIVE_MMS".equals(permission) && !"android.permission.RECEIVE_SMS".equals(permission) && !"android.permission.RECEIVE_WAP_PUSH".equals(permission)?(!"android.permission.READ_EXTERNAL_STORAGE".equals(permission) && !"android.permission.WRITE_EXTERNAL_STORAGE".equals(permission)?"":STORAGE):SMS)):PHONE)):LOCATION):CONTACTS)):CALENDAR;
    }

    private synchronized boolean needShowGuideUI(Activity activity, String... permissions) {
        boolean need = false;
        need = PermissionsManager.getInstance().getShowRequestPermissionTag(activity, permissions) && !PermissionsManager.getInstance().hasAllPermissions(activity, permissions);
        return need;
    }

    public synchronized void notifyPermissionsChange(String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    public synchronized void checkPermissionsGranted(int requestCode) {
        if(this.permissionListener != null && this.permissions != null && requestCode == 1) {
            if(PermissionsManager.getInstance().hasAllPermissions(this.activity, this.permissions)) {
                this.permissionListener.onPermissionGranted(this.permissions);
            } else {
                this.permissionListener.onPermissionDenied();
            }
        }

    }

    public synchronized void gotoPermissionSettingActivity() {
        if(this.activity != null) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            Uri uri = Uri.fromParts("package", this.activity.getPackageName(), (String)null);
            intent.setData(uri);
            this.activity.startActivityForResult(intent, 1);
        }

    }

    public String getGameObjectName() {
        return this.gameObjectName;
    }

    public void setGameObjectName(String gameObjectName) {
        this.gameObjectName = gameObjectName;
    }

   // public UnityMessageListener getUnityMessageListener() {
   //     return this.unityMessageListener;
   // }

//    public void setUnityMessageListener(UnityMessageListener unityMessageListener) {
//        this.unityMessageListener = unityMessageListener;
//    }

//    public void requestRuntimePermissionsForUnity3D(final int requestCode, long permisson) {
//        if(this.context != null) {
//            String[] permissons = PermissionUtils.permissionIndex2Values(permisson);
//            getInstance().requestPermission(this.context, new PermissionListener() {
//                public void onPermissionGranted(String... perssions) {
////                    if(PermissionHelper.this.unityMessageListener != null) {
////                        PermissionHelper.this.unityMessageListener.sendMessage(PermissionHelper.this.getGameObjectName(), "onPermissionGranted", String.valueOf(requestCode));
////                    }
//
//                }
//
//                public void onPermissionDenied() {
////                    if(PermissionHelper.this.unityMessageListener != null) {
////                        PermissionHelper.this.unityMessageListener.sendMessage(PermissionHelper.this.getGameObjectName(), "onPermissionDenied", String.valueOf(requestCode));
////                    }
//
//                }
//            }, permissons);
//        }
//
//    }
}
