package com.pontus.wishar.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.pontus.wishar.Constants.EXTRA_LOGIN_LOG_RESULT;

/**
 * Created by NorthBei on 2017/8/31.
 */

public abstract class DebugLogFragment extends Fragment {

    private LoginLogReceiver loginLogReceiver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String actionName = getActionName();
        if(actionName == null){
            Log.d("DebugLogFragment", "onCreateView: action is null "+getClass().getName());
        }
        // The filter's action is BROADCAST_ACTION
        IntentFilter intentFilter = new IntentFilter(actionName);
        // Sets the filter's category to DEFAULT , why need do this? http://fanli7.net/a/caozuoxitong/android/2011/1103/139302.html
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        loginLogReceiver = new LoginLogReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(loginLogReceiver, intentFilter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(loginLogReceiver !=null){
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(loginLogReceiver);
        }
    }

    public abstract String getActionName();
    public abstract void onReceiveLog(String log);

    private class LoginLogReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String log = intent.getStringExtra(EXTRA_LOGIN_LOG_RESULT);
            onReceiveLog(log);
//            for (String log: logResult) {
//
//                Log.d(TAG,log);
//            }
        }
    }
}
