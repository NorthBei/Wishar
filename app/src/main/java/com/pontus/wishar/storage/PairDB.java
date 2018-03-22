package com.pontus.wishar.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PairDB {

    private static String TAG = PairDB.class.getSimpleName();
    private static PairDB instance;
    private Context context;

    //Tray is an other choice for easy use SharedPreferences https://github.com/grandcentrix/tray

    private PairDB(Context context) {
        this.context = context;
    }

    public synchronized static PairDB getInstance(Context context){
        if (instance == null) {
            instance = new PairDB(context);
        }
        return instance;
    }

    public PairTable getTable(String tableName){
        return new PairTable(context,tableName);
    }

    public static boolean isTableExist(Context context,String tableName){
        //sharedPreferences API 會在 /data/data/packagename/shared_prefs/ 目錄下產生一個 file_name.xml
        File prefsdir = new File(context.getApplicationInfo().dataDir,"shared_prefs");
        //format of fileName is xyz.xml
        String fileName = tableName+".xml";
        if(prefsdir.exists() && prefsdir.isDirectory()){
            //全部sharedPreferences儲存的file
            String[] list = prefsdir.list();
            List<String> fileList = Arrays.asList(list);
//            for (String s: fileList) {
//                Timber.d("isTableExist: fileName:"+s);
//            }
            return fileList.contains(fileName);
        }
        return false;
    }

    public class PairTable{
        private SharedPreferences readOnlyTable;
        private SharedPreferences.Editor writeOnlyTable;
        private String tableName;

        public PairTable(Context context,String tableName){
            this.tableName = tableName;
            readOnlyTable = context.getSharedPreferences(tableName, Context.MODE_PRIVATE);
            writeOnlyTable = readOnlyTable.edit();
        }

        public PairTable write(@NonNull String key,@NonNull String value){
            writeOnlyTable.putString(key,value);
            return this;
        }

        public PairTable write(@NonNull String key,@NonNull int value){
            writeOnlyTable.putInt(key,value);
            return this;
        }

        public PairTable write(@NonNull String key,@NonNull boolean value){
            writeOnlyTable.putBoolean(key,value);
            return this;
        }

        public void writeDone(){
            writeOnlyTable.apply();
        }

        public String read(@NonNull String key,String defalut){
            return readOnlyTable.getString(key,defalut);
        }

        public int read(@NonNull String key,int defalut){
            return readOnlyTable.getInt(key,defalut);
        }

        public boolean read(@NonNull String key,boolean defalut){
            return readOnlyTable.getBoolean(key,defalut);
        }

        public void remove(@NonNull String key){
            writeOnlyTable.remove(key).apply();
        }

        public void removeTable(){
            //writeOnlyTable.clear().commit();
            File table = new File(context.getApplicationInfo().dataDir,"shared_prefs/"+tableName+".xml");
            if(table.exists())
                table.delete();
        }

    }
}
