package it.andreabisognin.cooktimer;

/**
 * Created by bisio on 04/01/15.
 */
public class Utility {
    static String  secondsToPrettyTime(long seconds) {
        long h = seconds / 3600;
        long rest = seconds % 3600;
        long m = rest / 60;
        long s = rest % 60;
        return String.format("%01d:%02d:%02d",h,m,s);
    }
}
