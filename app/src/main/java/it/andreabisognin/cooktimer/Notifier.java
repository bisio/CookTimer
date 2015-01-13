package it.andreabisognin.cooktimer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Just a class with static methods to put in one place notification related code
 */
public class Notifier {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Notification buildNotification(Context context, String title, String body, int icon) {
        NotificationCompat.Builder  nb = new NotificationCompat.Builder(context);
        nb.setContentTitle(title);
        if (body != null)
            nb.setContentText(body);
        if (icon == 0)
            icon = R.drawable.ic_launcher;
        nb.setSmallIcon(icon);
        nb.setAutoCancel(true);
        Intent intent = new Intent(context,CookTimer.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(CookTimer.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        nb.setContentIntent(pendingIntent);
        Notification notification = nb.build();
        return notification;
    }

    public static void notify(Context context, String title, String body, int icon){
        Notification notification = Notifier.buildNotification(context,title,body,icon);
        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);
    }
}
