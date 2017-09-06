package com.pontus.wishar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.service.WISPrService;
import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.wifi.WifiAdmin;

import java.util.Map;

import static android.net.wifi.WifiManager.EXTRA_NEW_STATE;
import static android.net.wifi.WifiManager.SUPPLICANT_STATE_CHANGED_ACTION;
import static com.pontus.wishar.Constants.WISPR_LOGIN_ACCOUNT;
import static com.pontus.wishar.Constants.WISPR_LOGIN_PASSWORD;

public class WifiConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = WifiConnectionReceiver.class.getSimpleName();
    private static final String EXCEPT_SSID_1 = "<unknown ssid>" , EXCEPT_SSID_2 = "0x";
    public static boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.equals(SUPPLICANT_STATE_CHANGED_ACTION)) {
            SupplicantState state =  intent.getParcelableExtra(EXTRA_NEW_STATE);

            if (state != null && state == SupplicantState.DISCONNECTED) {
                //訊號斷線
                //context.stopService(new Intent(context, MyService.class));
            }
            return;
        }

        if (isConnectedIntent(intent)) {

            String SSID = WifiAdmin.getInstance(context).getWifiInfo().getSSID().replace("\"", "");

            if(SSID.equals(EXCEPT_SSID_1) || SSID.equals(EXCEPT_SSID_2))
                return;

            if(!isConnected){
                isConnected = true;

                Log.d(TAG, "onReceive: CONNECT SSID:"+SSID);
                Log.d(TAG, "onReceive: Change isConnected: "+isConnected);

                loginWifi(context,SSID);
            }
        }
        else if (isDisconnectedIntent(intent)) {

            if(isConnected){
                isConnected = false;
                Log.d(TAG, "onReceive: DISCONNECT");
                Log.d(TAG, "onReceive: Change isConnected: "+isConnected);
                NotificationCenter.getInstance(context).cleanNotify();
            }
        }
    }

    private void loginWifi(Context context, String SSID) {

        boolean isOurService =  AccountStorage.isAccountExist(context,SSID);
        Log.d(TAG, "loginWifi: isOurService:"+isOurService);
        if(isOurService){
            NotificationCenter.getInstance(context).showNotify("wifi connect to " + SSID);
            AccountStorage as = new AccountStorage(context,SSID);
            Map<String,String> loginInfo = as.getLoginInfo();
        }

        if(isOurService){
            String account = null,password = null;

            switch (SSID){
                case "iTaiwan":
                    account = "0972102760@itw";
                    password = "simon054";
                    break;
                case "TPE-Free_CHT":
                case "TPE-Free":
                    account ="0972102760@tpe-free.tw";
                    password = "simon054";
                    break;
                case "CHT Wi-Fi(HiNet)":
                    account = "0975777632@emome.net";
                    password = "GFDB5Dqt";
                    break;
            }

            Intent loginIntent = new Intent(context, WISPrService.class);
            loginIntent.putExtra(WISPR_LOGIN_ACCOUNT,account);
            loginIntent.putExtra(WISPR_LOGIN_PASSWORD,password);

            context.startService(loginIntent);
            //EzWISPr.login((Activity)context,account,password);
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
