package club.charliefeng.kafkademoappengine.service;

import club.charliefeng.kafkademoappengine.exception.ServiceUnavailableException;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenericProducerService {

    private static Logger LOG = LoggerFactory.getLogger(GenericProducerService.class);

    private final String topic4Producer;
    private final KafkaProducer<String, GenericRecord> genericKafkaProducer;

    @Autowired
    public GenericProducerService(String topic4Producer, KafkaProducer<String, GenericRecord> genericKafkaProducer) {
        this.topic4Producer = topic4Producer;
        this.genericKafkaProducer = genericKafkaProducer;
    }

    public void publishSingleton(GenericRecord record) {
        publishSingleton(topic4Producer, record);
    }

    public void publishSingleton(String topic, GenericRecord record) {
        ProducerRecord<String,GenericRecord> producerRecord = new ProducerRecord<>(topic, record);
        genericKafkaProducer.send(producerRecord, (metadata, e) -> {
            if(e != null) {
                LOG.error("Generic producer send record to kafka failed");
                throw new ServiceUnavailableException("Kafka broker is unavailable, produce message failed", e.getCause());
            } else {
                LOG.info("Successfully sent record to kafka");
            }
        });
        genericKafkaProducer.flush();
    }

    public void publishBatch(List<GenericRecord> records) { publishBatch(topic4Producer, records);}

    public void publishBatch(String topic, List<GenericRecord> records) {
        records.forEach(record -> publishSingleton(topic,record));
    }
}
