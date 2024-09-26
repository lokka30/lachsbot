package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.awt.Color
import java.util.*

data class RecordUserUpdateOnlineStatus(

    @BsonId
    val id: ObjectId,

    @BsonProperty("keep")
    val keep: Boolean,

    @BsonProperty("timestamp_epoch")
    val timestampEpoch: BsonDateTime,

    @BsonProperty("user_id")
    val userId: Long,

    @BsonProperty("user_name")
    val userName: String,

    @BsonProperty("user_effective_name")
    val userEffectiveName: String,

    @BsonProperty("new_status")
    val newStatus: String,

    @BsonProperty("old_status")
    val oldStatus: String,

    @BsonProperty("guild_id")
    val guildId: Long,

    @BsonProperty("guild_name")
    val guildName: String,

    ) {

    fun toEmbedBuilder(): EmbedBuilder {
        return EmbedBuilder()
            .setTitle("User Updated Online Status")
            .addField("New Status", newStatus, false)
            .addField("Old Status", oldStatus, false)
            .addField("User ID", userId.toString(), true)
            .addField("User Name", userName, true)
            .addField("User Effective Name", userEffectiveName, true)
            .addField("Guild ID", guildId.toString(), true)
            .addField("Guild Name", guildName, true)
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .setFooter("${javaClass.simpleName} - ID#${id}")
            .setColor(Color.LIGHT_GRAY)
    }

}