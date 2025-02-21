package space.lachy.lachsbot.listener.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import space.lachy.lachsbot.LachsBot
import space.lachy.lachsbot.LachsBot.logger
import space.lachy.lachsbot.listener.command.delegate.InfoSlashCommand
import space.lachy.lachsbot.listener.command.delegate.PurgeSlashCommand
import space.lachy.lachsbot.listener.command.delegate.ReloadSlashCommand
import space.lachy.lachsbot.listener.command.delegate.ShutdownSlashCommand
import space.lachy.lachsbot.util.ThrowableUtil

object SlashCommandListener : ListenerAdapter() {

    private val commands: Collection<SlashCommand> = setOf(
        InfoSlashCommand,
        PurgeSlashCommand,
        ReloadSlashCommand,
        ShutdownSlashCommand,
    )

    fun queueUpdateCommands() {
        LachsBot.jda.updateCommands()
            .addCommands(commands.map(SlashCommand::build))
            .queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        logger.info("User '${event.user.name}' ran command '${event.commandString}' in guild '${event.guild?.name ?: "N/A"}'")

        val command: SlashCommand

        try {
            command = commands.first {
                    cmd -> event.name.equals(cmd.name, ignoreCase = true)
            }
        } catch (ex: Exception) {
            ThrowableUtil.logThrowable(ex, "Unable to retrieve slash command by name '${event.name}'")
            event
                .reply("I'm sorry, an error occurred whilst processing your command. Please inform an administrator so they can resolve this issue.")
                .setEphemeral(true)
                .queue()
            return
        }

        command.execute(event)
    }

}