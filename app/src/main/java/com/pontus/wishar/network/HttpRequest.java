package com.pontus.wishar.network;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public abstract class HttpRequest {

    protected static OkHttpClient client = null;

    public HttpRequest(){
        instanceOkHttpClient();
    }

    private synchronized void instanceOkHttpClient(){
        if(client == null)
            client = new OkHttpClient().newBuilder().followRedirects(false).followSslRedirects(false).build();
    }

    protected Request buildGetRequest(String url){
        return new Request.Builder().url(url).build();
    }

    protected Request buildPostRequest(String url, Map<String,String> parameters){
        FormBody.Builder formBuilder = new FormBody.Builder();

        for ( Map.Entry<String, String> entry : parameters.entrySet() ) {
            formBuilder.add( entry.getKey(), entry.getValue() );
        }

        RequestBody formBody = formBuilder.build();
//                .add("UserName", userName)
//                .add("Password", password)
//                .build();

        return new Request.Builder().post(formBody).url(url).build();
    }

}
