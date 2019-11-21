package club.charliefeng.kafkademoappengine.service;

import club.charliefeng.kafkademoappengine.error.ApiError;
import club.charliefeng.kafkademoappengine.error.ApiSubError;
import club.charliefeng.kafkademoappengine.error.ApiValidationError;
import club.charliefeng.kafkademoappengine.exception.ValidationException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvroGenericBuilderService {

    private static Logger LOG = LoggerFactory.getLogger(AvroGenericBuilderService.class);

    public List<GenericRecord> genericAvroRecords(List<Map<String,Object>> inputs, Schema schema) {
        return inputs.stream().map(it -> buildGenericRecord(it,schema)).collect(Collectors.toList());
    }

    public GenericRecord buildGenericRecord(Map<String,Object> params, Schema schema) {
        ApiError error = new ApiError();
        GenericRecord record = toGenericRecord(params,schema,error,null);
        if (!error.getSubErrors().isEmpty() || record == null) {
            error.setStatus(HttpStatus.BAD_REQUEST);
            error.setMessage("Payload validation failed");
            throw new ValidationException(error);
        } else {
            return record;
        }
    }

    private GenericRecord toGenericRecord(Map<String,Object> params, Schema schema, ApiError error, String parent) {
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        for(Schema.Field field: schema.getFields()) {
            Schema fieldSchema = field.schema();
            String fieldName = field.name();
            String fieldNameSnake = fieldName.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            if (params.containsKey(fieldNameSnake)) {
                Object fieldValue = params.get(fieldNameSnake);
                Object convertedValue = wrapOption(fieldNameSnake,fieldValue,fieldSchema,error);
                if(convertedValue instanceof ApiValidationError) {
                    ApiValidationError subError = (ApiValidationError) convertedValue;
                    if(parent != null) subError.setName(parent + "." + subError.getName());
                    error.getSubErrors().add(subError);
                } else if (convertedValue != null) {
                    builder.set(fieldName, convertedValue);
                }
            }else if(field.hasDefaultValue() || schema.isNullable()) {
                builder.set(fieldName, field.defaultVal());
            }else {
                String name = (parent==null ? fieldNameSnake : parent + "." + fieldNameSnake);
                ApiSubError subError = new ApiValidationError(name,fieldSchema.getType().getName());
                error.getSubErrors().add(subError);
            }
        }
        if(error.getSubErrors().isEmpty()) return builder.build();
        return null;
    }

    @SuppressWarnings("unchecked")
    private Object wrapOption(String name, Object option, Schema schema, ApiError error) {
        if (schema.getType() == Schema.Type.BYTES && option instanceof String) {
            option = ByteBuffer.wrap(((String) option).getBytes(Charset.defaultCharset()));
        } else if (schema.getType() == Schema.Type.FLOAT && option instanceof Double) {
            option = ((Double) option).floatValue();
        } else if (schema.getType() == Schema.Type.LONG && option instanceof Integer) {
            option = ((Integer) option).longValue();
        } else if (schema.getType() == Schema.Type.ARRAY && option instanceof Collection) {
            option = new GenericData.Array(schema, (Collection) option);
        } else if (schema.getType() == Schema.Type.ENUM && option instanceof String) {
            option = new GenericData.EnumSymbol(schema, (String) option);
        } else if (schema.getType() == Schema.Type.FIXED && option instanceof String) {
            option = new GenericData.Fixed(schema, ((String) option).getBytes(Charset.defaultCharset()));
        } else if (schema.getType() == Schema.Type.RECORD && option instanceof Map) {
            Map<String, Object> optionMap = (Map) option;
            option = toGenericRecord(optionMap, schema, error, name);
        } else if(schema.getType() == Schema.Type.UNION) {
            Schema second = schema.getTypes().get(1);
            return wrapOption(name, option, second, error);
        } else if ((schema.getType() == Schema.Type.NULL && option == null)
                   ||(schema.getType() == Schema.Type.BOOLEAN && option instanceof Boolean)
                   ||(schema.getType() == Schema.Type.INT && option instanceof Integer)
                   ||(schema.getType() == Schema.Type.LONG && option instanceof Long)
                   ||(schema.getType() == Schema.Type.FLOAT && option instanceof Float)
                   ||(schema.getType() == Schema.Type.DOUBLE && option instanceof Double)
                   ||(schema.getType() == Schema.Type.STRING && option instanceof String)
                   ||(schema.getType() == Schema.Type.MAP && option instanceof Map)
                  ) {
        } else {
            return new ApiValidationError(name,option,schema.getType().getName());
        }
        return option;
    }
}
