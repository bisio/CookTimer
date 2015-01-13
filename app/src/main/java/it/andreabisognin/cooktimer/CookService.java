package it.andreabisognin.cooktimer;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.media.MediaPlayer;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/**
 * provide timer service for CookTimer activity
 * @author Andrea Bisognin
 */
public class CookService extends Service {

    private final Binder binder = new CookBinder();
    private Activity activity;
    private ICookListenerFunctions callback;
    private final String LOG_TAG="BISIO_SERVICE";
    private boolean timerRunning = false;
    private String lastmessage = null;
    private CountDownTimer timer;
    protected MediaPlayer mp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String message = getString(R.string.notification_running);
        if (timerRunning)
            message = lastmessage;
        Notification notification = Notifier.buildNotification(this, message,null,0);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (binder);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "started service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Soo Cold... Goodbye...");
    }

    private final long ONE_SECOND = 1000;

    private void update(final int value) {
        if (activity == null || callback == null)
            return;

        try {
            Handler lo = new Handler(Looper.getMainLooper());
            lo.post(new Runnable() {
                 public void run() {
                    callback.setTimer(value);
                }
            });
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Exception!!!" + t.getMessage());
        }
    }

    public class CookBinder extends Binder implements ICookServiceFunctions {
        @Override
        public void registerActivity(Activity _activity, ICookListenerFunctions _callback) {
            activity = _activity;
            callback = _callback;
        }

        @Override
        public void unregisterActivity(Activity _activity) {
            activity = null;
            callback = null;
        }

        @Override
        public void startTimer(long seconds) {
            if (timerRunning)
                return;
            timerRunning = true;
            timer = new CountDownTimer(seconds * 1000, 1000) {
                private boolean firstTime = true;
                @Override
                public void onTick(long millisUntilFinished) {
                    //Log.i(LOG_TAG,"tick");

                    if ((millisUntilFinished / 1000)  %  60 == 0 || firstTime) {
                        long minutes = millisUntilFinished/(1000*60);
                        minutes = firstTime? minutes + 1: minutes;
                        lastmessage = "Less than " + minutes + " minutes left";
                        Notifier.notify(CookService.this, lastmessage,null,0);
                        Log.i(LOG_TAG, "sending 'less than' notification");
                        if (firstTime)
                            firstTime = false;
                    }

                    if (callback != null)
                        callback.setTimer(millisUntilFinished/1000);
                }

                @Override
                public void onFinish() {
                    timerRunning = false;
                    if (callback != null)
                        callback.onFinish();
                    startAlarm();
                    sendNotification();
                }
            };
            timer.start();

            Log.i(LOG_TAG, "Started Timer in Service");
        }

        @Override
        public void stopTimer() {
            if (timerRunning) {
                timer.cancel();
                timerRunning = false;
                Log.i(LOG_TAG, "Stopped Timer in Service");
                resetAlarm();
            } else {
                Log.i(LOG_TAG,"Timer not running!");
            }

        }

        @Override
        public void startAlarm() {
            mp = MediaPlayer.create(getApplicationContext(),R.raw.alarm);
            mp.start();
        }

        @Override
        public void stopAlarm() {
            if (mp != null)
                mp.stop();
            mp = null;
        }

        @Override
        public boolean isTimerRunning() {
            return timerRunning;
        }

        @Override
        public void resetAlarm() {
            Log.i(LOG_TAG,"resetting notification");
            Notifier.notify(CookService.this,
                    getString(R.string.notification_running),
                    null,
                    0);
        }
    }


    private void sendNotification() {
        Notifier.notify(this,
                getString(R.string.notification_alarm_title),
                getString(R.string.notification_arlarm_text),
                0);
    }
}
