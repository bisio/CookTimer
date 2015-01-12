package it.andreabisognin.cooktimer;

import android.app.Activity;

/**
 * Interfaces for the CookService.
 * @author Andrea Bisognin
 */
public interface ICookServiceFunctions {
    void registerActivity(Activity activity, ICookListenerFunctions callback);
    void unregisterActivity(Activity activity);
    void startTimer(long seconds);
    void stopTimer();
    void startAlarm();
    void stopAlarm();
    boolean isTimerRunning();
    void resetAlarm();
}
