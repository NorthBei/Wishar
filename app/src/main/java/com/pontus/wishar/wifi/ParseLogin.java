package com.pontus.wishar.wifi;

import android.content.Context;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.network.HttpReq;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.storage.AccountStorage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.pontus.wishar.Constants.CHECK_URL;
import static com.pontus.wishar.Constants.DESC_ERROR_CANNOT_FIND_FORM_BY_SELECTOR;
import static com.pontus.wishar.Constants.FORM_ACTION_METHOD_NOT_SUPPORT;
import static com.pontus.wishar.Constants.NETWORK_ERROR_RETURN_DATA_IS_NULL;
import static com.pontus.wishar.data.WifiDesc.Parse.KeyValue.LOAD_PASSWORD;
import static com.pontus.wishar.data.WifiDesc.Parse.KeyValue.LOAD_USERNAME;

/**
 * Created by NorthBei on 2018/3/23.
 */

public class ParseLogin extends LoginHandler {

    public ParseLogin(Context context, WifiDesc wifiDesc){
        super(context,wifiDesc);
    }

    @Override
    public void onNetworkUnavailable(String result) throws IOException {
        if(wifiDesc.getJumpToUrl() != null){
            result = new HttpReq().get(wifiDesc.getJumpToUrl()).result().body().string();
            Timber.d("JumpToPage result: %s",result);
        }

        for (WifiDesc.Parse parse : wifiDesc.getParseList()) {
            try {

                result = parseRun(parse,result);
                Timber.d("Parse Login result: %s",result);
                if (result == null) {
                    throw new Exception(NETWORK_ERROR_RETURN_DATA_IS_NULL);
                }
            }
            catch (IOException e){
                e.printStackTrace();
                Timber.d("onNetworkUnavailable(IOException):%s",e.getMessage());
                throw e;
            }
            catch (Exception e) {
                e.printStackTrace();
                Timber.d("onNetworkUnavailable(Exception):%s",e.getMessage());
                NotificationCenter.getInstance(context).showNotify(e.getMessage());
            }
        }

        int httpCode = new HttpReq().get(CHECK_URL).result().code();

        String msg = httpCode == 204 ?"連線成功" : "連線失敗";
        NotificationCenter.getInstance(context).showNotify(msg);
    }

    private String parseRun(WifiDesc.Parse parse,String result) throws Exception {
        Document doc = Jsoup.parse(result);
        Elements form = findForm(doc,parse);

        String method = form.attr("method");
        String action = parse.getActionUrl() == null ? form.attr("action") : parse.getActionUrl();

        action = action.startsWith("./") ? action.replace("./",wifiDesc.getUrl()) : action ;
        action = !action.startsWith("http") ? wifiDesc.getUrl()+action : action ;

        Timber.d("action:%s",action);

        Map<String,String> parameter = new HashMap<>();
        Elements inputs = form.select("input");
        for (Element e: inputs) {
            String name = e.attr("name");
            String value = e.attr("value");
            parameter.put(name,value);
            Timber.d("request parameter: %s = %s",name,value);
        }

        addDescInputData(parse,parameter);

        if(!method.trim().toLowerCase().equals("post")) {
            throw new Exception(FORM_ACTION_METHOD_NOT_SUPPORT);
        }

        return new HttpReq().post(action, parameter).result().body().string();
    }

    private void addDescInputData(WifiDesc.Parse parse,Map<String, String> parameter) {
        AccountStorage as = getAccountStorage();

        for (WifiDesc.Parse.KeyValue kv : parse.getInputData()) {
            String key = kv.getKey();
            String value = kv.getValue();

            value = value.equals(LOAD_USERNAME)? as.getAccount() + as.getPostfix() : value;
            value = value.equals(LOAD_PASSWORD)? as.getPassword() : value;

            parameter.put(key,value);
        }
    }

    private Elements findForm(Document doc,WifiDesc.Parse parse) throws Exception {
        Elements findForm = doc.select(parse.getFormSelector());

        if(!findForm.isEmpty()) {
            return findForm;
        }
        //wifiDesc中的css selector 找不到form
        throw new Exception(DESC_ERROR_CANNOT_FIND_FORM_BY_SELECTOR);
    }
}
