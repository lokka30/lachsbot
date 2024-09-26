package space.lachy.lachsbot.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private fun current(): Date {
        return Date()
    }

    fun formattedDate(): String {
        return format.format(current())
    }

}