package codes.recursive.barn.automation.service.kafka

import groovy.util.logging.Slf4j
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.eclipse.microprofile.config.inject.ConfigProperty

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
@Slf4j
class MessageProducerService implements KafkaService {

    String topicName
    String bootstrapServer
    Producer producer

    @Inject
    MessageProducerService(
            @ConfigProperty(name = "app.barn.incomingTopic") String incomingTopic,
            @ConfigProperty(name = "app.barn.incomingBootstrapServer") String incomingServer
    ) {
        topicName = incomingTopic
        bootstrapServer = incomingServer

        Properties props = new Properties()
        props.put("bootstrap.servers", bootstrapServer)
        props.put("acks", "all")
        props.put("retries", 0)
        props.put("batch.size", 16384)
        props.put("linger.ms", 1)
        props.put("buffer.memory", 33554432)
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

        producer = new KafkaProducer(props)
    }

    def send(String msg, String key=UUID.randomUUID().toString()) {
        log.info("Sending message to ${topicName} with key ${key}")
        try {
            producer.send(new ProducerRecord(topicName, key, msg))
        }
        catch(e) {
            log.error("An error occurred whilst sending message...")
            e.printStackTrace()
        }
        finally {
            log.info("Send complete for message with key ${key}")
        }
    }

    def close() {
        log.info("Closing producer for ${topicName}")
        producer.close()
    }
}
