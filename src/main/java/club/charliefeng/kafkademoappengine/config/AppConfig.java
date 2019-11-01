package club.charliefeng.kafkademoappengine.config;

import club.charliefeng.avro.User;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Autowired
    public AppProperties appProperties;

    @Bean
    @Qualifier("genericProducerProps")
    public static HashMap<String, Object> genericProducerProps(AppProperties appProperties) {
        return appProperties.getKafkaProps().getProducerConfig();
    }

    @Bean
    @Qualifier("genericConsumerProps")
    public static HashMap<String, Object> genericConsumerProps(AppProperties appProperties) {
        return appProperties.getKafkaProps().getConsumerConfig();
    }

    @Bean
    @Qualifier("specificProducerProps")
    public static HashMap<String, Object> specificProducerProps(AppProperties appProperties) {
        return appProperties.getKafkaProps().getProducerConfig();
    }

    @Bean
    @Qualifier("specificConsumerProps")
    public static HashMap<String, Object> specificConsumerProps(AppProperties appProperties) {
        HashMap<String, Object> specificConsumerProps = new HashMap<>(appProperties.getKafkaProps().getConsumerConfig());
        specificConsumerProps.put("specific.avro.reader", "true");
        return specificConsumerProps;
    }

    @Bean
    public static ArrayList<AppProperties.Topic> topics(AppProperties appProperties) {
        return appProperties.getKafkaProps().getTopics();
    }

    @Bean
    public static ArrayList<String> topics4Consumer(AppProperties appProperties) {
        return (ArrayList<String>) appProperties.getKafkaProps().getTopics()
                    .stream().map(AppProperties.Topic::getName).collect(Collectors.toList());
    }

    @Bean
    public static String topic4Producer(AppProperties appProperties) {
        return appProperties.getKafkaProps().getTopics().get(0).getName();
    }

    @Bean
    public static KafkaProducer<String, GenericRecord> genericKafkaProducer(
            @Qualifier("genericProducerProps") HashMap<String, Object> producerProps) {
        return new KafkaProducer<>(producerProps);
    }

    @Bean
    public static KafkaProducer<String, User> specificKafkaProducer(
            @Qualifier("specificProducerProps") HashMap<String, Object> producerProps) {
        return new KafkaProducer<>(producerProps);
    }

    @Bean
    public static CachedSchemaRegistryClient schemaRegistryClient(
            @Qualifier("genericProducerProps") HashMap<String, Object> producerProps) {
        return new CachedSchemaRegistryClient(producerProps.get("schema.registry.url").toString(), 100);
    }

    @Bean
    public static AdminClient kafkaAdminClient(
            @Qualifier("specificProducerProps") HashMap<String, Object> producerProps) {
        return AdminClient.create(producerProps);
    }



}
