package it.andreabisognin.cooktimer;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

/**
 * provide timer service for CookTimer activity
 * @author Andrea Bisognin
 */
public class CookService extends Service {

    private final Binder binder = new CookBinder();
    private Activity activity;
    private ICookListenerFunctions callback;


    @Override
    public IBinder onBind(Intent intent) {
        return (binder);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("BISIO", "started service");
        new Thread(fakeCountDownTimer).start();
    }

    private final long ONE_SECOND = 1000;

    private Runnable fakeCountDownTimer = new Runnable() {
        @Override
        public void run() {
            for (int i = 10; i > 0; i--) {
                update(i);
                Log.i("BISIO","this is the number: "+i);
                SystemClock.sleep(ONE_SECOND * 2);
            }
        }
    };


    private void update(final int value) {
        if (activity == null || callback == null)
            return;

        try {
            Handler lo = new Handler(Looper.getMainLooper());
            lo.post(new Runnable() {
                 public void run() {
                    //Log.i("BISIO","trying to run");
                    callback.setTimer(value);
                }
            });
        } catch (Throwable t) {
            Log.e("BISIO", "Exception!!!" + t.getMessage());
        }
    }


    public class CookBinder extends Binder implements ICookServiceFunctions {
        @Override
        public void registerActivity(Activity _activity, ICookListenerFunctions _callback) {
            activity = _activity;
            callback = _callback;
        }

        @Override
        public void unregisterActivity(Activity activity) {
            activity = null;
            callback = null;
        }
    }
}
