package it.andreabisognin.cooktimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by bisio on 1/13/15.
 */
public class Notifier {
    public static void notify(Context context, String title, String body, int icon){
        NotificationCompat.Builder  nb = new NotificationCompat.Builder(context);
        nb.setContentTitle(title);
        if (body != null)
            nb.setContentText(body);
        if (icon == 0)
            icon = R.drawable.ic_launcher;
        nb.setSmallIcon(icon);
        nb.setAutoCancel(true);
        Notification notification = nb.build();
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);
    }
}
