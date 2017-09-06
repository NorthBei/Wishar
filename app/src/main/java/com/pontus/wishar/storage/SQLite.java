package com.pontus.wishar.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NorthBei on 2017/9/2.
 */

public class SQLite extends SQLiteOpenHelper {

    //reference https://alumincan.github.io/2017/02/09/2017-02-09-1398988/

    // 資料庫名稱
    private static final String DATABASE_NAME = "wisharx.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    private  static final int DATABASE_VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private SQLiteDatabase database;
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Data";
    private static final String TABLE_NAME = "Data";

    private static SQLite instance = null;

    // 建構子，在一般的應用都不需要修改
    private  SQLite(Context context) {
        super (context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    public static synchronized SQLite getInstance(Context context) {
        if (context == null) {
            instance = new SQLite(context);
        }
        return instance;
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public  SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new SQLite(context).getWritableDatabase();
        }
        return database;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate 指的是如果Android載入app時發現沒有Database檔案，才會call，不是每次都會呼叫到
        StringBuilder sqlCommand = new StringBuilder();
        sqlCommand.append("CREATE  TABLE IF NOT EXISTS").append(TABLE_NAME)
                .append("(")
                .append("ssid TEXT PRIMARY KEY NOT NULL ,")
                .append("account TEXT VARCHAR ,")
                .append("password TEXT VARCHAR")
                .append(")");

        db.execSQL(sqlCommand.toString());
//        db.execSQL("CREATE  TABLE IF NOT EXISTS"  +TABLE_NAME +
//                "( id INTEGER PRIMARY KEY  NOT NULL , " +
//                "name VARCHAR  , " +
//                "account VARCHAR  , " +
//                "password VARCHAR, " );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade 則是如果資料庫結構有改變了就會觸發 onUpgrade
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    //刪除
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void updateEntity(String col_name,String find_value,String set_value){
        ContentValues values = new ContentValues();
        values.put(col_name, set_value);
        // Which row to update, based on the ID
        String selection =col_name+ " LIKE ?";
        String[] selectionArgs = { find_value};
        int count = database.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    //搜尋
    public String[] searchEntity(String value){
        String selectQuery = "SELECT * FROM "+TABLE_NAME+" WHERE name ="+value;
        Cursor c = database.rawQuery(selectQuery, null);
        String account="",password="";
        if(c.moveToFirst()){
            do{
                //id,name,account,password
                account = c.getString(2);
                password = c.getString(3);
                //Do something Here with values
            }while(c.moveToNext());
        }
        c.close();
        String[] data = {account,password};
        return data;
    }
    //Insert
    public void insert(String name,String account,String password){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("account", account);
        values.put("password", password);
        long id = database.insert(TABLE_NAME, null, values);
    }
}
