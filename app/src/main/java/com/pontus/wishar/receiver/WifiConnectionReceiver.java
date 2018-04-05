package com.pontus.wishar.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.pontus.wishar.Constants;
import com.pontus.wishar.data.DescCorr;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.data.WifiDescCache;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.service.EntryIntentService;
import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.storage.CheckStorage;
import com.pontus.wishar.storage.db.WisharDB;
import com.pontus.wishar.wifi.WifiAdmin;

import timber.log.Timber;

import static android.content.Context.ACTIVITY_SERVICE;

public class WifiConnectionReceiver extends BroadcastReceiver {

    private static final String EXCEPT_SSID_1 = "<unknown ssid>" , EXCEPT_SSID_2 = "0x";
    private static boolean isConnected = false;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if(!WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
            //要判斷是不是系統給的action,否則可能會被第三方應用傳送intent攻擊
            return;
        }

        if (!isConnected && isConnectedIntent(intent)) {
            //如果wifi目前沒連線,而且intent內容是wifi連線成功
            String SSID = WifiAdmin.getInstance(context).getWifiInfo().getSSID().replace("\"", "");

            //wifi可能會在開啟或是關閉的時候出現這兩個value，兩個值都是錯誤的，直接擋掉
            if(SSID.equals(EXCEPT_SSID_1) || SSID.equals(EXCEPT_SSID_2))
                return;

            isConnected = true;
            Timber.d("onReceive: Change- >CONNECT SSID:%s, isConnected: %b",SSID,isConnected);
            //判斷是不是Wishar的服務範圍,不是就掰掰
            DescCorr descCorr = WisharDB.getDB(context).descCorrespDao().getDescCorr(SSID);

            if (descCorr == null) {
                //這個公共wifi Wishar不支援
                Timber.d("%s not support",SSID);
                return;
            }
            String wifiDescName = descCorr.getWifiDescFileName();

            WifiDesc wifiDesc = WifiDescCache.getCache().getWifiDesc(context,wifiDescName);
            Timber.d("onReceive: wifiDescName:%s",wifiDescName);
            if(wifiDesc.isNeedLogin()){
                boolean isOurService =  AccountStorage.isAccountExist(context,wifiDescName);
                if(!isOurService)
                    return;
            }
            else if(!CheckStorage.isCheckStatusExist(context,wifiDescName)){
                return;
            }

            if(isServiceRunning(context)){
                Timber.d("onReceive: Service running");
                return;
            }

            NotificationCenter.setSsid(SSID);

            Intent i = new Intent(context, EntryIntentService.class);
            i.putExtra(Constants.EXTRA_WIFI_DESC_NAME,wifiDescName);
            Timber.d("onReceive: Start Service");
            context.startService(i);
        }
        else if (isConnected && isDisconnectedIntent(intent)) {
            //如果wifi已經連上了,而且intent內容是wifi斷線
            isConnected = false;
            if(isServiceRunning(context)){
                Timber.d("onReceive: Stop Service");
                context.stopService(new Intent(context,EntryIntentService.class));
            }
            Timber.d("onReceive: Change->DISCONNECT isConnected: %b",isConnected);
            NotificationCenter.getInstance(context).cleanNotify();
        }
    }

    private boolean isConnectedIntent(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private boolean isDisconnectedIntent(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {
            NetworkInfo.State state = networkInfo.getState();
            if ((state.equals(NetworkInfo.State.DISCONNECTING) || state.equals(NetworkInfo.State.DISCONNECTED)) && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            return false;
        }
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
        if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING) {
            return true;
        }
        return false;
    }

    private boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.pontus.wishar.service.EntryIntentService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
