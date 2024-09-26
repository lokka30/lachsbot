package space.lachy.lachsbot.listener.misc

import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import space.lachy.lachsbot.LachsBot
import space.lachy.lachsbot.cli.Cli
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread

object ReadyListener : ListenerAdapter() {

    override fun onReady(
        event: ReadyEvent
    ) {
        startEmbedBatchThread()
    }

    private fun startEmbedBatchThread() {
        if(EmbedPosterQueueThread.isAlive) {
            return
        }

        LachsBot.logger.info("Starting embed batch thread...")
        EmbedPosterQueueThread.start()

        LachsBot.logger.info("Starting CLI...")
        Cli.start()
    }
}