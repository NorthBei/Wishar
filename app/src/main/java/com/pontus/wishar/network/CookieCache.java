package com.pontus.wishar.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieCache implements CookieJar{

    private Map<String,List<Cookie>> cookieCache = new HashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieCache.put(url.host(),cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if(cookieCache.containsKey(url.host())){
            return cookieCache.get(url.host());
        }
        //不能return null
        return new ArrayList<Cookie>();
    }
}
