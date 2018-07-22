package com.pontus.wishar.map;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pontus.wishar.storage.db.*;

import java.util.List;

import ch.hsr.geohash.GeoHash;

public class MapController {

    private static final String FORMAT = "UTF-8";
    private Context context;
    private GoogleMap mMap;
    WifiData[] wifiList;
    WisharDB instance;
    GeoHash geoHash;
    Gson gson;
    String[] listItems;

    public MapController(Context context, GoogleMap googleMap, String[] listItems) {
        this.instance = WisharDB.getDB(context);
        this.context = context;
        this.listItems = listItems;
        mMap = googleMap;
        gson = new Gson();

    }

    public void ShowArea(Double lat, Double lng, Integer[] sellected) {
        mMap.clear();
        geoHash = geoHash.withCharacterPrecision(lat, lng, 7);
        Wifi[] wifidata;
        int len = 6;
        do {
            wifidata = instance.mapDao().loadAreawifis(geoHash.toBase32().substring(0, len--) + "%");
        } while (wifidata.length == 0 && len > 0);

        LatLng sydney;

        for (int i = 0; i < wifidata.length; i++) {

            WifiData[] wifilist = gson.fromJson(wifidata[i].WifiData, WifiData[].class);

            for (int k = 0, j = 0; j < wifilist.length; j++) {
                for(;k<sellected.length; k++) {
                    if (listItems[sellected[k]].equals(wifilist[j].getType())) {
                        sydney = new LatLng(Double.parseDouble(wifilist[j].getLat()), Double.parseDouble(wifilist[j].getLng()));
                        mMap.addMarker(new MarkerOptions().position(sydney).title(wifilist[j].getType() + " " + wifilist[j].getName()).snippet(wifilist[j].getAddr()));
                        break;
                    }
                }
            }
        }
    }

    public void MoveMap(LatLng position, int size) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((position), size));

    }
}
