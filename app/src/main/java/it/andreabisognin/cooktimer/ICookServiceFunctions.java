package it.andreabisognin.cooktimer;

import android.app.Activity;

/**
 * Interfaces for the CookService.
 * @author Andrea Bisognin
 */
public interface ICookServiceFunctions {
    void registerActivity(Activity activity, ICookListenerFunctions callback);
    void unregisterActivity(Activity activity);

}
