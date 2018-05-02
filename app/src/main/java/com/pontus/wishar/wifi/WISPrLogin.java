package com.pontus.wishar.wifi;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.network.Firebase;
import com.pontus.wishar.network.HttpReq;
import com.pontus.wishar.notify.NotificationCenter;
import com.pontus.wishar.storage.AccountStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by NorthBei on 2018/3/21.
 */

public class WISPrLogin extends LoginHandler {

    private String userName,password;

    private static final String TAG = WISPrLogin.class.getSimpleName();

    private static final String LOGIN_SUCCESSED = "50";
    private static final String ID_NOT_FOUND = "100";

    private static final Pattern m_pLoginURL = Pattern.compile(
            "<WISPAccessGatewayParam.*<Redirect.*<LoginURL>(.*)</LoginURL>.*</Redirect>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    private static final Pattern m_pResponseCode = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<ResponseCode>(.*)</ResponseCode>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    private static final Pattern m_pReplyMessage = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<ReplyMessage>(.*)</ReplyMessage>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    private static final Pattern m_pLogoffURL = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<LogoffURL>(.*)</LogoffURL>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    Firebase firebase;
    public WISPrLogin(Context context,WifiDesc wifiDesc){
        super(context,wifiDesc);
        firebase=new Firebase(context);
    }

    @Override
    public void onNetworkUnavailable(@NonNull String result) {
        AccountStorage as = getAccountStorage();
        userName = as.getAccount() + as.getPostfix();
        password = as.getPassword();
        login(result);
    }

    private void login(String sTmp) {
        Timber.d("login: account:"+userName+" password:"+password+" sTmp:"+sTmp);

        long initTime = System.currentTimeMillis();

        sTmp = removeNewLine(sTmp);
        String sURL = getXMLValue(sTmp, m_pLoginURL);

        if (sURL != null) {
            Timber.d("LoginURL=%s",sURL);

            if (sURL.toLowerCase().startsWith("https://")) {

                HashMap<String,String> parameter = new HashMap<>();
                parameter.put("UserName",userName);
                parameter.put("Password",password);

                try {
                    sTmp = new HttpReq().post(sURL,parameter).result().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//               WISPrHttpRequest http = new WISPrHttpRequest();
//               sTmp = http.post(sURL,parameter);

                String sResponseCode = getXMLValue(sTmp, m_pResponseCode);
                String sReplyMessage = getXMLValue(sTmp, m_pReplyMessage);
                String spLogoffURL = getXMLValue(sTmp, m_pLogoffURL);
                if (sResponseCode != null && LOGIN_SUCCESSED.equals(sResponseCode)) {
                    showNotify("Login success");
                    Timber.d("Login success(" + (System.currentTimeMillis() - initTime) + "ms): LogoffURL=" + spLogoffURL);
                }
                else {
                    String errorMsg = (ID_NOT_FOUND.equals(sResponseCode) ? "帳號類型、帳號或密碼錯誤" : sReplyMessage);
                    showNotify("Login failed,"+errorMsg);
                }
            }
            else {
                showNotify("不安全的連線，為了保護個資，已中斷登入流程");
                Timber.d("[Warning]LoginURL is not https connection. LoginURL=%s",sURL);
            }
        }
        else {
            showNotify("Login Type Error (WISPr)");
            Timber.d("Not supported WISPr.");
        }
        try {
            firebase.LoginInfo(wifiDesc.getScriptName(), "", "success", "success","WISPr");
            Timber.d("Firebase Success");
        }catch (Exception e){
            Timber.d("Firebase Error: "+e.toString());
        }
    }

    private String removeNewLine(String content) {
        return content != null ? content.replaceAll("\\r*\\n", "") : null;
    }

    private String getXMLValue(String sVal, Pattern pPattern) {
        String sRet = null;
        Matcher mm = pPattern.matcher(sVal);
        if (mm.find() && (sRet = mm.group(1).trim()).length() == 0)
            sRet = null;
        return sRet;
    }

    private void showNotify(String msg){
        NotificationCenter.getInstance(getContext()).showNotify(msg);
    }
}
