package space.lachy.lachsbot.util

import kotlin.math.min

object StringExtensions {

    private val humps = "(?<=.)(?=\\p{Upper})".toRegex()

    fun String.toSnakeCase() = replace(humps, "_").lowercase()

    fun String.truncate(newLength: Int): String {
        if(isEmpty() || newLength <= 3 || length <= newLength) {
            return this
        }

        return take(min(length, newLength) - 3) + "..."
    }

}