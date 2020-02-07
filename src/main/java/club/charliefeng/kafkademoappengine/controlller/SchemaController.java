package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.kafkademoappengine.service.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static club.charliefeng.kafkademoappengine.util.RestUtils.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("schema")
public class SchemaController {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaController.class);

    private final SchemaService schemaService;

    @Autowired
    public SchemaController(SchemaService schemaService) {this.schemaService = schemaService;}

    @GetMapping(path = "/{topic}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getLatestSchema(@PathVariable String topic) {
        String schemaStr = schemaService.getLatestSchema(topic).toString();
        return OK(schemaStr);
    }

    @PostMapping(path = "/update/{topic}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> registerSchema4Topic(@PathVariable String topic,
                                                        @RequestBody String schemaRequest) {
        int schemaId = schemaService.registerSchema(topic,schemaRequest);
        return OK(schemaId);
    }
}

