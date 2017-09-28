package com.pontus.wishar.storage;

import android.content.Context;


public class AccountStorage {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String POSTFIX = "postfix";

    private PairDB.PairTable table;

    public AccountStorage(Context context,String tableName){
        table =  PairDB.getInstance(context).getTable(tableName);
    }

    public String getAccount(){
        return table.read(ACCOUNT,null);
    }

    public String getPassword(){
        return table.read(PASSWORD,null);
    }

    public String getPostfix(){
        return table.read(POSTFIX,null);
    }

    public void setLoginInfo(String account, String password,String postfix){
        table.write(ACCOUNT,account).write(PASSWORD,password).write(POSTFIX,postfix).writeDone();
    }

    public void removeAccount(){
        table.removeTable();
    }

    public static boolean isAccountExist(Context context, String tableName){
        return PairDB.isTableExist(context,tableName);
    }
}
