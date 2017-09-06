package com.pontus.wishar.storage;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by NorthBei on 2017/9/4.
 */

public class AccountStorage {

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";

    private PairDB.PairTable table;

    public AccountStorage(Context context,String tableName){
        table =  PairDB.getInstance(context).getTable(tableName);
    }

    public HashMap<String,String> getLoginInfo(){
        HashMap<String,String> info = new HashMap<>();
        info.put(ACCOUNT,table.read(ACCOUNT,null));
        info.put(PASSWORD,table.read(PASSWORD,null));
        return info;
    }

    public void setLoginInfo(String account, String password){
        table.write(ACCOUNT,account).write(PASSWORD,password).writeDone();
    }

    public void removeAccount(){
        table.remove(ACCOUNT);
        table.remove(PASSWORD);
        //will delete ssid.xml file , or will find when wifi connection
    }

    public static boolean isAccountExist(Context context, String tableName){
        return PairDB.isTableExist(context,tableName);
    }
}
