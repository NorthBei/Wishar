package com.pontus.wishar.storage;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by NorthBei on 2017/9/23.
 */

public class AssetsStorage {

    private static final String FORMAT = "UTF-8";

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

    public <T> T readFileToJson(String fileName,Type type){
        //maybe return null
        return new Gson().fromJson(readFile(fileName) , type);
    }
}
