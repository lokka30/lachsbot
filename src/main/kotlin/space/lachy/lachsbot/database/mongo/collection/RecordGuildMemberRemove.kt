package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.awt.Color
import java.util.*

data class RecordGuildMemberRemove(

    @BsonId
    val id: ObjectId,

    @BsonProperty("keep")
    val keep: Boolean,

    @BsonProperty("timestamp_epoch")
    val timestampEpoch: BsonDateTime,

    @BsonProperty("member_id")
    val userId: Long,

    @BsonProperty("member_username")
    val userName: String,

    @BsonProperty("member_effective_name")
    val userEffectiveName: String,

    @BsonProperty("member_count")
    val guildMemberCount: Int,

    @BsonProperty("guild_id")
    val guildId: Long,

    @BsonProperty("guild_name")
    val guildName: String,

    ) {

    fun toEmbedBuilder(): EmbedBuilder {
        return EmbedBuilder()
            .setTitle("Member Left Guild")
            .addField("Member Tag", "<@${userId}>", false)
            .addField("Guild Member Count", "<@${guildMemberCount}>", false)
            .addField("Member ID", "${userId}", true)
            .addField("Member Username", userName, true)
            .addField("Member Effective Name", userEffectiveName, true)
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .addField("Guild ID", "${guildId}", true)
            .addField("Guild Name", guildName, true)
            .setFooter("${javaClass.simpleName} - ID#$id")
            .setColor(Color.LIGHT_GRAY)
    }

}
