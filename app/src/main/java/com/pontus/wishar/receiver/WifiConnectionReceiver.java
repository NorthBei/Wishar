package com.pontus.wishar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.pontus.wishar.Constants;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.service.WISPrLoginService;
import com.pontus.wishar.service.WebLoginService;
import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.storage.AssetsStorage;
import com.pontus.wishar.wifi.WifiAdmin;

import static android.net.wifi.WifiManager.EXTRA_NEW_STATE;
import static android.net.wifi.WifiManager.SUPPLICANT_STATE_CHANGED_ACTION;

public class WifiConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = WifiConnectionReceiver.class.getSimpleName();
    private static final String EXCEPT_SSID_1 = "<unknown ssid>" , EXCEPT_SSID_2 = "0x";
    private static boolean isConnected = false;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.equals(SUPPLICANT_STATE_CHANGED_ACTION)) {
            SupplicantState state =  intent.getParcelableExtra(EXTRA_NEW_STATE);

            if (state != null && state == SupplicantState.DISCONNECTED) {
                //訊號斷線
                //context.stopService(new Intent(context, MyService.class));
            }
            return;
        }

        if (!isConnected && isConnectedIntent(intent)) {
            //如果wifi目前沒連線,而且intent內容是wifi連線成功
            String SSID = WifiAdmin.getInstance(context).getWifiInfo().getSSID().replace("\"", "");

            //wifi可能會在開啟或是關閉的時候出現這兩個value，兩個值都是錯誤的，直接擋掉
            if(SSID.equals(EXCEPT_SSID_1) || SSID.equals(EXCEPT_SSID_2))
                return;

            isConnected = true;
            Log.d(TAG, "onReceive: CONNECT SSID:"+SSID+", Change isConnected: "+isConnected);

            //判斷是不是Wishar的服務範圍,不是就掰掰
            boolean isOurService =  AccountStorage.isAccountExist(context,SSID);
            Log.d(TAG, "loginWifi:"+SSID+", isOurService:"+isOurService);
            //isOurService = true;
            if (!isOurService)
                return;

            //讀描述檔，這邊主要是拿type來判斷要啟動哪一個service
            AssetsStorage as = new AssetsStorage(context);
            WifiDesc wifiDesc = as.readFileToJson(SSID+".json", WifiDesc.class);

            Class<?> cls = null;
            switch (wifiDesc.getType()){
                case WifiDesc.WISPr:{
                    cls = WISPrLoginService.class;
                    break;
                }
                case WifiDesc.WEB_LOGIN:{
                    cls = WebLoginService.class;
                    break;
                }
            }
            Intent i = new Intent(context, cls);
            i.putExtra(Constants.EXTRA_LOGIN_SSID,SSID);
            context.startService(i);
        }
        else if (isConnected && isDisconnectedIntent(intent)) {
            //如果wifi已經連上了,而且intent內容是wifi斷線
            isConnected = false;
            Log.d(TAG, "onReceive: DISCONNECT Change isConnected: "+isConnected);
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
}
