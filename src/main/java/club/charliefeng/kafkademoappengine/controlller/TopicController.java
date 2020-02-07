package club.charliefeng.kafkademoappengine.controlller;

import club.charliefeng.kafkademoappengine.config.AppProperties;
import club.charliefeng.kafkademoappengine.dto.TopicRequest;
import club.charliefeng.kafkademoappengine.mapper.TopicMapper;
import club.charliefeng.kafkademoappengine.service.TopicService;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("topic")
public class TopicController {

    private static final Logger LOG = LoggerFactory.getLogger(TopicController.class);

    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {this.topicService = topicService;}

    @PostMapping(path = "/create", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity createTopic(@RequestBody List<TopicRequest> requests) {
        List<AppProperties.Topic> topics = requests.stream()
                .map(TopicMapper::map).collect(Collectors.toList());
        topicService.createTopics(topics);
        return CREATED();
    }

}
