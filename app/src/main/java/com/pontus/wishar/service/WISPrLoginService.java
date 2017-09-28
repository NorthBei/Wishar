package com.pontus.wishar.service;

import com.pontus.wishar.storage.AccountStorage;
import com.pontus.wishar.view.fragment.WISPrFragment;
import com.pontus.wishar.wispr.LightWISPr;

import java.util.ArrayList;

public class WISPrLoginService extends AbstractLoginService {
    public WISPrLoginService() {}

    @Override
    protected void onNetworkUnavailable(String webHTML) {
        AccountStorage as = getAccountStorage();
        String account = as.getAccount();
        String password = as.getPassword();
        String postfix = as.getPostfix();

        log("onNetworkUnavailable: account:"+account+postfix+" password:"+password);

        LightWISPr lwp = new LightWISPr(account+postfix,password);
        ArrayList<String> logList = lwp.login(webHTML);

        for (String s: logList) {
            sendLog(s);
        }
    }

    @Override
    protected String getBroadcastAction() {
        return WISPrFragment.WISPR_LOG_RECEIVE_ACTION;
    }

    @Override
    protected String getTAG() {
        return WISPrLoginService.class.getSimpleName();
    }

    @Override
    protected boolean isRunOnMainThread() {
        return false;
    }
}
