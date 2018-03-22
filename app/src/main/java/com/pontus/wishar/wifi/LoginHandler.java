package com.pontus.wishar.wifi;

import android.content.Context;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.storage.AccountStorage;

public abstract class LoginHandler{

    protected Context context;
    protected String SSID;
    protected WifiDesc wifiDesc;
    protected AccountStorage accountStorage;

    public LoginHandler(Context context,String SSID,WifiDesc wifiDesc){
        this.context = context;
        this.SSID = SSID;
        this.wifiDesc = wifiDesc;
        accountStorage = new AccountStorage(context,SSID);
    }

    public abstract void onNetworkUnavailable(String result);

    protected AccountStorage getAccountStorage(){
        return accountStorage;
    }

    public Context getContext() {
        return context;
    }

    public String getSSID() {
        return SSID;
    }

    public WifiDesc getWifiDesc() {
        return wifiDesc;
    }
}