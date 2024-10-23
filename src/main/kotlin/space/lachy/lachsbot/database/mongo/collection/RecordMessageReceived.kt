package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed.VALUE_MAX_LENGTH
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import space.lachy.lachsbot.listener.record.RecordListenerManager
import space.lachy.lachsbot.util.StringExtensions.truncate
import java.awt.Color
import java.util.*

data class RecordMessageReceived(
    @BsonId
    val id: ObjectId,

    @BsonProperty("keep")
    val keep: Boolean,

    @BsonProperty("timestamp_epoch")
    val timestampEpoch: BsonDateTime,

    @BsonProperty("author_id")
    val authorId: Long,

    @BsonProperty("author_username")
    val authorUsername: String,

    @BsonProperty("author_effective_name")
    val authorEffectiveName: String,

    @BsonProperty("attachments")
    val attachments: List<RecordListenerManager.PrimitiveMessageAttachment>,

    @BsonProperty("channel_id")
    val channelId: Long,

    @BsonProperty("channel_name")
    val channelName: String,

    @BsonProperty("channel_type")
    val channelType: String,

    @BsonProperty("guild_id")
    val guildId: Long,

    @BsonProperty("guild_name")
    val guildName: String,

    @BsonProperty("jump_url")
    val jumpUrl: String,

    @BsonProperty("message_id")
    val messageId: Long,

    @BsonProperty("message_raw")
    val messageRaw: String,
) {

    fun toEmbedBuilder(): EmbedBuilder {
        val builder = EmbedBuilder()
            .setTitle("Message Received")
            .addField("Message", messageRaw.truncate(VALUE_MAX_LENGTH), false)
            .addField("Message ID", "$messageId", true)
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .addField("Guild ID", "$guildId", true)
            .addField("Guild Name", guildName, true)
            .addField("Channel ID", "$channelId", true)
            .addField("Channel Name", channelName, true)
            .addField("Channel Tag", "<#$channelId>", true)
            .addField("Author ID", authorId.toString(), true)
            .addField("Author Username", authorUsername, true)
            .addField("Author Effective Name", authorEffectiveName, true)
            .addField("Author Tag", "<@${authorId}>", true)
            .addField("Jump URL", jumpUrl.truncate(VALUE_MAX_LENGTH), true)
            .setFooter("${javaClass.simpleName} - ID#$id")
            .setColor(Color.LIGHT_GRAY)

        RecordListenerManager.mutateBuilderAddAttachmentSummaryFields(builder, attachments)

        return builder
    }

}
