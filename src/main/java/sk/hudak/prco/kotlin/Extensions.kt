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