package club.charliefeng.kafkademoappengine.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private final KafkaProperties kafkaProps = new KafkaProperties();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KafkaProperties getKafkaProps() {
        return kafkaProps;
    }

    public static class KafkaProperties {
        private ArrayList<Topic> topics = new ArrayList<>();
        private HashMap<String, Object> producerConfig = new HashMap<>();
        private HashMap<String, Object> consumerConfig = new HashMap<>();

        public ArrayList<Topic> getTopics() {
            return topics;
        }

        public void setTopics(ArrayList<Topic> topics) {
            this.topics = topics;
        }

        public HashMap<String, Object> getProducerConfig() {
            return producerConfig;
        }

        public void setProducerConfig(HashMap<String, Object> producerConfig) {
            this.producerConfig = producerConfig;
        }

        public HashMap<String, Object> getConsumerConfig() {
            return consumerConfig;
        }

        public void setConsumerConfig(HashMap<String, Object> consumerConfig) {
            this.consumerConfig = consumerConfig;
        }
    }

    public static class Topic {
        private String name;
        private int partitionNumber;
        private short replicationFactor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPartitionNumber() {
            return partitionNumber;
        }

        public void setPartitionNumber(int partitionNumber) {
            this.partitionNumber = partitionNumber;
        }

        public short getReplicationFactor() {
            return replicationFactor;
        }

        public void setReplicationFactor(short replicationFactor) {
            this.replicationFactor = replicationFactor;
        }
    }
}
