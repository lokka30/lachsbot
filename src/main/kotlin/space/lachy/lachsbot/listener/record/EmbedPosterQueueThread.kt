package space.lachy.lachsbot.listener.record

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import space.lachy.lachsbot.LachsBot.jda
import space.lachy.lachsbot.LachsBot.logger
import space.lachy.lachsbot.config.Config
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object EmbedPosterQueueThread : Thread() {

    private const val EMBED_POSTER_MSG_COUNT: Int = 10
    private const val EMBED_POSTER_MSG_PERIOD: Int = 3
    private val embedPosterQueue: BlockingQueue<EmbedBuilder> = LinkedBlockingQueue()

    fun submit(builder: EmbedBuilder) {
        embedPosterQueue.add(builder)
    }

    override fun run() {
        while (true) {
            if(isInterrupted) {
                break
            }

            publishPendingBatch()

            try {
                sleep(EMBED_POSTER_MSG_PERIOD * 1_000L)
            } catch (ex: InterruptedException) {
                // ignore
                break
            }
        }
    }

    /**
     * Takes [EMBED_POSTER_MSG_COUNT] embeds every [EMBED_POSTER_MSG_PERIOD] seconds and
     * posts them to all guilds the bot is in.
     */
    private fun publishPendingBatch() {
        logger.trace("Publishing pending batch; name=${name} currentTime=${System.currentTimeMillis()}; size=${embedPosterQueue.size}")

        val embeds: MutableList<MessageEmbed> = mutableListOf()

        for(i in 1..EMBED_POSTER_MSG_COUNT) {
            if(embedPosterQueue.isEmpty()) {
                break
            }

            embeds.add(embedPosterQueue.remove().build())
        }

        if(embeds.isEmpty()) {
            return
        }

        val msgData = MessageCreateBuilder()
            .addEmbeds(embeds)
            .build()

        for(guild in jda.guilds) {
            val channelId: Long = Config.embedPosterChannelIdByGuildId(guild.idLong) ?: continue

            val channel: TextChannel? = guild.getTextChannelById(channelId)

            if(channel == null) {
                logger.error("Unable to get text channel of ID '${channelId}' in guild '${guild.name}'")
                continue
            }

            channel.sendMessage(msgData).queue()
        }
    }

}