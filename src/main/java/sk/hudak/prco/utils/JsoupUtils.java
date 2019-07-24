package sk.hudak.prco.utils;

import lombok.NonNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Optional;

public class JsoupUtils {

    private JsoupUtils() {
    }

    public static int calculateCountOfPages(int countOfProduct, int pagging) {
        int hh = countOfProduct % pagging;

        int result = countOfProduct / pagging;

        if (hh > 0) {
            return result + 1;
        } else {
            return result;
        }
    }

    public static String hrefAttribute(Element element) {
        if (element == null) {
            return null;
        }
        return element.attr("href");
    }

    public static String srcAttribute(Element element) {
        if (element == null) {
            return null;
        }
        return element.attr("src");
    }

    public static String dataSrcAttribute(Element element) {
        if (element == null) {
            return null;
        }
        return element.attr("data-src");
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
