package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordGuildMemberJoin
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import java.util.concurrent.TimeUnit

object GuildMemberJoinEventRecordListener : RecordListener() {

    private const val COLLECTION_NAME: String = "guild_member_join_records"

    override fun onGuildMemberJoin(
        event: GuildMemberJoinEvent
    ) {
        val record = RecordGuildMemberJoin(
            id = ObjectId(),
            keep = false,
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            memberId = event.member.idLong,
            memberUsername = event.member.user.name,
            memberEffectiveName = event.member.effectiveName,
            guildMemberCount = event.guild.memberCount,
            guildId = event.guild.idLong,
            guildName = event.guild.name,
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        CoroutineScope(Dispatchers.IO).launch {
            val coll = Mongo.db
                .getCollection<RecordGuildMemberJoin>(COLLECTION_NAME)

            coll.createIndex(
                Indexes.descending(Mongo.getFieldName(RecordGuildMemberJoin::timestampEpoch)),
                IndexOptions().expireAfter(28, TimeUnit.DAYS)
            )

            coll.insertOne(record)
        }
    }
}