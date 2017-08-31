package com.pontus.wishar.view.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pontus.wishar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WISPrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WISPrFragment extends DebugLogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String WISPR_LOG_RECEIVE_ACTION = "com.pontus.wishar.WISPR_LOG_RECEIVE";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.account) TextView account;
    @BindView(R.id.password) TextView password;
    @BindView(R.id.editAccount) EditText editAccount;
    @BindView(R.id.editPassword) EditText editPassword;
    @BindView(R.id.wifiSpinner) Spinner wifiSpinner;
    @BindView(R.id.loginButton) Button loginButton;
    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    Unbinder unbinder;

    private OnFragmentInteractionListener mListener;

    public WISPrFragment() {
            // Required empty public constructor
    }

    public static WISPrFragment newInstance() {
        WISPrFragment fragment = new WISPrFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wispr, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public String getActionName() {
        return WISPR_LOG_RECEIVE_ACTION;
    }

    @Override
    public void onReceiveLog(String log) {
        TextView t = new TextView(getContext());
        t.setText(log);
        t.setTextColor(Color.parseColor("#4a4a4a"));
        linearLayout.addView(t);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        return;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
