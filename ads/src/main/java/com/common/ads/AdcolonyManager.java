package com.common.ads;

import android.app.Activity;

import com.adcolony.sdk.AdColony;

/**
 * Created by luotianqiang on 17/9/16.
 */
public class AdcolonyManager {
    private static AdcolonyManager instance;
    public static AdcolonyManager getInstance(){
        if(instance == null)
            instance = new AdcolonyManager();
        return  instance;
    }

    private String APP_ID;
    private String[] ZONE_ID;
    private Activity contextActivry;
    private boolean isConfig;

    public void config(Activity var, String app_id,String... zone_id){
        if(!isConfig) {
            contextActivry = var;
            APP_ID = app_id;
            ZONE_ID = zone_id;
            isConfig = true;
            AdColony.configure(contextActivry, APP_ID, ZONE_ID);
        }
    }
}
