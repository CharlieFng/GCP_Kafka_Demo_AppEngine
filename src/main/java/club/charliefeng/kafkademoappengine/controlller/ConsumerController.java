package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.avro.User;
import club.charliefeng.kafkademoappengine.dto.UserRequest;
import club.charliefeng.kafkademoappengine.mapper.JsonNodeMapper;
import club.charliefeng.kafkademoappengine.mapper.UserMapper;
import club.charliefeng.kafkademoappengine.service.GenericConsumerService;
import club.charliefeng.kafkademoappengine.service.SpecificConsumerService;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static club.charliefeng.kafkademoappengine.util.RestUtils.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("subscribe")
public class ConsumerController {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerController.class);

    private final SpecificConsumerService specificConsumerService;
    private final GenericConsumerService genericConsumerService;

    @Autowired
    public ConsumerController(SpecificConsumerService specificConsumerService,
                              GenericConsumerService genericConsumerService) {
        this.specificConsumerService = specificConsumerService;
        this.genericConsumerService = genericConsumerService;
    }

    @GetMapping(path = "/specific", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserRequest>> subscribeSpecific() {
        List<User> users = specificConsumerService.subscribe();
        List<UserRequest> res = users.stream().map(UserMapper::map).collect(Collectors.toList());
        return OK(res);
    }

    @GetMapping(path = "/generic", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JsonNode>> subscribeGeneric() {
        List<GenericRecord> records = genericConsumerService.subscribe();
        List<JsonNode> res = records.stream().map(JsonNodeMapper::map).collect(Collectors.toList());
        return OK(res);
    }
}
