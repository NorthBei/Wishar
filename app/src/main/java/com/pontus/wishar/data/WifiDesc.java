package com.pontus.wishar.data;

public class WifiDesc {

    /* Wifi 描述檔 (wifi Description file)
     * 描述一個wifi hot spot本身的type , login url , 認證的domain ... 資訊
     */
    public static final String WISPr = "WISPr" , WEB_LOGIN = "webLogin" , RECORD = "record";
    //WISPs or WebLogin
    private String type;
    //用來認證login service的登入網域認證是否正確
    private String domain;
    //有些service url 可能會包含電腦或手機的mac address or ip address , 是dynamic的, url可能會="dynamic"
    private String url;
    private Script script;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public class Script {
        private String loginJS, success, error_type;

        public String getLoginJS() {
            return loginJS;
        }

        public void setLoginJS(String loginJS) {
            this.loginJS = loginJS;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }

        public String getError_type() {
            return error_type;
        }

        public void setError_type(String error_type) {
            this.error_type = error_type;
        }
    }
}