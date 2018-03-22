package com.pontus.wishar.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.pontus.wishar.Constants;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.network.HttpReq;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.storage.AssetsStorage;
import com.pontus.wishar.wifi.LoginHandler;
import com.pontus.wishar.wifi.WISPrLogin;

import java.io.IOException;

import okhttp3.Response;
import timber.log.Timber;

import static com.pontus.wishar.Constants.CHECK_URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EntryIntentService extends IntentService {

    public static final String TAG = EntryIntentService.class.getSimpleName();
    private Context context;
    private String SSID;
    private AccountStorage accountStorage;
    private WifiDesc wifiDesc;

    public EntryIntentService() {
        super("EntryIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Timber.d("onHandleIntent: run");
        context = getApplicationContext();
        SSID = intent.getStringExtra(Constants.EXTRA_LOGIN_SSID);
        Timber.d("SSID: %s",SSID);

        //get wifi description file
        AssetsStorage as = new AssetsStorage(context);
        wifiDesc = as.getWifiDescObj(SSID);

        try {
            HttpReq httpReq = new HttpReq();//"https://www.wifly.com.tw/MCD/login.aspx"
            Response response = httpReq.get(CHECK_URL).result();

            Timber.d("onHandleIntent: %d",response.code());

            if(response.code() == 204){
                notifyUser(true,"Already connected to Internet.");
                return;
            }

            if(response.body() != null) {
                getLoginHandler().onNetworkUnavailable(response.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
            Timber.d("onError: %s",e.getMessage());
            if (e instanceof java.net.UnknownHostException) {
                //這個error會出現在wifi的連結剛建立完成的時候 立刻進行網路傳輸 會造成資料送不出去 等一下再重新送應該就可以了
                notifyUser(false,"出了點問題，重新連接(get some problem ,retry now)");
                return;
            }
            notifyUser(false,e.getMessage());

        }

        Timber.d("onHandleIntent: end");
    }

    protected void notifyUser(boolean isSuccess, String msg){
        if(isSuccess)
            NotificationCenter.getInstance(context).showNotify("OK:" + msg);
        else
            NotificationCenter.getInstance(context).showNotify("GG:" + msg);
    }

    @NonNull
    private LoginHandler getLoginHandler(){
        switch (wifiDesc.getType()){
            case WifiDesc.WISPr:
                return new WISPrLogin(context,SSID,wifiDesc);
        }

        return new LoginHandler(context,SSID,wifiDesc) {
            @Override
            public void onNetworkUnavailable(String result) {
                notifyUser(false,"沒有 LoginHandler");
                Timber.d("onNetworkUnavailable: "+SSID+" result:"+result);
            }
        };
    }
}
