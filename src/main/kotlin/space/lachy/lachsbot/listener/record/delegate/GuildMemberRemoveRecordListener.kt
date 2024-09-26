package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordGuildMemberRemove
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import java.util.concurrent.TimeUnit

object GuildMemberRemoveRecordListener : RecordListener() {

    private const val COLLECTION_NAME: String = "guild_member_remove_records"

    override fun onGuildMemberRemove(
        event: GuildMemberRemoveEvent
    ) {
        val record = RecordGuildMemberRemove(
            id = ObjectId(),
            keep = false,
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            userId = event.user.idLong,
            userName = event.user.name,
            userEffectiveName = event.user.effectiveName,
            guildMemberCount = event.guild.memberCount,
            guildId = event.guild.idLong,
            guildName = event.guild.name,
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        CoroutineScope(Dispatchers.IO).launch {
            val coll = Mongo.db
                .getCollection<RecordGuildMemberRemove>(COLLECTION_NAME)

            coll.createIndex(
                Indexes.descending(Mongo.getFieldName(RecordGuildMemberRemove::timestampEpoch)),
                IndexOptions().expireAfter(28, TimeUnit.DAYS)
            )

            coll.insertOne(record)
        }
    }
}