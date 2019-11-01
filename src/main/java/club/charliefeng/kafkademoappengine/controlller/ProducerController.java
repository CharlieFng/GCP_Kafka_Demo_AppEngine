package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.avro.User;
import club.charliefeng.kafkademoappengine.dto.UserRequest;
import club.charliefeng.kafkademoappengine.mapper.UserMapper;
import club.charliefeng.kafkademoappengine.service.SpecificProducerService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static club.charliefeng.kafkademoappengine.util.RestUtils.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("publish")
public class ProducerController {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);

    private final SpecificProducerService specificProducerService;

    @Autowired
    public ProducerController(SpecificProducerService specificProducerService) {
        this.specificProducerService = specificProducerService;
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

}
