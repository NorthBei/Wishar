package com.pontus.wishar.network;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class SyncHttpRequest extends HttpRequest {

    //同步的HttpRequest , 目前只提供get and post method , 其他需要要自己實作
    protected String requestUrl;

    public SyncHttpRequest(){
        super();
    }

    public String get(String requestUrl){
        this.requestUrl = requestUrl;
        Request request = buildGetRequest(requestUrl);

        return syncHttpRequest(request);
    }

    public String post(String requestUrl, Map<String, String> postParams) {
    //public String post(String requestUrl,Map<String,String> postParams){
        this.requestUrl = requestUrl;
        Request request = buildPostRequest(requestUrl,postParams);

        return syncHttpRequest(request);
    }

    private String syncHttpRequest(Request request) {
        Call call = client.newCall(request);

        try {
            Response response = call.execute();
            return onSuccess(call,response);
        } catch (IOException e) {
            e.printStackTrace();
            return  onError(call,e);
        }
    }

    public String onSuccess(Call call,Response response) throws IOException {
        return response.body().string();
    }

    public String onError(Call call,IOException e){
        return e.getMessage();
    }
}
