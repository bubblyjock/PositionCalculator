package thesis.master.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import thesis.master.model.core.Measurement;
import thesis.master.service.classification.LevelClassifierService;
import thesis.master.service.classification.NavigationNodeIdClassifierService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/classifier")
@Slf4j
public class ClassifierEndpoint {

    @Resource
    private NavigationNodeIdClassifierService navigationNodeIdClassifierService;

    @Resource
    private LevelClassifierService levelClassifierService;

    @RequestMapping(method = RequestMethod.POST, path = "/navigationNodeId")
    public String getNavigationNodeId(@RequestBody List<Measurement> measurements) {
        log.info("Getting navigationNodeId for measurements {}", measurements);
        return navigationNodeIdClassifierService.classify(measurements);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/level")
    public Double getLevel(@RequestBody List<Measurement> measurements) {
        log.info("Getting level for measurements {}", measurements);
        return levelClassifierService.classify(measurements);
    }

}
