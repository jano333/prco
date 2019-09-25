package sk.hudak.prco.utils

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

/**
 * Same as elements.attr("src")
 */
fun Elements.src(): String =
        this.attr("src")

/**
 * Same as element.attr("src")
 */
fun Element.src(): String =
        this.attr("src")


/**
 * Same as elements.attr("href")
 */
fun Elements.href(): String =
        this.attr("href")

/**
 * Same as element.attr("href")
 */
fun Element.href(): String =
        this.attr("href")


object JsoupUtils {

    fun calculateCountOfPages(countOfProducts: Int, pagging: Int): Int {
        val hh = countOfProducts % pagging
        val result = countOfProducts / pagging
        return if (hh > 0) {
            result + 1
        } else {
            result
        }
    }

    fun dataSrcAttribute(element: Element?): String? {
        return element?.attr("data-src")
    }

    fun existElement(document: Document, cssQuery: String): Boolean {
        return !notExistElement(document, cssQuery)
    }

    fun notExistElement(document: Document, cssQuery: String): Boolean {
        return document.select(cssQuery).isEmpty()
    }

    fun getFirstElementByClass(document: Document, className: String): Optional<Element> {
        return Optional.ofNullable(document.getElementsByClass(className).first())
    }

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
