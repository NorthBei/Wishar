package com.pontus.wishar;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.storage.AssetsStorage;

import timber.log.Timber;


public class WisharApp extends Application {

    private final String TAG = getClass().getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();

        //[Android dev] 使用 BuildConfig.DEBUG 區分 release 和 debug 版本 http://song.logdown.com/posts/247011
        if(BuildConfig.DEBUG){
            //checkWifiResource();
            Stetho.initializeWithDefaults(this);
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void checkWifiResource(){
        //讀assets/wifiDesc/*.json
        String[] wifiDescFiles = AssetsStorage.getWifiDescList(getBaseContext());

        for (String wifiName : wifiDescFiles) {
            AssetsStorage as = new AssetsStorage(getBaseContext());
            //看看各個wifi有沒有相對應的描述檔
            WifiDesc wifiDesc = as.getWifiDescObj(wifiName);
            String SSID = wifiDesc.getSsid();
            Log.d(TAG, "checkWifiResource: SSID:"+SSID);

            if(wifiDesc == null){
                throw new AssertionError(SSID+":沒有描述檔案");
            }
            //如果有多種帳號分類，去確認string.xml內有定義名稱
            if(wifiDesc.getLoginType().size()>1){
                for (WifiDesc.LoginType option: wifiDesc.getLoginType()) {
                    String displayNameKey = option.getDisplayName();
                    //if not found string key can be mapped in string.xml will throw error
                    int id = getResources().getIdentifier(displayNameKey, "string", getPackageName());
                    if(id == 0){
                        //找不到該資源
                        throw new AssertionError(SSID+":"+displayNameKey+"沒有在string.xml內找到對應到的key/value");
                    }
                }
            }
        }
    }

}
