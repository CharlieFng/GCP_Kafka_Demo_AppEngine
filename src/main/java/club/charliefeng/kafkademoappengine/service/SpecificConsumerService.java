package club.charliefeng.kafkademoappengine.service;

import club.charliefeng.avro.User;
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
public class SpecificConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(SpecificConsumerService.class);

    private final ArrayList<String> topics4Consumer;
    private final HashMap<String, Object> specificConsumerProps;

    @Autowired
    public SpecificConsumerService(ArrayList<String> topics4Consumer, HashMap<String, Object> specificConsumerProps) {
        this.topics4Consumer = topics4Consumer;
        this.specificConsumerProps = specificConsumerProps;
    }

    public List<User> subscribe() {
        KafkaConsumer<String, User> specificConsumer = new KafkaConsumer<>(specificConsumerProps);
        specificConsumer.subscribe(topics4Consumer);
        LOG.info("Specific consumer subscribe topics {}, waiting for data ....", topics4Consumer);
        List<User> result = new ArrayList<>();

        final int max = 5; int zeroPollCount = 0;
        while(true) {
            final ConsumerRecords<String, User> consumerRecords = specificConsumer.poll(1000);
            if(consumerRecords.count()==0) {
                zeroPollCount++;
                if(zeroPollCount > max) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                result.add(record.value());
                LOG.info("Specific record {} in partition {} with offset {}", record.value(), record.partition(), record.offset());
            });
            specificConsumer.commitAsync();
        }
        specificConsumer.close();
        LOG.info("Specific consumer has completed subscription");
        return result;
    }
}
