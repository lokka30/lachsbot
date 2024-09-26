package space.lachy.lachsbot.database.mongo.collection

import net.dv8tion.jda.api.EmbedBuilder
import org.bson.BsonDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.awt.Color
import java.util.*

data class RecordStatusChange(
    @BsonId
    val id: ObjectId,

    @BsonProperty("keep")
    val keep: Boolean,

    @BsonProperty("timestamp_epoch")
    val timestampEpoch: BsonDateTime,

    @BsonProperty("old_status")
    val oldStatus: String,

    @BsonProperty("new_status")
    val newStatus: String,
) {

    fun toEmbedBuilder(): EmbedBuilder {
        return EmbedBuilder()
            .setTitle("Bot Status Changed")
            .addField("New Status", newStatus, false)
            .addField("Old Status", oldStatus, false)
            .addField("Timestamp Epoch", "${timestampEpoch.value}", true)
            .addField("Timestamp FMT", "${Date(timestampEpoch.value)}", true)
            .setFooter("${javaClass.simpleName} - ID#${id}")
            .setColor(Color.CYAN)
    }

}