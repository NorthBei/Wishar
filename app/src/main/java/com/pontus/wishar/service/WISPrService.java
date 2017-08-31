package com.pontus.wishar.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pontus.wishar.Constants;
import com.pontus.wishar.view.fragment.WISPrFragment;
import com.pontus.wishar.wispr.EzWISPr;

import java.util.ArrayList;

import static com.pontus.wishar.Constants.EXTRA_LOGIN_LOG_RESULT;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class WISPrService extends IntentService {
    final static String TAG = WISPrService.class.getSimpleName();

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.pontus.wishar.service.action.FOO";
    public static final String ACTION_BAZ = "com.pontus.wishar.service.action.BAZ";

    public static final String EXTRA_PARAM1 = "com.pontus.wishar.service.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.pontus.wishar.service.extra.PARAM2";

    public WISPrService() {
        super("WISPrService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            String account = intent.getStringExtra(Constants.EXTRA_LOGIN_ACCOUNT);
            String password = intent.getStringExtra(Constants.EXTRA_LOGIN_PASSWORD);
            Log.d(TAG, "onHandleIntent: account:"+account+" password:"+password);
            EzWISPr ez = new EzWISPr();
            ArrayList<String> logResult = ez.loginWithLog(account,password);

            Intent localIntent = new Intent(WISPrFragment.WISPR_LOG_RECEIVE_ACTION)
                    // Puts the data into the Intent
                    .putExtra(EXTRA_LOGIN_LOG_RESULT, logResult);

            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

}
