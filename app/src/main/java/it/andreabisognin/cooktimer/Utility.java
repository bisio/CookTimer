package it.andreabisognin.cooktimer;

/**
 * Created by bisio on 04/01/15.
 */
public class Utility {
    static String  secondsToPrettyTime(long seconds) {
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d",m,s);
    }
}
