package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import space.lachy.lachsbot.listener.record.RecordListenerManager
import space.lachy.lachsbot.util.StringExtensions.truncate
import java.awt.Color
import java.util.*

data class RecordMessageUpdate(
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
    val messageRawNew: String,

    @BsonProperty("message_raw_last_known")
    val messageRawLastKnown: String?,

    @BsonProperty("original_timestamp_last_known")
    val originalTimestampLastKnown: BsonDateTime?,
) {

    fun toEmbedBuilder(): EmbedBuilder {
        val builder = EmbedBuilder()
            .setTitle("Message Updated")
            .addField("New Message Raw", messageRawNew.truncate(), false)
            .addField("Old Message Raw Last Known", messageRawLastKnown?.truncate() ?: "N/A", false)
            .addField("Message ID", "$messageId", true)
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .addField("Original Timestamp Epoch", "${originalTimestampLastKnown?.value ?: "N/A"}", true)
            .addField("Original Timestamp FMT", if (originalTimestampLastKnown == null) { "N/A" } else { Date(originalTimestampLastKnown.value).toString() }, true)
            .addField("Guild ID", "$guildId", true)
            .addField("Guild Name", guildName, true)
            .addField("Channel ID", "$channelId", true)
            .addField("Channel Name", channelName, true)
            .addField("Channel Tag", "<#$channelId>", true)
            .addField("Author ID", authorId.toString(), true)
            .addField("Author Username", authorUsername, true)
            .addField("Author Effective Name", authorEffectiveName, true)
            .addField("Author Tag", "<@${authorId}>", true)
            .addField("Jump URL", jumpUrl.truncate(), true)
            .setFooter("${javaClass.simpleName} - ID#$id")
            .setColor(Color.LIGHT_GRAY)

        RecordListenerManager.mutateBuilderAddAttachmentSummaryFields(builder, attachments)

        return builder
    }

}
