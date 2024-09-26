package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordMessageReceived
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import space.lachy.lachsbot.listener.record.RecordListenerManager.mapAttachmentsToPrimitivesForDb
import java.util.concurrent.TimeUnit

object MessageReceivedRecordListener : RecordListener() {

    const val COLLECTION_NAME = "message_received_records"

    override fun onMessageReceived(
        event: MessageReceivedEvent
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
        event: MessageReceivedEvent
    ) {
        val record: RecordMessageReceived = RecordMessageReceived(
            ObjectId(),
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            keep = false,
            authorId = event.author.idLong,
            authorUsername = event.author.name,
            authorEffectiveName = event.author.effectiveName,
            attachments = mapAttachmentsToPrimitivesForDb(event.message.attachments),
            channelId = event.channel.idLong,
            channelName = event.channel.name,
            channelType = event.channelType.name,
            guildId = event.guild.idLong,
            guildName = event.guild.name,
            jumpUrl = event.jumpUrl,
            messageId = event.messageIdLong,
            messageRaw = event.message.contentRaw
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        val coll = Mongo.db
            .getCollection<RecordMessageReceived>(COLLECTION_NAME)

        coll.createIndex(
            Indexes.descending(Mongo.getFieldName(RecordMessageReceived::timestampEpoch)),
            IndexOptions().expireAfter(28, TimeUnit.DAYS)
        )

        coll.insertOne(record)
    }

}