package thesis.master.indoorpositioning.service.common.utils;

import java.util.List;

import io.realm.RealmList;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.model.NavigationNode;

public class NavigationNodeTrimmer {

    // Needed to do this, because GSON has problems with serialization of cyclic recursive references
    public List<NavigationNode> trimNeighbourNodes(List<NavigationNode> navigationNodes) {
        return StreamSupport.stream(navigationNodes)
                .map(this::trimNeighbourNodes)
                .collect(Collectors.toList());
    }

    private NavigationNode trimNeighbourNodes(NavigationNode navigationNode) {
        NavigationNode navigationNodeCopy = new NavigationNode();
        navigationNodeCopy.setId(navigationNode.getId());
        navigationNodeCopy.setPosition(navigationNode.getPosition());
        navigationNodeCopy.setPatterns(navigationNode.getPatterns());
        navigationNodeCopy.setNeighbourNodes(new RealmList<>());
        for (NavigationNode neighbourNode : navigationNode.getNeighbourNodes()) {
            NavigationNode navigationNodeWithIdOnly = new NavigationNode();
            navigationNodeWithIdOnly.setId(neighbourNode.getId());
            navigationNodeCopy.getNeighbourNodes().add(navigationNodeWithIdOnly);
        }
        return navigationNodeCopy;
    }

}
