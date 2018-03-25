package com.pontus.wishar.wifi;

import android.content.Context;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.network.HttpReq;
import com.pontus.wishar.notify.NotificationCenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by NorthBei on 2018/3/23.
 */

public class ParseLogin extends LoginHandler {

    public ParseLogin(Context context, String SSID, WifiDesc wifiDesc){
        super(context,SSID,wifiDesc);
    }

    @Override
    public void onNetworkUnavailable(String result) {
        Document doc = Jsoup.parse(result);
        Elements form = doc.select(getWifiDesc().getParse().getFormSelect().get(0));
        if (form.size() == 0) {
            return;
        }
        String method = form.attr("method");
        String action = form.attr("action");

        if(action.startsWith("./")){
            action = action.replace("./",getWifiDesc().getProtocol()+"://"+getWifiDesc().getDomain()+'/');
            Timber.d("url:%s",action);
        }
        else if(!action.startsWith("http")){
            action = getWifiDesc().getProtocol()+"://"+getWifiDesc().getDomain()+'/'+action;
        }

        Map<String,String> parameter = new HashMap<>();
        Elements inputs = form.select("input");
        for (Element e: inputs) {
            String name = e.attr("name");
            String value = e.attr("value");
            Timber.d("request: %s = %s",name,value);
            parameter.put(name,value);
        }

        HttpReq httpReq = new HttpReq();
        if(method.toLowerCase().equals("post")) {
            try {
                Response response = httpReq.post(action, parameter).result();
                Timber.d("req url-:"+response.request().url().toString());
                String content = response.body().string();
                Timber.d("Jsoup Login result: %s",content);

                if(response.request().url().toString().contains("www.qsquare.com.tw")){
                    NotificationCenter.getInstance(context).showNotify(SSID+" Parse Login Success");
                }
                else if(getWifiDesc().getSsid().equals(".TPE-Free AD WiFi")){
                    onNetworkUnavailable(content);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
