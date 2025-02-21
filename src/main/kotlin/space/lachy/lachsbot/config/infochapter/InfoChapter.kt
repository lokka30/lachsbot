package space.lachy.lachsbot.config.infochapter

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.awt.Color

data class InfoChapter(
    val id: String,
    val title: String,
    val description: String,
    val color: Color,
    val footer: String,
) {

    fun buildEmbed(event: SlashCommandInteractionEvent): MessageEmbed {
        return EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(color)
            .setFooter(footer.replace("%sender-name%", event.user.name))
            .build()
    }

}