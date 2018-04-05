package com.pontus.wishar.wifi;

import android.content.Context;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.network.HttpReq;

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
 * Created by NorthBei on 2018/4/2.
 */

public class TestLogin extends LoginHandler {

    public TestLogin(Context context, WifiDesc wifiDesc) {
        super(context, wifiDesc);
    }

    @Override
    public void onNetworkUnavailable(String result) throws IOException {
        Response res = new HttpReq().get("http://www.wifly.com.tw/StarbucksFree/login.aspx?DeviceKind=2&RTYPE=2").result();
        String x = res.body().string();
        Timber.d("TestLogin result:%s",x);
        Timber.d("TestLogin response url:%s",res.request().url());

        //Document doc = (Document) Jsoup.connect("http://www.wifly.com.tw/StarbucksFree/login.aspx?DeviceKind=2&RTYPE=2");
        Document doc = Jsoup.parse(x);
        Elements form = doc.select("#frm");

        String method = form.attr("method");
        String action = "https://member.wifly.com.tw/MemberNew/LoginS1.aspx";//form.attr("action");

        Map<String,String> parameter = new HashMap<>();
        Elements inputs = form.select("input");
        for (Element e: inputs) {
            String name = e.attr("name");
            String value = e.attr("value");
            parameter.put(name,value);
            Timber.d("request parameter: %s = %s",name,value);
        }

        parameter.put("username","0972102760");
        parameter.put("password","simon054");
        //parameter.put("sUrl","http://www.starbucks.com.tw/starbuckschannel/index.jspx?hubid=HB06000765");

        Response response = new HttpReq().post(action, parameter).result();
        Timber.d("req url:%s",response.request().url().toString());
        x = response.body().string();
        Timber.d("data:%s",x);

        doc = Jsoup.parse(x);
        form = doc.select("#frm");

        method = form.attr("method");
        action = "https://wac.wifly.com.tw/login";//form.attr("action");

        parameter.clear();
        inputs = form.select("input");
        for (Element e: inputs) {
            String name = e.attr("name");
            String value = e.attr("value");
            parameter.put(name,value);
            Timber.d("request parameter: %s = %s",name,value);
        }

        response = new HttpReq().post(action, parameter).result();
        x = response.body().string();
        Timber.d("data:%s",x);
    }
}
