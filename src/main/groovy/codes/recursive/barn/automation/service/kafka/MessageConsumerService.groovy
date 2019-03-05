package codes.recursive.barn.automation.service.kafka

import codes.recursive.barn.automation.event.EventEmitter
import codes.recursive.barn.automation.model.BarnEvent
import codes.recursive.barn.automation.service.data.OracleDataService
import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.util.JSON
import groovy.json.JsonException
import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.eclipse.microprofile.config.inject.ConfigProperty
import xyz.morphia.Datastore
import xyz.morphia.Morphia

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import java.util.concurrent.atomic.AtomicBoolean

@ApplicationScoped
@Slf4j
class MessageConsumerService implements KafkaService {

    String topicName
    String bootstrapServer
    KafkaConsumer consumer
    String group = "barn_pi_group_1"
    private final AtomicBoolean closed = new AtomicBoolean(false)

    @Inject private EventEmitter eventEmitter
    @Inject private OracleDataService oracleDataService

    @Inject
    MessageConsumerService(
            @ConfigProperty(name = "app.barn.outgoingTopic") String outgoingTopic,
            @ConfigProperty(name = "app.barn.outgoingBootstrapServer") String outgoingServer) {

        this.topicName = outgoingTopic
        this.bootstrapServer = outgoingServer

        Properties props = new Properties()
        props.put("bootstrap.servers", bootstrapServer)
        props.put("group.id", group)
        props.put("enable.auto.commit", "true")
        props.put("auto.commit.interval.ms", "1000")
        props.put("session.timeout.ms", "30000")
        props.put("auto.offset.reset", "earliest")
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

        consumer = new KafkaConsumer(props)
    }

    def start() {
        try {
            consumer.subscribe(Arrays.asList(topicName))
            log.info("Subscribed to topic ${consumer.subscription()} ")

            while (!closed.get()) {
                ConsumerRecords records = consumer.poll(1000)
                if( records.size() ) {
                    log.info("Received ${records.size()} records")
                }
                records.each { ConsumerRecord record ->
                    log.info("offset: ${record.offset()}, key: ${record.key().toString()}, value: ${record.value()}, timestamp: ${new Date(record.timestamp())}")
                    def msg
                    try {
                        def slurper = new JsonSlurper()
                        msg = slurper.parseText(record.value())
                        BarnEvent evt = new BarnEvent( msg?.type, JsonOutput.toJson(msg?.data), new Date( record.timestamp() ) )
                        oracleDataService.save(evt)
                        eventEmitter.emit('incomingMessage', [message: [type: evt.type, capturedAt: evt.capturedAt, data: slurper.parseText(evt.data)], timestamp: record.timestamp()])
                    }
                    catch (JsonException e) {
                        log.error("Error parsing JSON from ${record.value()}")
                        e.printStackTrace()
                    }
                    catch (Exception e) {
                        log.error("Error:")
                        e.printStackTrace()
                    }
                }
            }
        }
        catch (WakeupException e) {
            if ( !closed.get() ) {
                log.error("Kafka WakeupException")
                e.printStackTrace()
                throw e
            }
        }
        finally {
            log.info("Closing consumer for ${topicName}")
            consumer.close()
        }
    }

    def close() {
        closed.set(true)
        consumer.wakeup()
    }
}
