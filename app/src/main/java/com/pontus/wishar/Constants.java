package com.pontus.wishar;

public class Constants {
    /*for WifiBroadcastReceiver intent key*/
    public static final String EXTRA_LOGIN_LOG_RESULT = "com.pontus.wishar.service.extra.login_log_result";
    public static final String EXTRA_LOGIN_SSID = "com.pontus.wishar.service.extra.login_ssid";
    public static final String JSON_FILE_EXTENSION = ".json";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
    public static final String CHECK_URL = "http://clients3.google.com/generate_204";
    public static final String ACCEPT_LANGUAGE = "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7";
    public static final int MAX_TRY_CONNECT = 20;

    // about ncsi
    // https://dotblogs.com.tw/swater111/2014/01/09/139420
    public static final String CHECK_NETWORK_URL = "http://www.msftncsi.com/ncsi.txt";
    public static final String CHECK_NETWORK_KEYWORD = "NCSI";
}
