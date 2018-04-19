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
    //Location location; // location

    public Firebase(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

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
  
       /* getLocation();
        String lat="",lng="";
        try{
            lat=Double.toString(location.getLatitude()) ;
            lng=Double.toString(location.getLongitude());

        }catch (Exception e){

        }*/

        Date currentTime = Calendar.getInstance().getTime();
        String mac =getNewMac();
        Bundle bundle= new Bundle();

        //bundle.putString("Latitude", lat);
        //bundle.putString("Longitude",lng);
        bundle.putString("SsidName",name);
        bundle.putString("SsidMac",mac);
        //bundle.putString("Speed",speed);
        bundle.putString("ConnentTime",currentTime.toString());
        //bundle.putString("ConnentResult",result);
        //bundle.putString("DeviceId",deviceId);
        mFirebaseAnalytics.logEvent("LoginInfo", bundle);


        Timber.d("name: "+name+" ,mac: "+mac+" ,speed: "+speed+" ,currentTime: "+currentTime.toString()+" ,deviceId: "+deviceId );

    }
/*
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }*/
   /* public Location getLocation() {
        // The minimum distance to change Updates in meters
         final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;
        LocationManager locationManager;

        double latitude; // latitude
        double longitude; // longitude

        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
*/

}
