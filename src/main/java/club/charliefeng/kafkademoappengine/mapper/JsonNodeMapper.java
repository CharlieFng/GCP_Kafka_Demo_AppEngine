package club.charliefeng.kafkademoappengine.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.generic.GenericRecord;

public class JsonNodeMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode map(GenericRecord record) {
        JsonNode res = null;
        try {
            res = mapper.readTree(record.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return res;
    }
}
