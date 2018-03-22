package com.pontus.wishar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.pontus.wishar.Constants;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.service.EntryIntentService;
import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.wifi.WifiAdmin;

import timber.log.Timber;

public class WifiConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = WifiConnectionReceiver.class.getSimpleName();
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
            Timber.d("onReceive: Change->CONNECT SSID: %s Change isConnected: %b",SSID,isConnected);

            //判斷是不是Wishar的服務範圍,不是就掰掰
            boolean isOurService =  AccountStorage.isAccountExist(context,SSID);// || SSID.equals("..MCD Free Wi-Fi");
            Timber.d("wifi:"+SSID+", isOurService:"+isOurService);

            if (!isOurService)
                return;

            Intent i = new Intent(context, EntryIntentService.class);
            i.putExtra(Constants.EXTRA_LOGIN_SSID,SSID);
            context.startService(i);
        }
        else if (isConnected && isDisconnectedIntent(intent)) {
            //如果wifi已經連上了,而且intent內容是wifi斷線
            isConnected = false;
            Timber.d("onReceive: Change->DISCONNECT isConnected: %b",isConnected);
            NotificationCenter.getInstance(context).cleanNotify();
        }
    }

    private boolean isConnectedIntent(Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
//            通过反射的方式去判断wifi是否已经连接上，并且可以开始传输数据 http://www.cnblogs.com/819158327fan/p/6689120.html
//            return checkWiFiConnectSuccess(wifiInfo);
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
