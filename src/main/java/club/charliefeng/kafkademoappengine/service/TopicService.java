package club.charliefeng.kafkademoappengine.service;

import club.charliefeng.kafkademoappengine.config.AppProperties;
import club.charliefeng.kafkademoappengine.mapper.TopicMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private static Logger LOG = LoggerFactory.getLogger(TopicService.class);

    private ArrayList<AppProperties.Topic> topics;
    private AdminClient kafkaAdminClient;

    @Autowired
    public TopicService(ArrayList<AppProperties.Topic> topics,
                        AdminClient kafkaAdminClient) {
        this.topics = topics;
        this.kafkaAdminClient = kafkaAdminClient;
        createTopics(this.topics);
    }

    public void createTopics(List<AppProperties.Topic> topics) {
        List<NewTopic> newTopics = topics.stream().map(TopicMapper::map).collect(Collectors.toList());
        kafkaAdminClient.createTopics(newTopics);
    }
}
