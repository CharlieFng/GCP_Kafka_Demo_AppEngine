package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.avro.User;
import club.charliefeng.kafkademoappengine.dto.UserRequest;
import club.charliefeng.kafkademoappengine.mapper.UserMapper;
import club.charliefeng.kafkademoappengine.service.SpecificConsumerService;
import club.charliefeng.kafkademoappengine.service.SpecificProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static club.charliefeng.kafkademoappengine.util.RestUtils.CREATED;
import static club.charliefeng.kafkademoappengine.util.RestUtils.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("subscribe")
public class ConsumerController {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerController.class);

    private final SpecificConsumerService specificConsumerService;

    @Autowired
    public ConsumerController(SpecificConsumerService specificConsumerService) {
        this.specificConsumerService = specificConsumerService;
    }

    @GetMapping(path = "/specific", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<UserRequest>> subscribeSpecific() {
        List<User> users = specificConsumerService.subscribe();
        List<UserRequest> res = users.stream().map(UserMapper::map).collect(Collectors.toList());
        return OK(res);
    }

}
