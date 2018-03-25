package com.pontus.wishar.storage;

import android.content.Context;

import com.google.gson.Gson;
import com.pontus.wishar.data.WifiDesc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import static com.pontus.wishar.Constants.JSON_FILE_EXTENSION;

/**
 * Created by NorthBei on 2017/9/23.
 */

public class AssetsStorage {

    private static final String FORMAT = "UTF-8";
    private static final String WIFI_DESC_FOLDER = "wifiDesc";

    private Context context;

    public AssetsStorage(Context context){
        this.context = context;
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

    public <T> T getWifiDescObj(String fileName){
        //add wifiDesc folder
        return readJsonToObj(WIFI_DESC_FOLDER+"/"+ fileName, WifiDesc.class);
    }

    public <T> T readJsonToObj(String fileName, Type type){
        //1.maybe return null 2.just pass json filename , auto add .json at last
        return new Gson().fromJson(readFile(fileName+JSON_FILE_EXTENSION) , type);
    }

    public static String[] getWifiDescList(Context context){
        String [] list;
        try {
            list = context.getAssets().list(WIFI_DESC_FOLDER);
        }
        catch (IOException e) {
            e.printStackTrace();
            list = new String[0];
        }
        //remove .json file extension
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].replace(JSON_FILE_EXTENSION,"").replace("$.",".");
        }
        return list;
    }
}
