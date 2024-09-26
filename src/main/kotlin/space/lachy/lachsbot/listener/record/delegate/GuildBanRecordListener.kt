package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordGuildBan
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import java.util.concurrent.TimeUnit

object GuildBanRecordListener : RecordListener() {

    private const val COLLECTION_NAME: String = "guild_ban_records"

    override fun onGuildBan(
        event: GuildBanEvent
    ) {
        val record = RecordGuildBan(
            id = ObjectId(),
            keep = false,
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            guildId = event.guild.idLong,
            guildName = event.guild.name,
            userId = event.user.idLong,
            userName = event.user.name,
            userEffectiveName = event.user.effectiveName
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        CoroutineScope(Dispatchers.IO).launch {
            val coll = Mongo.db
                .getCollection<RecordGuildBan>(COLLECTION_NAME)

            coll.createIndex(
                Indexes.descending(Mongo.getFieldName(RecordGuildBan::timestampEpoch)),
                IndexOptions().expireAfter(28, TimeUnit.DAYS)
            )

            coll.insertOne(record)

        }
    }

}