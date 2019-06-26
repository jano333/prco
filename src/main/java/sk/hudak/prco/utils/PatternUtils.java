package sk.hudak.prco.utils;

import lombok.NoArgsConstructor;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public final class PatternUtils {

    public static final String NUMBER_AT_LEAST_ONE = "[0-9]{1,}";
    public static final String SPACE = " ";

    public static Matcher createMatcher(String value, String... groups) {
        return cratePattern(groups).matcher(value);
    }

    public static Pattern cratePattern(String... groups) {
        StringJoiner stringJoiner = new StringJoiner(")(");
        for (String group : groups) {
            stringJoiner.add(group);
        }
        return Pattern.compile("(" + stringJoiner.toString() + ")");
    }
}
