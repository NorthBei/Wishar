package com.pontus.wishar.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.pontus.wishar.Constants;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.data.WifiDescCache;
import com.pontus.wishar.network.HttpReq;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.wifi.LoginHandler;
import com.pontus.wishar.wifi.ParseLogin;
import com.pontus.wishar.wifi.TestLogin;
import com.pontus.wishar.wifi.WISPrLogin;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Response;
import timber.log.Timber;

import static com.pontus.wishar.Constants.CHECK_URL;
import static com.pontus.wishar.Constants.MAX_TRY_CONNECT;

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
    private WifiDesc wifiDesc;
    private int tryConnectTimes = 0;

    public EntryIntentService() {
        super("EntryIntentService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Timber.d("onHandleIntent: run");
        context = getApplicationContext();
        String SSID = intent.getStringExtra(Constants.EXTRA_WIFI_DESC_NAME);
        Timber.d("SSID: %s",SSID);

        //get wifi description file
        wifiDesc = WifiDescCache.getCache().getWifiDesc(context,SSID);
        notifyUser("開始嘗試自動登入");
        tryLoginIfOffline();

        Timber.d("onHandleIntent: end");
    }

    private void tryLoginIfOffline(){
        tryConnectTimes++;
        if(tryConnectTimes == MAX_TRY_CONNECT){
            notifyUser("已嘗試連線"+ tryConnectTimes +"次，可能是網路訊號不佳或網速較慢導致無法連線成功");
            return;
        }

        try {
            Thread.sleep(1000);
            Response response = new HttpReq().get(CHECK_URL).result();

            Timber.d("onHandleIntent: %d",response.code());

            if(response.code() == 204){
                notifyUser("Already connected to Internet.");
                return;
            }

            if(response.body() == null) {
                notifyUser("WiFi供應商提供的資料錯誤，Wishar已停止");
                return;
            }

            String host = response.request().url().host();
            String reqUrl = response.request().url().toString();
            Timber.d("onHandleIntent: redirect url:%s",reqUrl);
            if(wifiDesc.getRedirectPageHost().size()>0 && !wifiDesc.getRedirectPageHost().contains(host)){
                notifyUser("WiFi供應商與熱點名稱不符，Wishar已停止");
                return;
            }
            String responseString = response.body().string();
            Timber.d(responseString);
            getLoginHandler().onNetworkUnavailable(responseString);
        }
        catch (UnknownHostException e){
            Timber.d(e.getStackTrace().toString());
            //SocketException會出現的時機參考http://www.voidcn.com/article/p-vbaskgpq-gt.html
            //UnknownHostException會出現在wifi的連結剛建立完成的時候 立刻進行網路傳輸 會造成資料送不出去 等一下再重新送應該就可以了
            if(tryConnectTimes%10 == 0){
                notifyUser("網路連線出了點問題，重新連接");
            }
            tryLoginIfOffline();
        }
        catch (SocketTimeoutException | SocketException e){
            e.printStackTrace();
            if(tryConnectTimes%10 == 0){
                notifyUser("WiFi回應超時，可能是網路訊號不佳或網速較慢");
            }
            tryLoginIfOffline();
        }
        catch (IOException e) {
            e.printStackTrace();
            Timber.d("onError: %s",e.getMessage());
            notifyUser(e.getMessage());
        }
        catch (InterruptedException e) {
            //call Thread.sleep()可能會出現的error，出現我也不知道怎麼辦，忽略吧QQ
            e.printStackTrace();
        }
        //network exception 解釋:http://shunji.wang/404.html
    }

    protected void notifyUser(String msg){
        NotificationCenter.getInstance(context).showNotify(msg);
    }

    @NonNull
    private LoginHandler getLoginHandler(){
        switch (wifiDesc.getType()){
            case WifiDesc.WISPr:
                return new WISPrLogin(context,wifiDesc);
            case WifiDesc.PARSE:
                return new ParseLogin(context,wifiDesc);
            case "Test":
                return new TestLogin(context,wifiDesc);
        }

        //throw Exception("沒有 LoginHandler");

        return new LoginHandler(context,wifiDesc) {
            @Override
            public void onNetworkUnavailable(String result) {
                notifyUser("沒有 LoginHandler");
                Timber.d("onNetworkUnavailable: result:"+result);
            }
        };
    }
}
