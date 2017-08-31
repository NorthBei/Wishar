package com.pontus.wishar.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pontus.wishar.MainActivity;
import com.pontus.wishar.view.fragment.WebLoginFragment;

import static com.pontus.wishar.Constants.EXTRA_LOGIN_LOG_RESULT;

public class WebLoginService extends Service {
    public WebLoginService() {
    }

    //https://stackoverflow.com/questions/25602117/android-webview-always-returns-null-for-javascript-getelementbyid-on-loadurl

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final WebView wv= new WebView(this);
        wv.clearCache(true);

        WebSettings webSettings = wv.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        wv.loadUrl("https://www.tpe-free.tw/tpe/tpe_login.aspx");

        Log.i("LocalService", "Received start id " + startId + ": " + intent);


        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage){
                super.onConsoleMessage(consoleMessage);
                Log.d("WebViewServicex", "onConsoleMessage: "+consoleMessage.message());
                sendLog(consoleMessage.message());
                return true;
            }
        });

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView v, String url) {
                Log.d("WebViewServicex", "onPageFinished: url:"+url);
                //wv.loadUrl("javascript:console.log(document.documentElement.innerHTML);");
                sendLog(url);

                if(!url.equals(MainActivity.TPE_FREE_URL))
                    return;

                wv.loadUrl("javascript: " +
                        "document.getElementById('userrole').value = 59;"+
                        "document.getElementById('pno').value = '0972102760';" +
                        "document.getElementById('passwd').value = 'simon054';" +
                        "document.getElementById('btn_login').click();"+
                        "console.log('test');"
                );
            }
        });

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLog(String msg){
        Intent localIntent = new Intent(WebLoginFragment.WEBLGOIN_LOG_RECEIVE_ACTION)
                // Puts the data into the Intent
                .putExtra(EXTRA_LOGIN_LOG_RESULT, msg);

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
    }

}
