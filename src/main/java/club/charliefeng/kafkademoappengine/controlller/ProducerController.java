package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.avro.User;
import club.charliefeng.kafkademoappengine.dto.UserRequest;
import club.charliefeng.kafkademoappengine.mapper.UserMapper;
import club.charliefeng.kafkademoappengine.service.AvroGenericBuilderService;
import club.charliefeng.kafkademoappengine.service.GenericProducerService;
import club.charliefeng.kafkademoappengine.service.SchemaService;
import club.charliefeng.kafkademoappengine.service.SpecificProducerService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static club.charliefeng.kafkademoappengine.util.RestUtils.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("publish")
public class ProducerController {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);

    private final SpecificProducerService specificProducerService;
    private final GenericProducerService genericProducerService;
    private final AvroGenericBuilderService genericBuilderService;
    private final SchemaService schemaService;

    @Autowired
    public ProducerController(SpecificProducerService specificProducerService,
                              GenericProducerService genericProducerService,
                              AvroGenericBuilderService genericBuilderService,
                              SchemaService schemaService) {
        this.specificProducerService = specificProducerService;
        this.genericProducerService = genericProducerService;
        this.genericBuilderService = genericBuilderService;
        this.schemaService = schemaService;
    }

    @PostMapping(path = "/specific/singleton", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> publishSpecificRecord(@RequestBody UserRequest request) {
        User user = UserMapper.map(request);
        LOG.info("User recorid is {}", user);
        specificProducerService.publishSingleton(user);
        return CREATED(user.toString());
    }

    @PostMapping(path = "/specific/batch", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> publishSpecificBatch(@RequestBody List<UserRequest> requests) {
        List<User> users = requests.stream().map(UserMapper::map).collect(Collectors.toList());
        specificProducerService.publishBatch(users);
        return CREATED(users.toString());
    }

    @PostMapping(path="/generic/singleton", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> publishGenericRecord(@RequestBody Map<String,Object> request,
                                                       @RequestParam(required = false) String topic) {
        GenericRecord record;
        if(topic == null) {
            Schema schema = schemaService.getLatestSchema4Default();
            record = genericBuilderService.buildGenericRecord(request, schema);
            genericProducerService.publishSingleton(record);
        }else {
            Schema schema = schemaService.getLatestSchema(topic);
            record = genericBuilderService.buildGenericRecord(request, schema);
            genericProducerService.publishSingleton(topic,record);
        }
        return CREATED(record.toString());
    }

    @PostMapping(path = "/generic/batch", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> publishGenericBatch(@RequestBody List<Map<String,Object>> requests,
                                                      @RequestParam(required = false) String topic) {
        List<GenericRecord> records;
        if(topic == null) {
            Schema schema = schemaService.getLatestSchema4Default();
            records = genericBuilderService.genericAvroRecords(requests, schema);
            genericProducerService.publishBatch(records);
        }else {
            Schema schema = schemaService.getLatestSchema(topic);
            records = genericBuilderService.genericAvroRecords(requests, schema);
            genericProducerService.publishBatch(topic,records);
        }
        return CREATED(records.toString());
    }


}
