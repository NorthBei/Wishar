package com.pontus.wishar.storage;

import android.content.Context;

/**
 * Created by NorthBei on 2018/3/26.
 */

public class CheckStorage {
    private static final String CHECKED = "checked";

    private PairDB.PairTable table;

    public CheckStorage(Context context, String tableName){
        table =  PairDB.getInstance(context).getTable(tableName);
    }

    public boolean isChecked(){
        return table.read(CHECKED,false);
    }

    public void setCheckedStatus(boolean isChecked){
        //資料放在記憶體裡面，所以要先改記憶體內的值
        table.write(CHECKED,isChecked).writeDone();
        if(!isChecked){
            remove();
        }
    }

    public void remove(){
        table.removeTable();
    }

    public static boolean isCheckStatusExist(Context context, String tableName){
        return PairDB.isTableExist(context,tableName);
    }
}
