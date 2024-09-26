package space.lachy.lachsbot.util

import java.lang.management.ManagementFactory

object JvmUtil {

    fun getUptimeMs(): Long {
        return ManagementFactory.getRuntimeMXBean().uptime
    }

}