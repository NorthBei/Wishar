package com.pontus.wishar.wifi;

import android.content.Context;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.storage.AccountStorage;

import java.io.IOException;

public abstract class LoginHandler{

    protected Context context;
    protected WifiDesc wifiDesc;
    protected AccountStorage accountStorage;

    public LoginHandler(Context context,WifiDesc wifiDesc){
        this.context = context;
        this.wifiDesc = wifiDesc;
        accountStorage = new AccountStorage(context,wifiDesc.getScriptName());
    }

    public abstract void onNetworkUnavailable(String result) throws IOException;

    protected AccountStorage getAccountStorage(){
        return accountStorage;
    }

    public Context getContext() {
        return context;
    }

    public String getSsid() {
        return wifiDesc.getSsid();
    }

    public WifiDesc getWifiDesc() {
        return wifiDesc;
    }
}