package com.pontus.wishar.data;


import java.util.ArrayList;
import java.util.List;

public class Web {
    String SSID, Type, Account, Password, Wispr;
    private Script Script = new Script();

    public String getAccount() {
        return Account;
    }

    public String getPassword() {
        return Password;
    }

    public String getSSID() {
        return SSID;
    }

    public String getType() {
        return Type;
    }

    public String getWispr() {
        return Wispr;
    }

    public Script getScript() {
        return Script;
    }

    public class Script {
        private String Url, CheckWeb, LoginJS, Success;
        private List<Errortype> Errortype = new ArrayList<>();

        public String getCheckWeb() {
            return CheckWeb;
        }

        public String getUrl() {
            return Url;
        }

        public String getLoginJS() {
            return LoginJS;
        }

        public String getSuccess() {
            return Success;
        }

        public List<Errortype> getWeberrortype() {
            return Errortype;
        }

        public class Errortype {
            String Loginfail, Netfail;

            public String getLoginfail() {
                return Loginfail;
            }

            public String getNetfail() {
                return Netfail;
            }
        }


    }

}







