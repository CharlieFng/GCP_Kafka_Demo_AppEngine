package club.charliefeng.kafkademoappengine.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OffsetService {

    private static Logger LOG = LoggerFactory.getLogger(OffsetService.class);

    private final ArrayList<String> topics4Consumer;
    private final HashMap<String,Object> genericConsumerProps;

    @Autowired
    public OffsetService(ArrayList<String> topics4Consumer, HashMap<String,Object> genericConsumerProps) {
        this.topics4Consumer = topics4Consumer;
        this.genericConsumerProps = genericConsumerProps;
    }


    public void resetOffset(OffsetResetStrategy strategy){
        resetOffset(topics4Consumer, strategy);
    }

    public void resetOffset(List<String> topics, OffsetResetStrategy strategy) {
        String groupId = (String) genericConsumerProps.get(ConsumerConfig.GROUP_ID_CONFIG);
        genericConsumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "25000");
        genericConsumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-offset");
        LOG.info("Adjusting committed offset to {} for current group id: {}", strategy, groupId);
        KafkaConsumer consumer = new KafkaConsumer(genericConsumerProps);
        LOG.info("Fetching committed offset state....");
        List<TopicPartition> topicPartitions = new ArrayList<>();
        Map<TopicPartition, OffsetAndMetadata> committedOffsets = new HashMap<>();
        topics.forEach(topic -> {
            consumer.partitionsFor(topic).forEach(partition -> {
                PartitionInfo pInfo = (PartitionInfo) partition;
                TopicPartition tp = new TopicPartition(topic, pInfo.partition());
                OffsetAndMetadata om = consumer.committed(tp);
                topicPartitions.add(tp);
                committedOffsets.put(tp,om);
            });
        });
        LOG.info("Committed offset: {}", committedOffsets);
        consumer.subscribe(topics);
        consumer.poll(100);

        LOG.info("Fetching offsets range and reset committed offset according to reset policy {}", strategy);
        Map<TopicPartition, OffsetAndMetadata> updateOffsets = new HashMap<>();
        for(TopicPartition partition: topicPartitions) {
            consumer.seekToBeginning(Collections.singletonList(partition));
            long earliestOffset = consumer.position(partition);
            consumer.seekToEnd(Collections.singletonList(partition));
            long latestOffset = consumer.position(partition);
            OffsetAndMetadata committed = committedOffsets.get(partition);
            OffsetAndMetadata updated = null;
            if(OffsetResetStrategy.EARLIEST.equals(strategy)) {
                updated = new OffsetAndMetadata(earliestOffset);
                LOG.info("Partition {} - {}; currently has committed to {}, will reset offset to earliest {}", partition.topic(), partition.partition(), committed.offset(), earliestOffset);
            } else if(OffsetResetStrategy.LATEST.equals(strategy)) {
                updated = new OffsetAndMetadata(latestOffset);
                LOG.info("Partition {} - {}; currently has committed to {}, will reset offset to latest {}", partition.topic(), partition.partition(), committed.offset(), latestOffset);
            }
            updateOffsets.put(partition, updated);
        }
        LOG.info("Updated offset: {}", updateOffsets);
        consumer.commitSync(updateOffsets);
        consumer.unsubscribe();
        consumer.close();
    }

}
