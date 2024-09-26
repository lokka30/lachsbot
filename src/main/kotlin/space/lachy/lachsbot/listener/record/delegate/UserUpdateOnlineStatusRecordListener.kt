package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordUserUpdateOnlineStatus
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import java.util.concurrent.TimeUnit

object UserUpdateOnlineStatusRecordListener : RecordListener() {

    override fun onUserUpdateOnlineStatus(
        event: UserUpdateOnlineStatusEvent
    ) {
        if (event.user.isBot) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            insertRecordFromEvent(event)
        }
    }

    private const val COLLECTION_NAME = "user_update_online_status_records"

    private suspend fun insertRecordFromEvent(
        event: UserUpdateOnlineStatusEvent
    ) {
        val record = RecordUserUpdateOnlineStatus(
            ObjectId(),
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            keep = false,
            userId = event.member.idLong,
            userName = event.user.name,
            userEffectiveName = event.member.effectiveName,
            newStatus = event.newOnlineStatus.name,
            oldStatus = event.oldOnlineStatus.name,
            guildId = event.guild.idLong,
            guildName = event.guild.name,
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        val coll = Mongo.db
            .getCollection<RecordUserUpdateOnlineStatus>(COLLECTION_NAME)

        coll.createIndex(
            Indexes.descending(Mongo.getFieldName(RecordUserUpdateOnlineStatus::timestampEpoch)),
            IndexOptions().expireAfter(28, TimeUnit.DAYS)
        )

        coll.insertOne(record)
    }
}