package space.lachy.lachsbot.listener.command.delegate

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import space.lachy.lachsbot.LachsBot
import space.lachy.lachsbot.listener.command.SlashCommand

object ReloadSlashCommand: SlashCommand(
    name = "reload",
    description = "Soft-reloads the bot",
    permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR),
) {

    override fun execute(event: SlashCommandInteractionEvent) {
        if (!event.isFromGuild) {
            return
        }

        event.deferReply(true).queue {
            val success: Boolean = LachsBot.reload()
            if(success) {
                it.editOriginal("Reloaded successfully.")
            } else {
                it.editOriginal("Reloaded unsuccessfully. :(")
            }
        }
    }

}