package space.lachy.lachsbot.listener.misc

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import space.lachy.lachsbot.config.Config
import java.awt.Color

object JoinAnnounceListener : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        if(event.user.isBot) {
            return
        }

        val eb: MessageEmbed = EmbedBuilder()
            .setColor(Color.GREEN)
            .setTitle("🚪 User Joined the Guild")
            .setDescription("**Welcome, <@${event.user.idLong}>!**")
            .addField("ID", event.user.id, true)
            .addField("Name", event.user.name, true)
            .addField("Effective Name", event.user.effectiveName, true)
            .addField("User Count", event.guild.memberCount.toString(), true)
            .setThumbnail(event.user.avatarUrl)
            .build()

        announceJoinLeaveEvent(event.guild, eb)
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        if(event.user.isBot) {
            return
        }

        val eb: MessageEmbed = EmbedBuilder()
            .setColor(Color.RED)
            .setTitle("🚪 User Left the Guild")
            .setDescription("**Goodbye, <@${event.user.idLong}>.**")
            .addField("ID", event.user.id, true)
            .addField("Name", event.user.name, true)
            .addField("Effective Name", event.user.effectiveName, true)
            .addField("User Count", event.guild.memberCount.toString(), true)
            .setThumbnail(event.user.avatarUrl)
            .build()

        announceJoinLeaveEvent(event.guild, eb)
    }

    private fun announceJoinLeaveEvent(guild: Guild, embed: MessageEmbed) {
        val channel: TextChannel = let {
            val channelId: Long? = Config.joinAnnouncerChannelIdByGuildId(guild.idLong)

            return@let if(channelId == null) {
                null
            } else {
                guild.getTextChannelById(channelId)
            }
        } ?: return

        channel.sendMessageEmbeds(embed).queue()
    }

}