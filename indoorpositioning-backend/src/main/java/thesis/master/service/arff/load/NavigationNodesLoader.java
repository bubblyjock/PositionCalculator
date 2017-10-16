package thesis.master.service.arff.load;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import thesis.master.model.core.NavigationNode;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NavigationNodesLoader {

    public List<NavigationNode> loadNavigationNodes() throws IOException {
        log.info("Loading navigation nodes from JSON.");
        ObjectMapper objectMapper = new ObjectMapper();
        List<NavigationNode> navigationNodes = objectMapper.readValue(getClass().getClassLoader().getResourceAsStream("navigationNodes.json"), new TypeReference<List<NavigationNode>>() {});
        log.info("Navigation nodes has been loaded");
        return navigationNodes;
    }

}
