package space.lachy.lachsbot.util

object StringExtensions {

    private val humps = "(?<=.)(?=\\p{Upper})".toRegex()

    fun String.toSnakeCase() = replace(humps, "_").lowercase()

}