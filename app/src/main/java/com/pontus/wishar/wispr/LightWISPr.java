package com.pontus.wishar.wispr;

import android.util.Log;

import com.pontus.wishar.network.WISPrHttpRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LightWISPr {

    private ArrayList<String> logList = new ArrayList<>();
    private String userName,password;

    private static final String TAG = LightWISPr.class.getSimpleName();

    static final String LOGIN_SUCCESSED = "50";

    final Pattern m_pLoginURL = Pattern.compile(
            "<WISPAccessGatewayParam.*<Redirect.*<LoginURL>(.*)</LoginURL>.*</Redirect>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    final Pattern m_pResponseCode = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<ResponseCode>(.*)</ResponseCode>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    final Pattern m_pReplyMessage = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<ReplyMessage>(.*)</ReplyMessage>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
    final Pattern m_pLogoffURL = Pattern.compile(
            "<WISPAccessGatewayParam.*<AuthenticationReply.*<LogoffURL>(.*)</LogoffURL>.*</AuthenticationReply>.*</WISPAccessGatewayParam>",
            Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

    // about ncsi
    // https://dotblogs.com.tw/swater111/2014/01/09/139420
//    private static final String m_CHECK_URL = "http://www.msftncsi.com/ncsi.txt";
//    private static final String m_CHECK_KEYWORD = "NCSI";
//    private static final String m_sCharset = "UTF-8";

    long initTime = System.currentTimeMillis();
    Date date = new Date();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public LightWISPr(String userName,String password){
        this.userName = userName;
        this.password = password;
    }

    public ArrayList<String> login(String sTmp) {
        log("login: account:"+userName+" password:"+password+" sTmp:"+sTmp);

        initTime = System.currentTimeMillis();

        if (sTmp.startsWith("[Warning]")) {
            log(sTmp);
            return logList;
        }

        sTmp = removeNewLine(sTmp);
        String sURL = getXMLValue(sTmp, m_pLoginURL);

        if (sURL != null) {
            log("LoginURL=" + sURL);

            if (sURL.toLowerCase().startsWith("https://")) {
                WISPrHttpRequest http = new WISPrHttpRequest();

                HashMap<String,String> parameter = new HashMap<>();
                parameter.put("UserName",userName);
                parameter.put("Password",password);

                sTmp = http.post(sURL,parameter);
                if(sTmp == null){
                    //error
                    return logList;
                }
                String sResponseCode = getXMLValue(sTmp, m_pResponseCode);
                String sReplyMessage = getXMLValue(sTmp, m_pReplyMessage);
                String spLogoffURL = getXMLValue(sTmp, m_pLogoffURL);
                if (sResponseCode != null && LOGIN_SUCCESSED.equals(sResponseCode)) {
                    log("Login success(" + (System.currentTimeMillis() - initTime) + "ms): LogoffURL=" + spLogoffURL);
                } else {
                    log("Login failed(" + (System.currentTimeMillis() - initTime) + "ms): ResponseCode=" + sResponseCode + ", ReplyMessage=" + sReplyMessage);
                }
            } else {
                log("[Warning]LoginURL is not https connection. LoginURL=" + sURL);
            }
        }
        else {
            log("Not supported WISPr.");
        }
        return logList;
    }

    private String removeNewLine(String sVal) {
        return sVal != null ? sVal.replaceAll("\\r*\\n", "") : null;
    }

    private String getXMLValue(String sVal, Pattern pPattern) {
        String sRet = null;
        Matcher mm = pPattern.matcher(sVal);
        if (mm.find() && (sRet = mm.group(1).trim()).length() == 0)
            sRet = null;
        return sRet;
    }

    private void log(String msg) {
        date.setTime(System.currentTimeMillis());
        String log = dateFormat.format(date) + " " + (System.currentTimeMillis() - initTime) + "ms " + msg;
        Log.d(TAG, log);
        logList.add(log);
    }

}
