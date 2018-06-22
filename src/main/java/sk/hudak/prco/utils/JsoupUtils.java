package sk.hudak.prco.utils;

import lombok.NonNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class JsoupUtils {

    private JsoupUtils() {
    }

    public static boolean existElement(@NonNull Document document, @NonNull String cssQuery) {
        return !notExistElement(document, cssQuery);
    }

    public static boolean notExistElement(@NonNull Document document, @NonNull String cssQuery) {
        return document.select(cssQuery).isEmpty();
    }

    public static Optional<Element> getFirstElementByClass(@NonNull Document document, @NonNull String className) {
        return Optional.ofNullable(document.getElementsByClass(className).first());
    }

    public static Optional<String> getTextFromFirstElementByClass(@NonNull Document document, @NonNull String className) {
        Optional<Element> firstElementByClass = getFirstElementByClass(document, className);
        if (!firstElementByClass.isPresent()) {
            return Optional.empty();
        }
        //TODO NPE
        String text = firstElementByClass.get().getAllElements().get(0).text();
        return Optional.of(text);
    }

}
