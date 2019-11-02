package club.charliefeng.kafkademoappengine.service;

import club.charliefeng.avro.User;
import club.charliefeng.kafkademoappengine.exception.ServiceUnavailableException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificProducerService {

    private static Logger LOG = LoggerFactory.getLogger(SpecificProducerService.class);

    private final String topic4Producer;
    private final KafkaProducer<String, User> specificKafkaProducer;

    @Autowired
    public SpecificProducerService(String topic4Producer, KafkaProducer<String, User> specificKafkaProducer) {
        this.topic4Producer = topic4Producer;
        this.specificKafkaProducer = specificKafkaProducer;
    }

    public void publishSingleton(User user) {
        ProducerRecord<String, User> producerRecord = new ProducerRecord<>(topic4Producer, user);
        specificKafkaProducer.send(producerRecord, (metadata, e) -> {
            if(e != null) {
                LOG.error("Specific producer send record to kafka failed");
                throw new ServiceUnavailableException("Kafka broker is unavailable, send message failed", e.getCause());
            }else {
                LOG.info("Successfully sent record to kafka");
            }
        });
        specificKafkaProducer.flush();
    }

    public void publishBatch(List<User> users) {users.forEach(this::publishSingleton);}
}
