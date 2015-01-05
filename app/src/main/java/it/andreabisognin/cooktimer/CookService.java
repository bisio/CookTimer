package it.andreabisognin.cooktimer;

import android.app.Activity;
import android.app.Service;
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
    private CountDownTimer timer;


    @Override
    public IBinder onBind(Intent intent) {
        return (binder);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "started service");
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
                @Override
                public void onTick(long millisUntilFinished) {
                    if (callback != null)
                        callback.setTimer(millisUntilFinished/1000);
                }

                @Override
                public void onFinish() {
                    timerRunning = false;
                    if (callback != null)
                        callback.onFinish();
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
            } else {
                Log.i(LOG_TAG,"Timer not running!");
            }

        }
    }
}
