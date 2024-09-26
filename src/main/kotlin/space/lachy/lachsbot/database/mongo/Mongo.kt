package space.lachy.lachsbot.database.mongo

import com.mongodb.MongoSocketOpenException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.BsonInt64
import org.bson.Document
import space.lachy.lachsbot.LachsBot.logger
import space.lachy.lachsbot.util.StringExtensions.toSnakeCase
import space.lachy.lachsbot.util.SystemUtil
import kotlin.reflect.KCallable

object Mongo {

    private lateinit var connectionString: String
    private lateinit var client: MongoClient
    lateinit var db: MongoDatabase

    fun start() {
        logger.info("Starting database...")
        connect()
    }

    fun stop() {
        logger.info("Stopping database...")
        client.close()
    }

    fun <T> getFieldName(field: KCallable<T>): String {
        /*
        // Version 1
        var propAnnot: BsonProperty? = null

        for (annot in field.annotations) {
            if(annot is BsonProperty) {
                propAnnot = annot
                break
            }
        }

        return propAnnot?.value ?: "?"
        */

        /*
        // Version 2
        fun <T> warnBackup(field: KCallable<T>): String {
            val fn: String = field.name
            val annot: String = field.annotations.joinToString { it.annotationClass.simpleName ?: "?" }
            logger.warn("Using backup field name '${fn}' as BsonProperty annotation is missing. Annotations: ${annot}")
            return fn
        }

        return field.annotations
            .plus(field.javaClass.annotations)
            .filterIsInstance<BsonProperty>()
            .firstOrNull()
            ?.value ?: warnBackup(field)
         */

        // TODO: can we *not* use this bad workaround below? it works, so it's probably here forever ;)
        // Version 3
        return field.name.toSnakeCase()
    }

    private fun connect() {
        var attempts: Byte = 0
        val maxAttempts: Byte = 10
        val delay: Long = 3

        while (true) {
            attempts++

            try {
                logger.info("Connecting to MongoDB (attempt ${attempts} of ${maxAttempts})...")
                val name: String = SystemUtil.getDbName()
                //val user: String = SystemUtil.getDbUserProperty()
                //val pass: String = SystemUtil.getDbPassProperty()
                //connectionString = "mongodb+srv://${user}:${pass}@localhost/?retryWrites=true&w=majority"
                //connectionString = "mongodb://localhost:27017"
                //connectionString = "mongodb://mongo:27017"
                connectionString = SystemUtil.getDbConnectionStr()
                client = MongoClient.create(connectionString)
                db = client.getDatabase(name)

                runBlocking {
                    ping()
                }

                logger.info("Connected to MongoDB successfully.")
                break
            } catch (ex: MongoSocketOpenException) {
                logger.warn("Unable to connect, retrying in ${delay}s (attempt ${attempts} of ${maxAttempts}).")
                Thread.sleep(delay * 1_000)
                if(attempts == maxAttempts) {
                    logger.warn("Unable to connect after ${attempts} attempts. Connection string: ${connectionString}")
                    throw ex
                }
                continue
            }
        }
    }

    private suspend fun ping() {
        val command = Document("ping", BsonInt64(1))
        db.runCommand(command)
        logger.info("Successful ping to database.")
    }

}