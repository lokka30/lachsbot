package space.lachy.lachsbot.util

import space.lachy.lachsbot.LachsBot

object ThrowableUtil {

    private val separator: String = "=".repeat(45)

    fun logThrowable(thr: Throwable, msg: String) {
        val logSevere = { logMsg: String -> LachsBot.logger.error(logMsg) }
        val logSeparator = { logSevere(separator) }

        logSeparator()
        logSevere("An unexpected Throwable has been detected. Please investigate using the details below.")
        logSevere("Description:")
        logSevere(msg)
        logSeparator()
        logSevere("Details:")
        logSevere(thr.localizedMessage ?: "(Not Available)")
        logSeparator()
        logSevere("Stack trace:")
        thr.printStackTrace()
        logSeparator()
    }
}