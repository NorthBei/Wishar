package com.pontus.wishar.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.pontus.wishar.Constants.WIFI_DESC_SCRIPT_PREFIX;

public class WifiDesc {

    public static final String WISPr = "WISPr";
    public static final String PARSE = "Parse";

    private String scriptName;
    private String description;
    private String protocol;
    private String domain;
    private boolean isNeedLogin;
    private String type;
    private List<String> roamingSupport;
    private List<LoginType> loginType;
    private List<String> redirectPageHost;
    private String jumpToUrl;
    @SerializedName("parse")
    private List<Parse> parseList;

    public String getJumpToUrl() {
        return jumpToUrl;
    }

    public void setJumpToUrl(String jumpToUrl) {
        this.jumpToUrl = jumpToUrl;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getSsid() {
        return getScriptName().replace(WIFI_DESC_SCRIPT_PREFIX,"");
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
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

    public boolean isNeedLogin() {
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

    public List<Parse> getParseList() {
        return parseList;
    }

    public void setParseList(List<Parse> parseList) {
        this.parseList = parseList;
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

    public List<String> getRedirectPageHost() {
        return redirectPageHost;
    }

    public void setRedirectPageHost(List<String> redirectPageHost) {
        this.redirectPageHost = redirectPageHost;
    }

    public String getUrl(){
        return String.format("%s://%s/",getProtocol(),getDomain());
    }

    public static class Parse {
        private String actionUrl;
        private String formSelector;
        private List<KeyValue> inputData;

        public String getActionUrl() {
            return actionUrl;
        }

        public void setActionUrl(String actionUrl) {
            this.actionUrl = actionUrl;
        }

        public String getFormSelector() {
            return formSelector;
        }

        public void setFormSelector(String formSelector) {
            this.formSelector = formSelector;
        }

        public List<KeyValue> getInputData() {
            return inputData;
        }

        public void setInputData(List<KeyValue> inputData) {
            this.inputData = inputData;
        }

        public static class KeyValue {

            public static final String LOAD_USERNAME = "%username%";
            public static final String LOAD_PASSWORD = "%password%";

            private String key;
            private String value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
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
}