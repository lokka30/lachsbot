package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordMessageUpdate
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import space.lachy.lachsbot.listener.record.RecordListenerManager
import space.lachy.lachsbot.listener.record.RecordListenerManager.LastKnownMessageData
import java.util.concurrent.TimeUnit

object MessageUpdateRecordListener : RecordListener() {

    const val COLLECTION_NAME = "message_update_records"

    override fun onMessageUpdate(
        event: MessageUpdateEvent
    ) {
        if (!event.isFromGuild) {
            return
        }

        if (event.author.isBot) {
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            insertRecordFromEvent(event)
        }
    }

    private suspend fun insertRecordFromEvent(
        event: MessageUpdateEvent
    ) {
        val lastKnownMessageData: LastKnownMessageData? = RecordListenerManager.getLastKnownMessageData(event.messageIdLong)

        if(event.message.isSuppressedEmbeds && lastKnownMessageData?.messageRaw == event.message.contentRaw) {
            // probably just triggered by the AnnoyingEmbedListener
            return
        }

        val record = RecordMessageUpdate(
            ObjectId(),
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            keep = false,
            authorId = event.author.idLong,
            authorUsername = event.author.name,
            authorEffectiveName = event.author.effectiveName,
            attachments = RecordListenerManager.mapAttachmentsToPrimitivesForDb(event.message.attachments),
            channelId = event.channel.idLong,
            channelName = event.channel.name,
            channelType = event.channelType.name,
            guildId = event.guild.idLong,
            guildName = event.guild.name,
            jumpUrl = event.jumpUrl,
            messageId = event.messageIdLong,
            messageRawNew = event.message.contentRaw,
            messageRawLastKnown = lastKnownMessageData?.messageRaw,
            originalTimestampLastKnown = lastKnownMessageData?.originalTimestamp,
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        val coll = Mongo.db
            .getCollection<RecordMessageUpdate>(COLLECTION_NAME)

        coll.createIndex(
            Indexes.descending(Mongo.getFieldName(RecordMessageUpdate::timestampEpoch)),
            IndexOptions().expireAfter(28, TimeUnit.DAYS)
        )

        coll.insertOne(record)
    }

}