package com.pontus.wishar.data;

import java.util.List;

public class WifiDesc {

    public static final String WISPr = "WISPr";
    /**
     * ssid : iTaiwan
     * description : 中央行政機關室內公共區域提供免費無線上網
     * protocol : https
     * domain : wlangw.hinet.net
     * roamingSupport : ["TPE-Free"]
     * isNeedLogin : true
     * type : WISPr
     * parse : {}
     * loginType : [{"displayName":"iTaiwan","accountPostfix":"@itw"}]
     */

    private String ssid;
    private String description;
    private String protocol;
    private String domain;
    private boolean isNeedLogin;
    private String type;
    private Parse parse;
    private List<String> roamingSupport;
    private List<LoginType> loginType;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isIsNeedLogin() {
        return isNeedLogin;
    }

    public void setIsNeedLogin(boolean isNeedLogin) {
        this.isNeedLogin = isNeedLogin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Parse getParse() {
        return parse;
    }

    public void setParse(Parse parse) {
        this.parse = parse;
    }

    public List<String> getRoamingSupport() {
        return roamingSupport;
    }

    public void setRoamingSupport(List<String> roamingSupport) {
        this.roamingSupport = roamingSupport;
    }

    public List<LoginType> getLoginType() {
        return loginType;
    }

    public void setLoginType(List<LoginType> loginType) {
        this.loginType = loginType;
    }

    public static class Parse {
    }

    public static class LoginType {

        private String displayName;
        private String accountPostfix;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAccountPostfix() {
            return accountPostfix;
        }

        public void setAccountPostfix(String accountPostfix) {
            this.accountPostfix = accountPostfix;
        }
    }

    /* Wifi 描述檔 (wifi Description file)
     * 描述一個wifi hot spot本身的type , login url , 認證的domain ... 資訊
     */

}