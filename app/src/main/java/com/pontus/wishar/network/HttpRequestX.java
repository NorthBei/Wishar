package com.pontus.wishar.network;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpRequestX {

    //這個類別暫時無用，ignore it
    private static final String TAG = HttpRequestX.class.getSimpleName();
    protected static OkHttpClient client = null;
    protected String requestUrl;
    private boolean isAsyn;
    private static final int MAX_REDO = 10;
    private int redirectTimes = 0;
    private int retryHostTimes = 0;

    private static final String REDIRECT_TOO_MUCH_TIMES = "[Warning]Server redirected too many times";
    private static final String UNTRUST_URL = "[Warning]Untrust certification url";
    private static final String IO_EXCEPTION = "[Warning]IOException";
    private static final String UNKNOWN_HOST = "[Warning]UnknownHostException";

    public HttpRequestX(){
        //預設是非同步處理
        this(true);
    }

    public HttpRequestX(boolean isAsyn){
        this.isAsyn = isAsyn;
        instanceOkHttpClient();
    }

    private synchronized void instanceOkHttpClient(){
        if(client == null)
            client = new OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build();
    }

    public void get(String requestUrl){
        this.requestUrl = requestUrl;
        Request request = new Request.Builder().url(requestUrl).build();

        sendRequest(request);
    }

    public void post(String requestUrl,Map<String,String> postParams){
        this.requestUrl = requestUrl;
        FormBody.Builder formBuilder = new FormBody.Builder();

        for ( Map.Entry<String, String> entry : postParams.entrySet() ) {
            formBuilder.add( entry.getKey(), entry.getValue() );
        }

        RequestBody formBody = formBuilder.build();
//                .add("UserName", userName)
//                .add("Password", password)
//                .build();

        Request request = new Request.Builder().post(formBody).url(requestUrl).build();

        sendRequest(request);
    }

    private void sendRequest(Request request){
        if(isAsyn)
            asyncHttpRequest(request);
        else
            syncHttpRequest(request);
    }


    private void asyncHttpRequest(Request request) {
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onError(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onSuccess(call,response);
            }
        });
    }

    private void syncHttpRequest(Request request) {
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            onSuccess(call,response);
        } catch (IOException e) {
            onError(call,e);
            e.printStackTrace();
        }
    }

    private void  onSuccess(Call call,Response response) throws IOException {

        if (response.code() == HttpURLConnection.HTTP_MOVED_TEMP) {
            //if has redirect url , get the url XML
            String location = response.header("Location");
            Log.d(TAG, "onResponse: Location:" + location);
            if(redirectTimes < MAX_REDO){
                redirectTimes++;
                get(location);
                return;
            }
            else{
                onResult(REDIRECT_TOO_MUCH_TIMES);
                return;
            }
        }

        onResult(response.body().string());
    }

    private void  onError(Call call,IOException e){

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
                get(requestUrl);
            }
            else{
                onResult(UNKNOWN_HOST);
            }

        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            onResult(UNTRUST_URL);
            Log.d(TAG, "onError: "+UNTRUST_URL+" : "+requestUrl);
        } else {
            //IOException
            e.printStackTrace(System.err);
            onResult(IO_EXCEPTION);
            Log.d(TAG, "onError: "+IO_EXCEPTION+" : "+e.getMessage());
        }
    }

    public void onResult(String result){
        Log.d(TAG, "onResult: requestUrl:"+requestUrl+"\nresult:"+result);
    }

    private String syncHttpRequest(String requestUrl) {
        OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build();

        Request request = new Request.Builder().url(requestUrl).build();

        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            if(response.code() == HttpURLConnection.HTTP_MOVED_TEMP){
                String location = response.header("Location");
                //if has redirect url , get the url XML
                return syncHttpRequest(location);
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
            return syncHttpRequest(requestUrl);
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
}
