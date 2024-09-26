package space.lachy.lachsbot.util

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.*

object DiscordUtil {

    // Collection of gateway intents used by the bot.
    // I've converted it to an immutable set here since it's meant to be constant.
    val allIntents: Set<GatewayIntent> = EnumSet.allOf(GatewayIntent::class.java).toSet()

    fun buildBot(
        token: String,
        intents: Collection<GatewayIntent>,
        listeners: Collection<ListenerAdapter>,
        activityStatus: String?
    ): JDA {
        return try {
            val builder = JDABuilder
                .createLight(token, intents)
                .addEventListeners(*listeners.toTypedArray())

            if (activityStatus != null) {
                builder.setActivity(Activity.customStatus(activityStatus))
            }

            builder.build() // <-- ret
        } catch(ex: Exception) {
            throw RuntimeException("Unable to build JDA instance", ex)
        }
    }

}