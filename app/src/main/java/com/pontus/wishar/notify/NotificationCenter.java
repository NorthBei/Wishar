package com.pontus.wishar.notify;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.pontus.wishar.R;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class NotificationCenter {

    private final static int NOTIFY_ID = 1207;
    private static NotificationCenter instance;
    private NotificationManager notificationManager;
    private Context context;

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

    public void showNotify(String content){
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Wishar")
                .setContentText(content)
                //.setContentIntent(pendingIntent)
                .setPriority(PRIORITY_HIGH)
                .setAutoCancel(true);
        //.setPriority(Notification.PRIORITY_HIGH);

        // use uid to be notifyID
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }
}
