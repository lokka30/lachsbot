package space.lachy.lachsbot.listener.command.delegate

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import space.lachy.lachsbot.LachsBot
import space.lachy.lachsbot.listener.command.SlashCommand

object ShutdownSlashCommand: SlashCommand(
    name = "shutdown",
    description = "Shuts down the bot (emergencies only)",
    permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR),
) {

    override fun execute(event: SlashCommandInteractionEvent) {
        event
            .reply("Shutting down...")
            .setEphemeral(true)
            .queue {
                LachsBot.stop()
            }
    }

}