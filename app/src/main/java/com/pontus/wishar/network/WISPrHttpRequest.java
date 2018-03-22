package com.pontus.wishar.network;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import okhttp3.Response;
import timber.log.Timber;


public class WISPrHttpRequest {
    private static final String TAG = WISPrHttpRequest.class.getSimpleName();

    private static final int MAX_REDO = 10;
    private int redirectTimes = 0;
    private int retryHostTimes = 0;

    // about ncsi
    // https://dotblogs.com.tw/swater111/2014/01/09/139420
    public static final String CHECK_NETWORK_URL = "http://www.msftncsi.com/ncsi.txt";
    public static final String CHECK_NETWORK_KEYWORD = "NCSI";

    public static final String WARNING = "[Warning]";
    public static final String REDIRECT_TOO_MUCH_TIMES = "[Warning]Server redirected too many times";
    public static final String UNTRUST_URL = "[Warning]Untrust certification url";
    public static final String IO_EXCEPTION = "[Warning]IOException";
    public static final String UNKNOWN_HOST = "[Warning]UnknownHostException";

    private HttpReq httpReq;
    private String requestUrl;

    public WISPrHttpRequest(){
        httpReq = new HttpReq();
    }

    public String get(String url){
        requestUrl = url;
        try {
            Response response = httpReq.get(url).result();
            return onSuccess(response);
        } catch (IOException e) {
            e.printStackTrace();
            return onError(e);
        }
    }

    public String post(String url, Map<String,String> parameters){
        requestUrl = url;
        try {
            Response response = httpReq.post(url,parameters).result();
            return onSuccess(response);
        } catch (IOException e) {
            e.printStackTrace();
            return onError(e);
        }
    }


    public String onSuccess(Response response) throws IOException {
        if (response.code() == HttpURLConnection.HTTP_MOVED_TEMP) {
            //if has redirect url , get the url XML
            String location = response.header("Location");
            Log.d(TAG, "onResponse: Location:" + location);

            if(redirectTimes < MAX_REDO){
                redirectTimes++;
                return get(location);
            }
            else{
                return REDIRECT_TOO_MUCH_TIMES;
            }
        }

        return response.body().string();
    }

    private String onError(IOException e){
        if (e instanceof java.net.UnknownHostException) {
            //這個error會出現在wifi的連結剛建立完成的時候 立刻進行網路傳輸 會造成資料送不出去 等一下再重新送應該就可以了

            if(retryHostTimes < MAX_REDO) {
                Timber.d("onError:retry after 0.5s");
                try {
                    Thread.sleep(500);
                    retryHostTimes++;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                //after 0.5s , retry
                return get(requestUrl);
            }
            else{
                return UNKNOWN_HOST;
            }

        }
        else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            Timber.d( "onError: "+UNTRUST_URL+" : "+requestUrl);
            return UNTRUST_URL;
        }

        //else : IOException
        e.printStackTrace(System.err);
        Timber.d("onError: "+IO_EXCEPTION+" : "+e.getMessage());
        return IO_EXCEPTION;
    }
}
