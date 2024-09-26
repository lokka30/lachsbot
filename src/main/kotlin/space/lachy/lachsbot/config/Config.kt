package space.lachy.lachsbot.config

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import space.lachy.lachsbot.LachsBot.logger
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString


object Config {

    private const val FILE_NAME: String = "config.yml"
    private val path: Path = Path(System.getProperty("user.dir"), "config", FILE_NAME)
    private var loaded: Boolean = false
    private lateinit var loader: YamlConfigurationLoader
    private lateinit var root: CommentedConfigurationNode

    private fun initLoader() {
        loader = YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.FLOW)
            .path(path)
            .build()
    }

    private fun initRoot() {
        root = loader.load()
    }

    fun start() {
        if(loaded) {
            throw IllegalStateException("Config already loaded")
        }

        initLoader()
        initRoot()
        copyDefaultFile(requireNotExists = true)
        loaded = true
    }

    /**
     * Reload the Config.
     *
     * Reinitializes root and ensures default file is there if no file was present (e.g.,
     * removed during runtime).
     *
     * This function requires the config to already be loaded, so it is at a state where it
     * can be loaded (i.e., the loader has been initialized).
     */
    @Suppress("SameParameterValue")
    fun reload() {
        if(!loaded) {
            throw IllegalStateException("Config is not loaded yet")
        }

        initRoot()
        copyDefaultFile(requireNotExists = true)
    }

    @Suppress("unused")
    fun save() {
        loader.save(root)
    }

    private fun exists(): Boolean {
        return path.exists()
    }

    private fun copyDefaultFile(
        @Suppress("SameParameterValue") requireNotExists: Boolean
    ) {
        if(requireNotExists && exists()) {
            return
        }

        logger.info("Default config file doesn't exist, copying...")
        logger.info("(Config path: ${path.toAbsolutePath().pathString})")

        val resourceStream: InputStream = this.javaClass.classLoader.getResourceAsStream(FILE_NAME)
            ?: throw IllegalAccessException("Unable to retrieve resource stream for '${FILE_NAME}'")

        Files.copy(resourceStream, path)
    }

    fun activityStatus(): String? {
        return root.node("activity-status").string
    }

    fun embedPosterChannelIdByGuildId(
        guildId: Long
    ): Long? {
        val node = root.node("embed-poster", "channel-by-guild", guildId.toString())

        return if(node.virtual()) {
            null
        } else {
            node.long
        }
    }

    fun joinAnnouncerChannelIdByGuildId(
        guildId: Long
    ): Long? {
        val node = root.node("join-announcer", "channel-by-guild", guildId.toString())

        return if(node.virtual()) {
            null
        } else {
            node.long
        }
    }

    fun embedPosterSuppressedRecords(): List<String> {
        return root.node("embed-poster", "suppressed-records")
            .getList(String::class.java) ?: Collections.emptyList()
    }

}