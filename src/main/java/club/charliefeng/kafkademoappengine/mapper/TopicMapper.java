package club.charliefeng.kafkademoappengine.mapper;

import club.charliefeng.kafkademoappengine.config.AppProperties;
import org.apache.kafka.clients.admin.NewTopic;

public final class TopicMapper {

    public static NewTopic map(AppProperties.Topic topic) {
        NewTopic newTopic = new NewTopic(topic.getName(),
                                         topic.getPartitionNumber(),
                                         topic.getReplicationFactor());
        return newTopic;
    }
}
