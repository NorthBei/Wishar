package com.pontus.wishar.map;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.pontus.wishar.storage.db.Wifi;
import com.pontus.wishar.storage.db.WisharDB;

import ch.hsr.geohash.GeoHash;

public class MapController {

    private static final String FORMAT = "UTF-8";
    private Context context;
    private GoogleMap mMap;
    private WifiData[] wifiList;
    private WisharDB instance;
    private GeoHash geoHash;
    private Gson gson;
    private String[] listItems;

    public MapController(Context context, GoogleMap googleMap, String[] listItems) {
        this.instance = WisharDB.getDB(context);
        this.context = context;
        this.listItems = listItems;
        mMap = googleMap;
        gson = new Gson();
    }

    public void ShowArea(Double lat, Double lng, Integer[] selected) {
        ShowArea(lat,lng,selected,7 );
    }
    public void ShowArea(Double lat, Double lng, Integer[] selected,int precision) {
        mMap.clear();
        geoHash = geoHash.withCharacterPrecision(lat, lng, precision);
        Wifi[] wifidata;

        int len = 6;
        do {
            wifidata = instance.mapDao().loadAreawifis(geoHash.toBase32().substring(0, len--) + "%");
        } while (wifidata.length == 0 && len > 0);

        LatLng sydney;

        for (int i = 0; i < wifidata.length; i++) {

            WifiData[] wifiList = gson.fromJson(wifidata[i].WifiData, WifiData[].class);

            for (int k = 0, j = 0; j < wifiList.length; j++) {
                for(;k<selected.length; k++) {
                    if (listItems[selected[k]].equals(wifiList[j].getType())) {
                        sydney = new LatLng(Double.parseDouble(wifiList[j].getLat()), Double.parseDouble(wifiList[j].getLng()));
                        mMap.addMarker(new MarkerOptions().position(sydney).title(wifiList[j].getType() + " " + wifiList[j].getName()).snippet(wifiList[j].getAddr()));
                        break;
                    }
                }
            }
        }
    }

    public void MoveMap(LatLng position, int size) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((position), size));
    }

    public void MoveMapAnim(LatLng position, int size) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((position), size));
    }
}
