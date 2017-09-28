package com.pontus.wishar.network;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WISPrHttpRequest extends SyncHttpRequest{
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


    @Override
    public String onSuccess(Call call,Response response) throws IOException {
        if (response.code() == HttpURLConnection.HTTP_MOVED_TEMP) {
            //if has redirect url , get the url XML
            String location = response.header("Location");
            Log.d(TAG, "onResponse: Location:" + location);

            if(redirectTimes < MAX_REDO){
                redirectTimes++;
                return get(location);
            }
            else{
                call.cancel();
                return REDIRECT_TOO_MUCH_TIMES;
            }
        }

        return response.body().string();
    }

    @Override
    public String onError(Call call,IOException e){
       return onErrorHandler(call,e);
    }

    private String onErrorHandler(Call call,IOException e){
        if (e instanceof java.net.UnknownHostException) {
            //這個error會出現在wifi的連結剛建立完成的時候 立刻進行網路傳輸 會造成資料送不出去 等一下再重新送應該就可以了

            if(retryHostTimes < MAX_REDO) {
                Log.d(TAG, "onError:retry after 0.5s");
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
                call.cancel();
                return UNKNOWN_HOST;
            }

        }
        else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            Log.d(TAG, "onError: "+UNTRUST_URL+" : "+requestUrl);
            return UNTRUST_URL;
        }

        //else : IOException
        e.printStackTrace(System.err);
        Log.d(TAG, "onError: "+IO_EXCEPTION+" : "+e.getMessage());
        return IO_EXCEPTION;
    }

    //old WISPr HTTP request code
    private String wisprSyncHttpRequest(String requestUrl) {
        OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build();

        Request request = new Request.Builder().url(requestUrl).build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if(response.code() == HttpURLConnection.HTTP_MOVED_TEMP){
                String location = response.header("Location");
                //if has redirect url , get the url XML
                return wisprSyncHttpRequest(location);
            }
            return response.body().string();

        }
        catch (java.net.UnknownHostException e) {
            //這個error會出現在wifi的連結剛建立完成的時候 立刻進行網路傳輸 會造成資料送不出去 等一下再重新送應該就可以了
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            //after 0.5s , retry
            return wisprSyncHttpRequest(requestUrl);
        }
        catch (javax.net.ssl.SSLHandshakeException e) {
            e.printStackTrace(System.err);
            return "[Warning]Untrust certification:" + requestUrl;
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
            return "[Warning]IOException:" + e.getMessage();
        }

    }

    public String tryNetWorkAvailable(){
        NetworkTestRequest n = new NetworkTestRequest();
        return n.get(CHECK_NETWORK_URL);
    }

    private class NetworkTestRequest extends SyncHttpRequest{

        @Override
        public String onSuccess(Call call,Response response) throws IOException {

            if (response.code() == HttpURLConnection.HTTP_MOVED_TEMP) {
                //if has redirect url , get the url XML
                String location = response.header("Location");
                Log.d(TAG, "onResponse: Location:" + location);

                return location;

            }
            return response.body().string();
        }

        @Override
        public String onError(Call call,IOException e){
            return onErrorHandler(call,e);
        }
    }
}
