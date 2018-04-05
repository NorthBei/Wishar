package com.pontus.wishar.notify;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.pontus.wishar.R;

import timber.log.Timber;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

public class NotificationCenter {

    private final static int NOTIFY_ID = 1207;
    private static NotificationCenter instance;
    private NotificationManager notificationManager;
    private Context context;
    private static String SSID = "";

    public static synchronized NotificationCenter getInstance(Context context){
        if(instance == null){
            instance = new NotificationCenter(context);
        }
        return instance;
    }

    // private constructor，這樣其他物件就沒辦法直接用new來取得新的實體
    private NotificationCenter(Context context){
        this.context = context.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void cleanNotify(){
        notificationManager.cancel(NOTIFY_ID);
    }

    public void showNotify(String msg){
        String content = String.format("%s:%s",SSID,msg);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                //.setContentIntent(pendingIntent)
                .setPriority(PRIORITY_MAX)
                .setSound(uri)
                .setAutoCancel(true);

        // use uid to be notifyID
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
        Timber.d("notifyUser:%s",content);
    }

    public static void setSsid(String SSID) {
        NotificationCenter.SSID = SSID;
    }
}
