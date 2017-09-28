package com.pontus.wishar.network;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;

public class AsyncHttpRequest extends HttpRequest {

    protected String requestUrl;
    private Callback callback;

    public AsyncHttpRequest(Callback callback){
        super();
        this.callback = callback;
    }

    public void get(String requestUrl){
        this.requestUrl = requestUrl;
        Request request = buildGetRequest(requestUrl);

        asyncHttpRequest(request);
    }

    public void post(String requestUrl,Map<String,String> postParams){
        this.requestUrl = requestUrl;
        Request request = buildPostRequest(requestUrl,postParams);

        asyncHttpRequest(request);
    }

    private void asyncHttpRequest(Request request) {
        Call call = client.newCall(request);
        call.enqueue(callback);

//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
    }
}
