package sk.hudak.prco.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    val DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss"

    @JvmOverloads
    fun formatDate(date: Date, format: String = DD_MM_YYYY_HH_MM_SS): String {
        return SimpleDateFormat(format).format(date)
    }
}
