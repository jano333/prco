package sk.hudak.prco.utils

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*

object JsoupUtils {

    @JvmStatic
    fun calculateCountOfPages(countOfProduct: Int, pagging: Int): Int {
        val hh = countOfProduct % pagging

        val result = countOfProduct / pagging

        return if (hh > 0) {
            result + 1
        } else {
            result
        }
    }

    @JvmStatic
    fun hrefAttribute(element: Element?): String? {
        return element?.attr("href")
    }

    @JvmStatic
    fun srcAttribute(element: Element?): String? {
        return element?.attr("src")
    }

    @JvmStatic
    fun dataSrcAttribute(element: Element?): String? {
        return element?.attr("data-src")
    }

    @JvmStatic
    fun existElement(document: Document, cssQuery: String): Boolean {
        return !notExistElement(document, cssQuery)
    }

    @JvmStatic
    fun notExistElement(document: Document, cssQuery: String): Boolean {
        return document.select(cssQuery).isEmpty()
    }

    @JvmStatic
    fun getFirstElementByClass(document: Document, className: String): Optional<Element> {
        return Optional.ofNullable(document.getElementsByClass(className).first())
    }

    @JvmStatic
    fun getTextFromFirstElementByClass(document: Document, className: String): Optional<String> {
        val firstElementByClass = getFirstElementByClass(document, className)
        if (!firstElementByClass.isPresent) {
            return Optional.empty()
        }
        //TODO NPE
        val text = firstElementByClass.get().allElements[0].text()
        return Optional.of(text)
    }

}
