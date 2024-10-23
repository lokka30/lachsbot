package space.lachy.lachsbot.listener.record

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed.VALUE_MAX_LENGTH
import org.bson.BsonDateTime
import org.bson.BsonInt64
import space.lachy.lachsbot.database.mongo.Mongo
import space.lachy.lachsbot.database.mongo.collection.RecordMessageReceived
import space.lachy.lachsbot.database.mongo.collection.RecordMessageUpdate
import space.lachy.lachsbot.listener.record.delegate.GuildBanRecordListener
import space.lachy.lachsbot.listener.record.delegate.GuildMemberJoinEventRecordListener
import space.lachy.lachsbot.listener.record.delegate.GuildMemberRemoveRecordListener
import space.lachy.lachsbot.listener.record.delegate.GuildUnbanRecordListener
import space.lachy.lachsbot.listener.record.delegate.MessageDeleteRecordListener
import space.lachy.lachsbot.listener.record.delegate.MessageReceivedRecordListener
import space.lachy.lachsbot.listener.record.delegate.MessageUpdateRecordListener
import space.lachy.lachsbot.listener.record.delegate.StatusChangeRecordListener
import space.lachy.lachsbot.listener.record.delegate.UserUpdateOnlineStatusRecordListener
import space.lachy.lachsbot.util.StringExtensions.truncate

object RecordListenerManager {

    /*
    TODO: Implementations to make

    - UserUpdateActivityOrderEvent
    - UserUpdateActivitiesEvent
    - UserUpdateAvatarEvent
    - UserUpdateFlagsEvent
    - UserUpdateNameEvent
    - UserActivityStartEvent
    - UserActivityEndEvent

    - MessageReactionRemoveAllEvent
    - MessageReactionRemoveEmojiEvent
    - MessageReactionAddEvent
    - MessageReactionRemoveEvent

    - GuildInviteCreateEvent
    - GuildInviteDeleteEvent
    - GuildMemberUpdateNicknameEvent
    - GuildMemberUpdateAvatarEvent
    - GuildVoiceJoinEvent
    - GuildVoiceLeaveEvent
    - GuildVoiceMoveEvent

    list of events: <https://jda.wiki/introduction/events-list/#jda-events>
     */

    // maintain a list of all record listener objects here.
    val allRecordListeners: Collection<RecordListener> = listOf(
        GuildBanRecordListener,
        GuildMemberJoinEventRecordListener,
        GuildMemberRemoveRecordListener,
        GuildUnbanRecordListener,
        MessageDeleteRecordListener,
        MessageReceivedRecordListener,
        MessageUpdateRecordListener,
        StatusChangeRecordListener,
        UserUpdateOnlineStatusRecordListener,
    )

    fun mapAttachmentsToPrimitivesForDb(
        attachments: List<Message.Attachment>
    ): List<PrimitiveMessageAttachment> {
        return attachments.map {
            PrimitiveMessageAttachment(
                it.fileName,
                it.fileExtension,
                it.url
            )
        }
    }

    fun mutateBuilderAddAttachmentSummaryFields(
        builder: EmbedBuilder,
        attachments: List<PrimitiveMessageAttachment>
    ) {
        var i = 1
        for(att in attachments) {
            builder.addField(
                "Attachment ${i}",
                "Name: '${att.name}'; Extension: '${att.extension}'; URL: '${att.url}'".truncate(VALUE_MAX_LENGTH),
                false
            )

            i++
        }
    }

    /**
     * Tries to construct a [LastKnownMessageData] object with the best-known
     * data values from the database. If unsuccessful, `null` is returned.
     *
     * @see LastKnownMessageData
     */
    suspend fun getLastKnownMessageData(
        messageId: Long
    ): LastKnownMessageData? {
        val lastKnownUpdate: RecordMessageUpdate? = getLastKnownRecordMessageUpdate(messageId)
        if(lastKnownUpdate != null) {
            return LastKnownMessageData(
                messageRaw = lastKnownUpdate.messageRawNew,
                authorId = lastKnownUpdate.authorId,
                authorUsername = lastKnownUpdate.authorUsername,
                authorEffectiveName = lastKnownUpdate.authorEffectiveName,
                originalTimestamp = lastKnownUpdate.originalTimestampLastKnown
            )
        }

        val lastKnownReceived: RecordMessageReceived? = getLastKnownRecordMessageReceived(messageId)
        if(lastKnownReceived != null) {
            return LastKnownMessageData(
                messageRaw = lastKnownReceived.messageRaw,
                authorId = lastKnownReceived.authorId,
                authorUsername = lastKnownReceived.authorUsername,
                authorEffectiveName = lastKnownReceived.authorEffectiveName,
                originalTimestamp = lastKnownReceived.timestampEpoch
            )
        }

        return null
    }

    private suspend fun getLastKnownRecordMessageUpdate(
        messageId: Long
    ): RecordMessageUpdate? {
        return Mongo.db.getCollection<RecordMessageUpdate>(MessageUpdateRecordListener.COLLECTION_NAME)
            .withDocumentClass<RecordMessageUpdate>()
            .find(eq(Mongo.getFieldName(RecordMessageUpdate::messageId), BsonInt64(messageId)))
            .sort(Sorts.descending(Mongo.getFieldName(RecordMessageUpdate::timestampEpoch)))
            .firstOrNull()
    }

    private suspend fun getLastKnownRecordMessageReceived(
        messageId: Long
    ): RecordMessageReceived? {
        return Mongo.db.getCollection<RecordMessageReceived>(MessageReceivedRecordListener.COLLECTION_NAME)
            .withDocumentClass<RecordMessageReceived>()
            .find(eq(Mongo.getFieldName(RecordMessageReceived::messageId), BsonInt64(messageId)))
            .sort(Sorts.descending(Mongo.getFieldName(RecordMessageReceived::timestampEpoch)))
            .firstOrNull()
    }

    /**
     * Represents the last known data about an existing message in Discord.
     *
     * Since Discord doesn't provide this info on all messages (especially deleted ones),
     * we figure it out ourselves.
     */
    data class LastKnownMessageData(
        val messageRaw: String,
        val authorId: Long,
        val authorUsername: String,
        val authorEffectiveName: String,
        val originalTimestamp: BsonDateTime?,
    )

    data class PrimitiveMessageAttachment(
        val name: String,
        val extension: String?,
        val url: String
    )

}