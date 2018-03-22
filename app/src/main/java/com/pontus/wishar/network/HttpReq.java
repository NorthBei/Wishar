package com.pontus.wishar.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.pontus.wishar.Constants;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by NorthBei on 2018/3/21.
 */

public class HttpReq {

    private Request request;
    private static final int HTTP_TIMEOUT = 5;//seconds
    //singleton , 全部只需要1個 http client就夠了，省資源
    private static OkHttpClient client = null;

    private synchronized static OkHttpClient getHttpClient() {
        if(client == null){
            //If unset, redirects will be followed.
            client = new OkHttpClient.Builder().connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS).addNetworkInterceptor(new StethoInterceptor()).build();
        }
        return client;
    }

    private Request.Builder requestBuilder(){
        return new Request.Builder().removeHeader("User-Agent").addHeader("User-Agent", Constants.USER_AGENT);
    }

    public HttpReq get(String url){
        request = requestBuilder().url(url).build();
        return this;
    }

    public HttpReq post(String url, Map<String,String> parameters){
        FormBody.Builder formBuilder = new FormBody.Builder();

        for ( Map.Entry<String, String> entry : parameters.entrySet() ) {
            formBuilder.add( entry.getKey(), entry.getValue() );
        }

        RequestBody formBody = formBuilder.build();
//                .add("UserName", userName)
//                .add("Password", password)
//                .build();

        request = requestBuilder().post(formBody).url(url).build();
        return this;
    }

    public void result(Callback callback) {
        getHttpClient().newCall(request).enqueue(callback);
    }

    public Response result() throws IOException {
        return getHttpClient().newCall(request).execute();
    }
}
