package space.lachy.lachsbot.listener.misc

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object AnnoyingEmbedListener : ListenerAdapter() {

    private const val REGEX = "(https|http).*(github\\.com|wikipedia\\.org|spigotmc\\.org|mc-market\\.org|polymart\\.org|papermc\\.io|gitbook.io)"

    private val pattern: Pattern = Pattern.compile(REGEX)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(pattern.matcher(event.message.contentRaw).find()) {
            event.message.suppressEmbeds(true).queueAfter(250, TimeUnit.MILLISECONDS)
        }
    }

}