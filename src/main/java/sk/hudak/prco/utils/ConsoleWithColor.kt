package sk.hudak.prco.utils

object ConsoleWithColor {

    fun wrapWithColor(text: String, fontColor: ConsoleColor): String {
        return when (fontColor) {
            ConsoleColor.RESET -> "" + ConsoleColor.RESET.colorName
            ConsoleColor.BLACK -> ConsoleColor.BLACK.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.RED -> ConsoleColor.RED.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.GREEN -> ConsoleColor.GREEN.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.YELLOW -> ConsoleColor.YELLOW.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.BLUE -> ConsoleColor.BLUE.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.PURPLE -> ConsoleColor.PURPLE.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.CYAN -> ConsoleColor.CYAN.colorName + text + ConsoleColor.RESET.colorName
            ConsoleColor.WHITE -> ConsoleColor.WHITE.colorName + text + ConsoleColor.RESET.colorName
        }
    }
}

enum class ConsoleColor(val colorName: String) {
    RESET("\u001B[0m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m")
}

// TODO for backqround
//val ANSI_BLACK_BACKGROUND = "\u001B[40m"
//val ANSI_RED_BACKGROUND = "\u001B[41m"
//val ANSI_GREEN_BACKGROUND = "\u001B[42m"
//val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
//val ANSI_BLUE_BACKGROUND = "\u001B[44m"
//val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
//val ANSI_CYAN_BACKGROUND = "\u001B[46m"
//val ANSI_WHITE_BACKGROUND = "\u001B[47m"

fun main() {
    ConsoleColor.values().forEach {
        println(ConsoleWithColor.wrapWithColor("ahoj", it))
    }
}