package com.pontus.wishar.map;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapController {

    private static final String FORMAT = "UTF-8";
    private Context context;
    private GoogleMap mMap;
    WifiData[] wifiList;

    public MapController(Context context,GoogleMap googleMap){
        this.context = context;
        mMap = googleMap;
    }

    public String readFile(String fileName){
        String fileContent = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            fileContent = new String(buffer, FORMAT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent;
    }

    public <T> T  readJsonToObj(String fileName, Type type){
        //1.maybe return null 2.just pass json filename , auto add .json at last
        String data= readFile(fileName);
        Log.d("data",data);
        return new Gson().fromJson(data , type);
    }

    public void controller(String filename,String title){

        wifiList=readJsonToObj(filename,WifiData[].class);
        // Add a marker in Sydney and move the camera

        LatLng sydney= new LatLng(Double.parseDouble(wifiList[0].getLat()), Double.parseDouble(wifiList[0].getLng()));
        for(int i=1;i<wifiList.length;i++){
            sydney = new LatLng(Double.parseDouble(wifiList[i].getLat()), Double.parseDouble(wifiList[i].getLng()));
        mMap.addMarker(new MarkerOptions().position(sydney).title(title+" "+wifiList[i].getName()).snippet(wifiList[i].getAddr()));
        }


    }
    public void MoveMap(LatLng position,int size){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((position),size));
    }
   }
