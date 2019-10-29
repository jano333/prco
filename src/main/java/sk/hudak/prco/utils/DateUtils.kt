package sk.hudak.prco.utils

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

object DateUtils {

    const val DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy HH:mm:ss"

    fun formatDate(date: Date, format: String = DD_MM_YYYY_HH_MM_SS): String {
        return SimpleDateFormat(format).format(date)
    }

    fun calculateDate(olderThanInHours: Int): Date {
        val currentDate = Date()
        val localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val newDateTime = localDateTime.minusHours(olderThanInHours.toLong())
        return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }
}
