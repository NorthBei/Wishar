package com.pontus.wishar.data;

import android.content.Context;

import com.pontus.wishar.storage.AssetsStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NorthBei on 2018/3/26.
 */

public class WifiDescCache {

    private static WifiDescCache wifiDescCache;
    private Map<String,WifiDesc> cache;

    private WifiDescCache(){
        cache = new HashMap<>();
    }
    public static WifiDescCache getCache() {
        if(wifiDescCache == null){
            wifiDescCache = new WifiDescCache();
        }
        return wifiDescCache;
    }

    public WifiDesc getWifiDesc(Context context,String wifiDescName){
        if(cache.containsKey(wifiDescName))
            return get(wifiDescName);
        AssetsStorage as = new AssetsStorage(context);
        WifiDesc wifiDesc = as.getWifiDescObj(wifiDescName);
        put(wifiDescName,wifiDesc);
        return wifiDesc;
    }

    public WifiDesc get(String wifiDescName){
        return cache.get(wifiDescName);
    }

    public void put(String wifiDescName,WifiDesc wifiDesc){
        cache.put(wifiDescName,wifiDesc);
    }
}
