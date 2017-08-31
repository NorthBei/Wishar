package com.pontus.wishar.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pontus.wishar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by NorthBei on 2017/8/31.
 */

public class WebLoginFragment extends DebugLogFragment {

    public static final String WEBLGOIN_LOG_RECEIVE_ACTION = "com.pontus.wishar.WEBLOGIN_LOG_RECEIVE";

    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    Unbinder unbinder;

    public WebLoginFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WebLoginFragment newInstance() {
        WebLoginFragment fragment = new WebLoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_weblogin, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public String getActionName() {
        return WEBLGOIN_LOG_RECEIVE_ACTION;
    }

    @Override
    public void onReceiveLog(String log) {
        TextView t = new TextView(getContext());
        t.setText(log);
        t.setTextColor(Color.parseColor("#4a4a4a"));
        linearLayout.addView(t);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
