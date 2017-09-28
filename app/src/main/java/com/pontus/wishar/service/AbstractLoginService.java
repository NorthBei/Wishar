package com.pontus.wishar.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pontus.wishar.Constants;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.network.WISPrHttpRequest;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.storage.AssetsStorage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.pontus.wishar.Constants.EXTRA_LOGIN_LOG_RESULT;

public abstract class AbstractLoginService extends Service {

    private Context context;
    private String SSID;
    private AccountStorage accountStorage;
    private WifiDesc wifiDesc;

    protected long initTime = System.currentTimeMillis();
    protected Date date = new Date();
    protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void onCreate() {
        context = getBaseContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null)
            return START_NOT_STICKY;

        SSID = intent.getStringExtra(Constants.EXTRA_LOGIN_SSID);
        accountStorage = new AccountStorage(context,SSID);

        //get wifi description file
        AssetsStorage as = new AssetsStorage(context);
        wifiDesc = as.readFileToJson(SSID+".json", WifiDesc.class);

        //RxJava 文章 : https://medium.com/@ome450901/rxjava2-%E4%BA%8C%E4%B8%89%E5%9B%9B%E4%BA%94%E5%85%AD%E8%A8%AA-29b6ab624ab2
        //RxJava 文章 : http://www.jianshu.com/p/6fd8640046f1

        /* 想像是開關按鈕：發送 開/關 的訊息給檯燈 */ /*Button*/
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                // 使用 ObservableEmitter 來發送訊息，這邊會run在

                WISPrHttpRequest wispr = new WISPrHttpRequest();

                /*
                 *這邊的result 會回傳redirect url or HTTP Get Response body content
                 *所以這邊的result不是url就是NCSI
                 */
                String result = wispr.tryNetWorkAvailable();

                log("subscribe: tryNetWorkAvailable:"+result);

                if(result.contains(WISPrHttpRequest.CHECK_NETWORK_KEYWORD)){
                    //該wifi已經可以上網，STOP
                    showNotify(true);
                    emitter.onComplete();
                    return;
                }

                //為了防止有人用相同的SSID騙Wishar對某個熱點送出帳號密碼，必須認證登入url的domain是否與安全的domain相同
                URL url = new URL(result);
                log("subscribe: authority:"+url.getAuthority()+" , domain in wifiDesc:"+wifiDesc.getDomain());
                if(!url.getAuthority().equals(wifiDesc.getDomain())) {
                    //認證失敗，不做登入的動作
                    showNotify(false);
                    return;
                }

                result = wispr.get(result);
                emitter.onNext(result);
//                emitter.onNext("on");
//                emitter.onNext("off");
                emitter.onComplete();
            }
        });

        /* 想像是檯燈：接收來自開關按鈕的訊息 */ /*listener*/
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(getTAG(), "onSubscribe");
            }

            @Override
            public void onNext(String string) {
                // 收到 開/關 的訊息
                Log.d(getTAG(), "onNext: " + string);
                onNetworkUnavailable(string);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(getTAG(), "onError:" + t.getLocalizedMessage());
            }

            @Override
            public void onComplete() {
                Log.d(getTAG(), "onComplete");
            }
        };

        // 把檯燈跟開關按鈕用電線連接起來
        //observable.subscribe(observer);

        Scheduler resultThreadType = isRunOnMainThread()? AndroidSchedulers.mainThread() : Schedulers.io();

        // 把檯燈跟開關按鈕用電線連接起來
        observable.subscribeOn(Schedulers.io())//非同步任務在IO thread執行
                //.observeOn(AndroidSchedulers.mainThread())//執行結果run on main thread
                .observeOn(resultThreadType)//執行結果run在哪個thread
                .subscribe(observer);

        return START_NOT_STICKY;
    }

    //service不做binding , 維持自己的生命周期
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //LocalBroadcast用的TAG ,指定今天要送出哪一個key的廣播
    protected abstract String getBroadcastAction();

    //顯示Log.d的TAG
    protected abstract String getTAG();

    //絕對onNetworkUnavailable() method 是要run在UI thread(true)還是IO thread(false)
    protected abstract boolean isRunOnMainThread();

    //在wifi連上之後網路還不通，就會call這個function
    protected abstract void onNetworkUnavailable(String result);

    protected void sendLog(String msg){
        Intent localIntent = new Intent(getBroadcastAction())
                // Puts the data into the Intent
                .putExtra(EXTRA_LOGIN_LOG_RESULT, msg);

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    protected void log(String msg) {
        date.setTime(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        sb.append(dateFormat.format(date)).append(" ").append(System.currentTimeMillis() - initTime).append("ms ").append(msg);
        Log.d(getTAG(), sb.toString());

        sendLog(msg);
    }

    //簡易版的顯示通知，還需要在更改
    protected void showNotify(boolean isSuccess){
        if(isSuccess)
            NotificationCenter.getInstance(context).showNotify(SSID + ": Already connected to Internet.");
        else
            NotificationCenter.getInstance(context).showNotify(SSID + ": error");
    }

    //幫你找一個人來去跟PairDB溝通
    protected AccountStorage getAccountStorage(){
       return accountStorage;
    }

    protected String getSSID(){
        return SSID;
    }
}
