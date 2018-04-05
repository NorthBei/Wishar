package com.pontus.wishar;

public class Constants {
    /*for WifiBroadcastReceiver intent key*/
    public static final String EXTRA_WIFI_DESC_NAME = "com.pontus.wishar.service.extra.wifi_desc_name";
    public static final String JSON_FILE_EXTENSION = ".json";
    public static final String USER_AGENT = "Android-Wifi/0.1";//"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
    public static final String CHECK_URL = "http://clients3.google.com/generate_204";
    public static final String ACCEPT_LANGUAGE = "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7";
    public static final String ACCEPT_ENCODE = "gzip,deflate";
    public static final int MAX_TRY_CONNECT = 20;
    public static final String WIFI_DESC_SCRIPT_PREFIX = "$";

    // about ncsi
    // https://dotblogs.com.tw/swater111/2014/01/09/139420
    public static final String CHECK_NETWORK_URL = "http://www.msftncsi.com/ncsi.txt";
    public static final String CHECK_NETWORK_KEYWORD = "NCSI";

    //Exception
    public static final String FORM_ACTION_METHOD_NOT_SUPPORT = "Wishar出了點問題，不好意思";
    public static final String DESC_ERROR_CANNOT_FIND_FORM_BY_SELECTOR = "Wishar出了點問題，不好意思";
    public static final String NETWORK_ERROR_RETURN_DATA_IS_NULL = "這個WIFI沒有回傳任何資料，Wishar已停止";
}
