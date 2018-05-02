package com.pontus.wishar.network;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.location.*;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;


public class Firebase   {

    private FirebaseAnalytics mFirebaseAnalytics;
    Location location;
    String lat,lng;

    public Firebase(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        location = LocationUtils.getInstance( context ).showLocation();
        if (location != null) {

            lat=Double.toString(location.getLatitude());
            lng=Double.toString(location.getLongitude());
            String address = "Latitude：" +lat  + "Longitude：" + lng;
            Timber.d("LocationUtils"+ address );
        }



    }

    public void errorReport(String id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
    /**
     * 手機之wifi mac
     * return
     */
    private static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    public void LoginInfo(String name,  String speed,  String result, String deviceId,String type) {
        //Timber.d("name: "+name+" ,speed: "+speed+" ,result: "+result+" ,deviceId: "+deviceId);


        Date currentTime = Calendar.getInstance().getTime();
        String mac =getNewMac();
        Bundle bundle= new Bundle();

        bundle.putString("Latitude", lat);
        bundle.putString("Longitude",lng);
        bundle.putString("SsidName",name);
        bundle.putString("SsidMac",mac);
        //bundle.putString("Speed",speed);
        bundle.putString("ConnentTime",currentTime.toString());
        //bundle.putString("ConnentResult",result);
        //bundle.putString("DeviceId",deviceId);
        mFirebaseAnalytics.logEvent("LoginInfo", bundle);


        Timber.d("name: "+name+" ,mac: "+mac+" ,speed: "+speed+" ,currentTime: "+currentTime.toString()+" ,deviceId: "+deviceId );

    }

}
