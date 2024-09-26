package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.awt.Color
import java.util.*

data class RecordMessageDelete(
    @BsonId
    val id: ObjectId,

    @BsonProperty("keep")
    val keep: Boolean,

    @BsonProperty("timestamp_epoch")
    val timestampEpoch: BsonDateTime,

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

    @BsonProperty("message_id")
    val messageId: Long,

    @BsonProperty("message_raw_last_known")
    val messageRawLastKnown: String?,

    @BsonProperty("author_id_last_known")
    val authorIdLastKnown: Long?,

    @BsonProperty("author_username_last_known")
    val authorUsernameLastKnown: String?,

    @BsonProperty("author_effective_name_last_known")
    val authorEffectiveNameLastKnown: String?,

    @BsonProperty("original_timestamp_last_known")
    val originalTimestampLastKnown: BsonDateTime?,
) {

    fun toEmbedBuilder(): EmbedBuilder {
        return EmbedBuilder()
            .setTitle("Message Deleted")
            .setDescription("Discord's API does not advise who caused this, use Audit Log to check.")
            .addField("Last Known Message Raw", messageRawLastKnown ?: "N/A", false)
            .addField("Message ID", "$messageId", true)
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .addField("Guild ID", "$guildId", true)
            .addField("Guild Name", guildName, true)
            .addField("Channel ID", "$channelId", true)
            .addField("Channel Name", channelName, true)
            .addField("Channel Tag", "<#$channelId>", true)
            .addField("Last Known Author ID", "${authorIdLastKnown ?: "N/A"}", true)
            .addField("Last Known Author Username", authorUsernameLastKnown ?: "N/A", true)
            .addField("Last Known Author Effective Name", authorEffectiveNameLastKnown ?: "N/A", true)
            .addField(
                "Last Known Author Tag",
                if(authorIdLastKnown == null) "N/A" else "<@${authorIdLastKnown}>",
                true
            )
            .addField("Last Known Original Timestamp Epoch", "${originalTimestampLastKnown?.value ?: "N/A"}", true)
            .addField(
                "Last Known Original Timestamp UTC",
                if (originalTimestampLastKnown == null) "N/A" else "${Date(originalTimestampLastKnown.value)}",
                true
            )
            .setFooter("${javaClass.simpleName} - ID#$id")
            .setColor(Color.YELLOW)
    }

}
