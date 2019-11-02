package club.charliefeng.kafkademoappengine.service;

import club.charliefeng.kafkademoappengine.exception.EntityNotFoundException;
import club.charliefeng.kafkademoappengine.exception.SchemaNotCompatibleException;
import club.charliefeng.kafkademoappengine.exception.ServiceUnavailableException;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Collection;

@Service
public class SchemaService {

    private static Logger LOG = LoggerFactory.getLogger(SchemaService.class);

    private static final String DEFAULT_SCHEMA_PATH = "avro/demo.avsc";
    private static final String DEFAULT_CONFLUENT_SCHEMA_NAME_SUFFIX = "-value";

    private final String topic4Producer;
    private final CachedSchemaRegistryClient schemaRegistryClient;
    private final String defaultSchemaName;

    @Autowired
    public SchemaService(String topic4Producer, CachedSchemaRegistryClient schemaRegistryClient) {
        this.topic4Producer = topic4Producer;
        this.schemaRegistryClient = schemaRegistryClient;
        this.defaultSchemaName = topic4Producer + DEFAULT_CONFLUENT_SCHEMA_NAME_SUFFIX;
        try {
            Schema schema = new Schema.Parser().parse(new ClassPathResource(DEFAULT_SCHEMA_PATH).getInputStream());
            schemaRegistryClient.register(defaultSchemaName,schema);
        }catch (IOException e) {
            LOG.error("Schema service initialization failed, check system path: {}", DEFAULT_SCHEMA_PATH);
        }catch (RestClientException e) {
            LOG.error("Schema service register default schema failed");
        }
    }

    public Schema getLatestSchema4Default() {return getLatestSchema(topic4Producer);}

    public Schema getLatestSchema(String topic) {
        String schemaName = topic + DEFAULT_CONFLUENT_SCHEMA_NAME_SUFFIX;
        Schema latestSchema;
        try {
            SchemaMetadata schemaMetadata = schemaRegistryClient.getLatestSchemaMetadata(schemaName);
            latestSchema = schemaRegistryClient.getByID(schemaMetadata.getId());
        }catch (Exception e) {
            throw new EntityNotFoundException(String.format("Schema for topic %s cannot be found", topic));
        }
        return latestSchema;
    }

    public int registerSchema4Default(String schemaStr) { return registerSchema(topic4Producer, schemaStr);}

    public int registerSchema(String topic, String schemaStr) {
        String schemaName = topic + DEFAULT_CONFLUENT_SCHEMA_NAME_SUFFIX;
        Schema toUpdate = new Schema.Parser().parse(schemaStr);
        try {
            Collection<String> subjects = schemaRegistryClient.getAllSubjects();
            if(subjects.contains(schemaName)) {
                boolean isCompatiable = schemaRegistryClient.testCompatibility(schemaName, toUpdate);
                if(!isCompatiable) {
                    throw new SchemaNotCompatibleException("Schema is not compatible with previous register one");
                }
            }
            int schemaId = schemaRegistryClient.register(schemaName, toUpdate);
            return schemaId;
        }catch (SchemaNotCompatibleException e) {
            LOG.error("New schema {} is not compatible with previous, update schema rejected", toUpdate);
            throw new SchemaNotCompatibleException("Schema is not compatible with previous register one");
        }catch (Exception e) {
            LOG.error("Register new schema failed");
            throw new ServiceUnavailableException("Schema registry service is unavailable, register schema failed", e);
        }
    }

}
