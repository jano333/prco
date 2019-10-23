package sk.hudak.prco.kotlin

import org.slf4j.Logger
import sk.hudak.prco.utils.ConsoleColor
import sk.hudak.prco.utils.ConsoleWithColor

fun Logger.warnYellow(msg: String) {
    this.warn(ConsoleWithColor.wrapWithColor(msg, ConsoleColor.YELLOW));
}

fun String.color(color: ConsoleColor): String {
    return ConsoleWithColor.wrapWithColor(this, color)
}

fun String.firstCharacterToUpperCase(): String {
    return if (this.isNotEmpty()) {
        val firstCharacter = this.substring(0, 1)
        val rest = this.substring(1);
        firstCharacter.toUpperCase() + rest
    } else {
        this
    }
}

