package club.charliefeng.kafkademoappengine.mapper;

import club.charliefeng.kafkademoappengine.config.AppProperties;
import club.charliefeng.kafkademoappengine.dto.TopicRequest;
import org.apache.kafka.clients.admin.NewTopic;

public final class TopicMapper {

    public static NewTopic map(AppProperties.Topic topic) {
        NewTopic newTopic = new NewTopic(topic.getName(),
                                         topic.getPartitionNumber(),
                                         topic.getReplicationFactor());
        return newTopic;
    }

    public static AppProperties.Topic map(TopicRequest topicRequest) {
        AppProperties.Topic topic = new AppProperties.Topic();
        topic.setName(topicRequest.getName());
        topic.setPartitionNumber(topicRequest.getPartitionNumber());
        topic.setReplicationFactor(topicRequest.getReplicationFactor());
        return topic;
    }
}
