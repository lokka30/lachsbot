package space.lachy.lachsbot.listener.record.delegate

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.StatusChangeEvent
import org.bson.BsonDateTime
import org.bson.types.ObjectId
import space.lachy.lachsbot.LachsBot
import space.lachy.lachsbot.config.Config
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordStatusChange
import space.lachy.lachsbot.listener.record.EmbedPosterQueueThread
import space.lachy.lachsbot.listener.record.RecordListener
import java.util.concurrent.TimeUnit

object StatusChangeRecordListener : RecordListener() {

    // https://ci.dv8tion.net/job/JDA5/javadoc/net/dv8tion/jda/api/events/StatusChangeEvent.html

    private const val COLLECTION_NAME = "status_change_records"

    override fun onStatusChange(
        event: StatusChangeEvent
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            insertRecordFromEvent(event)
        }
    }

    private suspend fun insertRecordFromEvent(
        event: StatusChangeEvent
    ) {
        val record = RecordStatusChange(
            ObjectId(),
            timestampEpoch = BsonDateTime(System.currentTimeMillis()),
            keep = false,
            oldStatus = event.oldStatus.name,
            newStatus = event.newStatus.name,
        )

        if(!Config.embedPosterSuppressedRecords().contains(COLLECTION_NAME)) {
            EmbedPosterQueueThread.submit(record.toEmbedBuilder())
        }

        if(!LachsBot.shuttingDown.get()) {
            val coll = Mongo.db
                .getCollection<RecordStatusChange>(COLLECTION_NAME)

            coll.createIndex(
                Indexes.descending(Mongo.getFieldName(RecordStatusChange::timestampEpoch)),
                IndexOptions().expireAfter(28, TimeUnit.DAYS)
            )

            coll.insertOne(record)
        }
    }
}