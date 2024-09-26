package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.awt.Color
import java.util.*

data class RecordGuildBan(

    @BsonId
    val id: ObjectId,

    @BsonProperty("keep")
    val keep: Boolean,

    @BsonProperty("timestamp_epoch")
    val timestampEpoch: BsonDateTime,

    @BsonProperty("guild_id")
    val guildId: Long,

    @BsonProperty("guild_name")
    val guildName: String,

    @BsonProperty("user_id")
    val userId: Long,

    @BsonProperty("user_name")
    val userName: String,

    @BsonProperty("user_effective_name")
    val userEffectiveName: String,

    ) {

    fun toEmbedBuilder(): EmbedBuilder {
        @Suppress("DuplicatedCode")
        return EmbedBuilder()
            .setTitle("User Banned from Guild")
            .setDescription("Discord's API does not advise who caused this, use Audit Log to check.")
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .addField("Guild ID", "$guildId", true)
            .addField("Guild Name", guildName, true)
            .addField("User ID", "$userId", true)
            .addField("User Name", userName, true)
            .addField("User Effective Name", userEffectiveName, true)
            .addField("User Tag", "@<${userId}>", true)
            .setFooter("${javaClass.simpleName} - ID#${id}")
            .setColor(Color.RED)
    }

}
