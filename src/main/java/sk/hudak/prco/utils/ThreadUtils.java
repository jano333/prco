package sk.hudak.prco.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class ThreadUtils {

    private ThreadUtils() {
    }

    /**
     * Will sleep between 5 to 20 second
     */
    public static void sleepRandomSafe() {
        sleepRandomSafeBetween(5, 20);
    }

    public static void sleepRandomSafeBetween(int min, int max) {
        sleepSafe(new Random().nextInt(max - min + 1) + min);
    }

    public static void sleepSafe(int second) {
        try {
            log.debug("start sleeping for {} sec", second);
            Thread.sleep(1000l * second);
//            log.debug("end sleeping");

        } catch (InterruptedException e) {
            //FIXME log
            e.printStackTrace();
        }
    }
}
