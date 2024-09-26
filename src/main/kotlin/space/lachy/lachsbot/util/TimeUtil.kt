package space.lachy.lachsbot.util

object TimeUtil {

    private const val MILLISECONDS_PER_SECOND: Long = 1000L
    private const val MILLISECONDS_PER_MINUTE: Long = MILLISECONDS_PER_SECOND * 60
    private const val MILLISECONDS_PER_HOUR: Long = MILLISECONDS_PER_MINUTE * 60
    private const val MILLISECONDS_PER_DAY: Long = MILLISECONDS_PER_HOUR * 60
    private const val MILLISECONDS_PER_MONTH: Long = (MILLISECONDS_PER_DAY * 30.437).toLong()

    fun formatMillisToDuration(millis: Long): String {
        val durations: MutableList<String> = mutableListOf()
        var millisRemaining: Long = millis

        val months: Long = Math.floorDiv(millisRemaining, MILLISECONDS_PER_MONTH)
        if (months > 0) {
            millisRemaining -= months * MILLISECONDS_PER_MONTH
            durations.add("${months} month${if (months == 1L) "" else 's'}")
        }

        val days: Long = Math.floorDiv(millisRemaining, MILLISECONDS_PER_DAY)
        if (days > 0) {
            millisRemaining -= days * MILLISECONDS_PER_DAY
            durations.add("${days} day${if (days == 1L) "" else 's'}")
        }

        val hours: Long = Math.floorDiv(millisRemaining, MILLISECONDS_PER_HOUR)
        if(hours > 0) {
            millisRemaining -= hours * MILLISECONDS_PER_HOUR
            durations.add("${hours} hour${if (hours == 1L) "" else 's'}")
        }

        val minutes: Long = Math.floorDiv(millisRemaining, MILLISECONDS_PER_MINUTE)
        if(minutes > 0) {
            millisRemaining -= minutes * MILLISECONDS_PER_MINUTE
            durations.add("${minutes} minute${if (minutes == 1L) "" else 's'}")
        }

        val seconds: Long = Math.floorDiv(millisRemaining, MILLISECONDS_PER_SECOND)
        if(seconds > 0) {
            millisRemaining -= seconds * MILLISECONDS_PER_SECOND
            durations.add("${seconds} second${if (seconds == 1L) "" else 's'}")
        }

        durations.add("${millisRemaining} millisecond${if (millisRemaining == 1L) "" else 's'}")

        // reassemble string stack with terms in reverse order
        val sb: StringBuilder = StringBuilder()
        while (true) {
            sb.append(durations.removeAt(0))
            if(durations.isEmpty()) {
                break
            } else {
                sb.append(", ")
            }
        }
        return sb.toString()
    }

}