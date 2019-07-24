package sk.hudak.prco.utils

import org.slf4j.LoggerFactory
import java.util.*


object ThreadUtils {

    val log = LoggerFactory.getLogger(ThreadUtils::class.java)

    /**
     * Will sleep between 5 to 20 second
     */
    @JvmStatic
    fun sleepRandomSafe() {
        sleepRandomSafeBetween(5, 20)
    }

    @JvmStatic
    fun sleepRandomSafeBetween(min: Int, max: Int) {
        val second = generateRandomSecondInInterval(min, max)
        sleepSafe(second)
    }

    @JvmStatic
    @JvmOverloads
    fun generateRandomSecondInInterval(min: Int = 5, max: Int = 20): Int {
        return Random().nextInt(max - min + 1) + min
    }

    @JvmStatic
    fun sleepSafe(second: Int) {
        try {
            log.debug("start sleeping for $second sec")
            Thread.sleep(1000L * second)

        } catch (e: InterruptedException) {
            //FIXME log
            e.printStackTrace()
        }

    }
}
