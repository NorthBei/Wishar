package com.pontus.wishar.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by NorthBei on 2017/8/30.
 */

public class WifiAdmin {
    // 定義WifiManager對象
    private WifiManager mWifiManager;
    // 定義WifiInfo對象
    private WifiInfo mWifiInfo;
    // 掃描出的網络連接列表
    private List<ScanResult> mWifiList;
    // 網络連接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定義一個WifiLock
    private WifiManager.WifiLock mWifiLock;


    private static WifiAdmin instance = null;

    public static synchronized WifiAdmin getInstance(Context context) {
        if (instance == null) {
            instance = new WifiAdmin(context);
        }
        return instance;
    }

    // 使用Private 建構子, 確保類別WifiAdmin 的物件化只能透過 API:getInstance()
    private WifiAdmin(Context context) {
        // 取得WifiManager對象
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo對象
        //mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打開WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 關閉WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 檢查當前WIFI狀態
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判斷時候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 創建一個WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的網络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的網络進行連接
    public void connectConfiguration(int index) {
        // 索引大於配置好的網络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 連接配置好的指定ID的網络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    public void startScan() {
        //呼叫 WifiManager.startScan() 之後，回傳的值為是否呼叫成功
        //而掃描是非同步進行的，比較可靠的就是用 BroadcastReceiver 去等掃瞄完成的通知
        mWifiManager.startScan();
        // 得到掃描結果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的網络連接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // 得到網络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看掃描結果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_").append(i + 1).append(":");
            // 將ScanResult信息轉換成一個字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return getWifiInfo().getMacAddress();
    }

    // 得到接入點的BSSID
    public String getBSSID() {
        return getWifiInfo().getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        return getWifiInfo().getIpAddress();
    }

    // 得到連接的ID
    public int getNetworkId() {
        return getWifiInfo().getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfoString() {
        return getWifiInfo().toString();
    }

    //得到wifiInfo
    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    // 添加一個網络並連接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b =  mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }

    // 斷開指定ID的網络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    //然後是一個實際應用方法，只驗證過沒有密碼的情況：
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        ///getConfiguredNetworks 可以拿到手機內已存的Wi-Fi資訊
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

}

//分为三種情況：1沒有密碼2用wep加密3用wpa加密