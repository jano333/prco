package sk.hudak.prco.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public final class ThreadUtils {

    private ThreadUtils() {
    }

    /**
     * Will sleep between 5 to 20 second
     */
    public static void sleepRandomSafe() {
        sleepRandomSafeBetween(5, 20);
    }

    public static void sleepRandomSafeBetween(int min, int max) {
        sleepSafe(generateRandomSecondInInterval(min, max));
    }

    public static int generateRandomSecondInInterval() {
        return generateRandomSecondInInterval(5, 20);
    }


    public static int generateRandomSecondInInterval(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static void sleepSafe(int second) {
        try {
            log.debug("start sleeping for {} sec", second);
            Thread.sleep(1000l * second);

        } catch (InterruptedException e) {
            //FIXME log
            e.printStackTrace();
        }
    }
}
