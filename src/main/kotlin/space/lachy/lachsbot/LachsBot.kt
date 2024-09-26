package space.lachy.lachsbot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.listener.command.SlashCommandListener
import space.lachy.lachsbot.listener.misc.AnnoyingEmbedListener
import space.lachy.lachsbot.listener.misc.JoinAnnounceListener
import space.lachy.lachsbot.listener.misc.ReadyListener
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListenerManager
import space.lachy.lachsbot.util.DiscordUtil
import space.lachy.lachsbot.util.SystemUtil
import space.lachy.lachsbot.util.ThrowableUtil
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.system.exitProcess

object LachsBot {

    val logger: Logger = LoggerFactory.getLogger(LachsBot.javaClass)

    lateinit var jda: JDA

    var shuttingDown: AtomicBoolean = AtomicBoolean(false)

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("Welcome to LachsBot")
        logger.info("Working directory: ${Path(System.getProperty("user.dir")).toAbsolutePath().pathString}")

        logger.info("Adding shutdown hook...")
        Runtime.getRuntime().addShutdownHook(Thread {
            if(shuttingDown.get()) {
                return@Thread
            }

            logger.warn("Invoking shutdown hook...")
            // Even though we're not on the main thread, let's do our best to
            // gracefully stop the application. It's possible the main thread
            // can get blocked doing something anyhow, so we don't want to
            // try synchronise to it directly.
            stop()
        })

        logger.info("Loading config...")
        Config.start()

        logger.info("Loading database...")
        Mongo.start()

        val listeners: List<ListenerAdapter> = let {
            val list = mutableListOf(
                AnnoyingEmbedListener,
                JoinAnnounceListener,
                ReadyListener,
                SlashCommandListener,
            )

            list.addAll(RecordListenerManager.allRecordListeners)

            return@let list
        }

        logger.info("Loading bot...")
        jda = try {
            DiscordUtil.buildBot(
                SystemUtil.getToken(),
                DiscordUtil.allIntents,
                listeners,
                Config.activityStatus()
            )
        } catch(ex: Exception) {
            throw RuntimeException("Unable to build JDA instance", ex)
        }

        logger.info("Loading listeners...")
        loadListeners()

        logger.info("Initialization complete. CLI will start once bot is ready.")
    }

    fun stop() {
        if(shuttingDown.get()) {
            logger.warn("Already stopping!")
            return
        }

        logger.info("Shutting down...")
        shuttingDown.set(true)
        EmbedPosterQueueThread.interrupt()
        jda.shutdown()
        Mongo.stop()
        logger.info("Thank you and goodbye")
        exitProcess(0)
    }

    fun reload(): Boolean {
        logger.info("Reloading...")
        try {
            Config.reload()
        } catch (e: Exception) {
            logger.error("Reload unsuccessful!")
            ThrowableUtil.logThrowable(e, "Unable to soft-reload")
            return false
        }
        logger.info("Reloaded successfully.")
        return true
    }

    private fun loadListeners() {
        SlashCommandListener.queueUpdateCommands()
    }

}