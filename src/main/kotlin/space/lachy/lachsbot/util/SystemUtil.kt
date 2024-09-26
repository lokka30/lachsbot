package space.lachy.lachsbot.util

object SystemUtil {

    fun getToken(): String {
        return getReqEnvVar("TOKEN")
    }

    fun getDbName(): String {
        return getReqEnvVar("DB_NAME")
    }

    fun getDbConnectionStr(): String {
        return getReqEnvVar("DB_CONNECTION_STR")
    }

    private fun getReqEnvVar(name: String): String {
        return System.getenv(name)
            ?: throw IllegalArgumentException("'${name}' environment var is required, but is undefined on the system")
    }

}