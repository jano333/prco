package sk.hudak.prco.utils

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class PatternUtils {

    companion object {

        const val NUMBER_AT_LEAST_ONE = "[0-9]{1,}"
        const val SPACE = " "

        @JvmStatic // tato anotacia tu musi byt ak chcem aby dany kotlin kod bolo mozne volat aj priamo z javy !
        fun createMatcher(value: String, vararg groups: String): Matcher {
            return cratePattern(*groups).matcher(value)
        }

        @JvmStatic
        fun cratePattern(vararg groups: String): Pattern {
            val stringJoiner = StringJoiner(")(")
            for (group in groups) {
                stringJoiner.add(group)
            }
            return Pattern.compile("($stringJoiner)")
        }
    }

}
