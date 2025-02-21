package space.lachy.lachsbot.listener.command.delegate

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.config.infochapter.InfoChapter
import space.lachy.lachsbot.listener.command.SlashCommand

object InfoSlashCommand : SlashCommand(
    name = "info",
    description = "Posts the requested information panel in the channel it was ran",
    permissions = DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE),
    options = listOf(OptionData(OptionType.STRING, "chapter-id", "Identifier of the info chapter wanted"))
) {

    override fun execute(event: SlashCommandInteractionEvent) {
        if (!event.isFromGuild) {
            return
        }

        val chapterId = event.getOption("chapter-id")?.asString

        if (chapterId == null) {
            event
                .reply("Error (invalid usage): You must specify a chapter-id")
                .setEphemeral(true)
                .queue()
            return
        }

        val chapter: InfoChapter? = Config.infoChapters.firstOrNull { it.id == chapterId }

        if (chapter == null) {
            event
                .reply("Error (unknown value): There is no chapter with the ID: ${chapterId}")
                .setEphemeral(true)
                .queue()
            return
        }

        event
            .replyEmbeds(chapter.buildEmbed(event))
            .queue()
    }

}