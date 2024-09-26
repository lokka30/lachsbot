package space.lachy.lachsbot.listener.command.delegate

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import space.lachy.lachsbot.listener.command.SlashCommand
import space.lachy.lachsbot.util.ThrowableUtil

object PurgeSlashCommand : SlashCommand(
    name = "purge",
    description = "Purge 10 of the most recent messages in the channel this is ran",
    permissions = DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE),
) {

    private const val AMOUNT: Int = 10

    override fun execute(event: SlashCommandInteractionEvent) {
        event
            .deferReply(true)
            .queue { deferHook ->
                event.channel
                    .iterableHistory
                    .takeAsync(AMOUNT)
                    .thenAccept(event.channel::purgeMessages)
                    .whenComplete { _, throwable ->
                        if(throwable == null) {
                            deferHook.editOriginal("Purged up to ${AMOUNT} messages.").queue()
                        } else {
                            deferHook.editOriginal("Sorry, an error occurred.").queue()
                            ThrowableUtil.logThrowable(throwable, "Unable to purge messages")
                        }
                    }
            }
    }

}