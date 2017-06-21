//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.common.android.permission;

import java.util.ArrayList;

public class PermissionUtils {
    public PermissionUtils() {
    }

    public static String[] permissionIndex2Values(long permissionIndex) {
        ArrayList tmpList = new ArrayList();
        long type = PermissionType.kReadCalendar;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.READ_CALENDAR");
        }

        type = PermissionType.kWriteCalendar;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.WRITE_CALENDAR");
        }

        type = PermissionType.kCamera;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.CAMERA");
        }

        type = PermissionType.kReadContacts;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.READ_CONTACTS");
        }

        type = PermissionType.kWriteContacts;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.WRITE_CONTACTS");
        }

        type = PermissionType.kGetAccounts;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.GET_ACCOUNTS");
        }

        type = PermissionType.kAccessFineLocation;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.ACCESS_FINE_LOCATION");
        }

        type = PermissionType.kAccessCoraseLocation;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.ACCESS_COARSE_LOCATION");
        }

        type = PermissionType.kRecordAudio;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.RECORD_AUDIO");
        }

        type = PermissionType.kReadPhoneState;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.READ_PHONE_STATE");
        }

        type = PermissionType.kCallPhone;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.CALL_PHONE");
        }

        type = PermissionType.kReadCallLog;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.READ_CALL_LOG");
        }

        type = PermissionType.kWriteCallLog;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.WRITE_CALL_LOG");
        }

        type = PermissionType.kAddVoicemail;
        if((type & permissionIndex) == type) {
            tmpList.add("com.android.voicemail.permission.ADD_VOICEMAIL");
        }

        type = PermissionType.kUseSIP;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.USE_SIP");
        }

        type = PermissionType.kProcessOutgoingCalls;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.PROCESS_OUTGOING_CALLS");
        }

        type = PermissionType.kBodySensors;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.BODY_SENSORS");
        }

        type = PermissionType.kSendSMS;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.SEND_SMS");
        }

        type = PermissionType.kReadSMS;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.READ_SMS");
        }

        type = PermissionType.kReceiveSMS;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.RECEIVE_SMS");
        }

        type = PermissionType.kReceiveWapPush;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.RECEIVE_WAP_PUSH");
        }

        type = PermissionType.kReceiveMMS;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.RECEIVE_MMS");
        }

        type = PermissionType.kReadExternalStorage;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.READ_EXTERNAL_STORAGE");
        }

        type = PermissionType.kWriteExternalStorage;
        if((type & permissionIndex) == type) {
            tmpList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }

        if(tmpList != null && !tmpList.isEmpty()) {
            String[] permissions = new String[tmpList.size()];

            for(int i = 0; i < tmpList.size(); ++i) {
                permissions[i] = (String)tmpList.get(i);
            }

            return permissions;
        } else {
            return null;
        }
    }
}
