package club.charliefeng.kafkademoappengine.service;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GenericConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(GenericConsumerService.class);

    private final ArrayList<String> topics4Consumer;
    private final HashMap<String, Object> genericConsumerProps;

    @Autowired
    public GenericConsumerService(ArrayList<String> topics4Consumer, HashMap<String, Object> genericConsumerProps) {
        this.topics4Consumer = topics4Consumer;
        this.genericConsumerProps = genericConsumerProps;
    }

    public List<GenericRecord> subscribe() {return subscribe(topics4Consumer);}

    public List<GenericRecord> subscribe(List<String> topicList) {
        KafkaConsumer<String, GenericRecord> genericConsumer = new KafkaConsumer<>(genericConsumerProps);
        genericConsumer.subscribe(topicList);
        LOG.info("Generic consumer subscribe topics {}, waiting for data ....", topicList);
        List<GenericRecord> result = new ArrayList<>();

        final int max = 5; int zeroPollCount = 0;
        while(true) {
            final ConsumerRecords<String, GenericRecord> consumerRecords = genericConsumer.poll(1000);
            if(consumerRecords.count()==0) {
                zeroPollCount++;
                if(zeroPollCount > max) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                result.add(record.value());
                LOG.info("Consume record {} in partition {} with offset {}", record.value(), record.partition(), record.offset());
            });
            genericConsumer.commitAsync();
        }
        genericConsumer.close();
        LOG.info("Generic consumer has completed subscription");
        return result;
    }
}
