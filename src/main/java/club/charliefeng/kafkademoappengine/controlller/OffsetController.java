package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.kafkademoappengine.service.OffsetService;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static club.charliefeng.kafkademoappengine.util.RestUtils.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("offset")
public class OffsetController {

    private static final Logger LOG = LoggerFactory.getLogger(OffsetController.class);

    private final OffsetService offsetService;

    @Autowired
    public OffsetController(OffsetService offsetService) {
        this.offsetService = offsetService;
    }

    @PostMapping(path = "/{topic}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity reset(@PathVariable String topic,
                                @RequestParam(defaultValue = "earliest") String reset_policy) {
        offsetService.resetOffset(Collections.singletonList(topic),OffsetResetStrategy.valueOf(reset_policy.toUpperCase()));
        return OK();
    }
}
