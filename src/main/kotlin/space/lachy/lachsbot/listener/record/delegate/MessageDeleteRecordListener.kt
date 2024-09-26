package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordGuildUnban
import space.lachy.lachsbot.database.mongo.collection.RecordMessageDelete
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import space.lachy.lachsbot.listener.record.RecordListenerManager.LastKnownMessageData
import space.lachy.lachsbot.listener.record.RecordListenerManager.getLastKnownMessageData
import java.util.concurrent.TimeUnit

object MessageDeleteRecordListener : RecordListener() {

    private const val COLLECTION_NAME = "message_delete_records"

    override fun onMessageDelete(
        event: MessageDeleteEvent
    ) {
        if (!event.isFromGuild) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            insertRecordFromEvent(event)
        }
    }

    private suspend fun insertRecordFromEvent(
        event: MessageDeleteEvent
    ) {
        val lastKnown: LastKnownMessageData? = getLastKnownMessageData(event.messageIdLong)

        val record = RecordMessageDelete(
            ObjectId(),
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            keep = false,
            channelId = event.channel.idLong,
            channelName = event.channel.name,
            channelType = event.channelType.name,
            guildId = event.guild.idLong,
            guildName = event.guild.name,
            messageId = event.messageIdLong,
            messageRawLastKnown = lastKnown?.messageRaw,
            authorIdLastKnown = lastKnown?.authorId,
            authorUsernameLastKnown = lastKnown?.authorUsername,
            authorEffectiveNameLastKnown = lastKnown?.authorEffectiveName,
            originalTimestampLastKnown = lastKnown?.originalTimestamp,
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        val coll = Mongo.db
            .getCollection<RecordMessageDelete>(COLLECTION_NAME)

        coll.createIndex(
            Indexes.descending(Mongo.getFieldName(RecordGuildUnban::timestampEpoch)),
            IndexOptions().expireAfter(28, TimeUnit.DAYS)
        )

        coll.insertOne(record)
    }

}