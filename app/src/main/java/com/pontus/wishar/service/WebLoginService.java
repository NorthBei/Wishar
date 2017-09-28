package com.pontus.wishar.service;

import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.storage.AssetsStorage;
import com.pontus.wishar.view.fragment.WebLoginFragment;

public class WebLoginService extends AbstractLoginService {

    //private static final String WIFI_JSON = "ntou";
    private static final String TAG = WebLoginService.class.getSimpleName();

    private boolean isLogined = true;
    private String url;

    public WebLoginService() {
    }

    //如何使用Service http://givemepass.blogspot.tw/2015/10/service.html
    //https://stackoverflow.com/questions/25602117/android-webview-always-returns-null-for-javascript-getelementbyid-on-loadurl

    @Override
    protected void onNetworkUnavailable(String result) {

        final AssetsStorage assetsManager = new AssetsStorage(getBaseContext());
        final WifiDesc wifiDesc = assetsManager.readFileToJson(getSSID()+".json",WifiDesc.class);

        final WebView webView = new WebView(this);
        //webView.clearCache(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        url = wifiDesc.getUrl();
        webView.loadUrl(url);//get web
        Log.d("loadUrl", url);

//        wv.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                super.onConsoleMessage(consoleMessage);
//                sendLog(consoleMessage.message());
//                return true;
//            }
//        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String webUrl) {
                Log.d(TAG, "onPageFinished: url:" + webUrl);
                //webView.loadUrl("javascript:console.log(document.documentElement.innerHTML);");
                sendLog("onPageFinished url:"+webUrl);
                if (!webUrl.equals(url))
                    return;

                if(isLogined) {
                    String account = getAccountStorage().getAccount();
                    String password = getAccountStorage().getPassword();

                    sendLog("login account:"+account+" ,password:"+password);

                    String js = String.format(wifiDesc.getScript().getLoginJS(), account , password);
                    Log.d("JS  ", js);

                    String jquery = assetsManager.readFile("jquery.js");

                    webView.loadUrl("javascript:" + jquery);
                    webView.loadUrl(js);

                    isLogined = false;
                }
            }

        });

        //stopSelf();
    }

    @Override
    protected String getBroadcastAction() {
        return WebLoginFragment.WEBLGOIN_LOG_RECEIVE_ACTION;
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    //webview must run on main thread
    @Override
    protected boolean isRunOnMainThread() {
        return true;
    }
}
