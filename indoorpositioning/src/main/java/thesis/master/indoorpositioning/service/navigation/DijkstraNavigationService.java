package thesis.master.indoorpositioning.service.navigation;


import org.apache.commons.math3.util.Pair;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thesis.master.indoorpositioning.model.NavigationNode;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.db.repository.NavigationNodesRepository;
import thesis.master.indoorpositioning.service.exception.NavigationNodesNotFoundException;

public class DijkstraNavigationService implements NavigationService {
    private static final int EARTH_RADIUS = 6371000;

    private final SimpleDirectedWeightedGraph<Position, DefaultWeightedEdge> map;

    private final Map<Integer, List<Position>> positionsHavingConnectionWithLevel;

    public DijkstraNavigationService() {
        map = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        positionsHavingConnectionWithLevel = new HashMap<>();
        NavigationNodesRepository navigationNodesRepository = new NavigationNodesRepository();
        List<NavigationNode> navigationNodes = navigationNodesRepository.getAll();
        if (navigationNodes.size() == 0) {
            throw new NavigationNodesNotFoundException();
        }
        createMapFrom(navigationNodes);
        for (Map.Entry<Integer, List<Position>> entry : positionsHavingConnectionWithLevel.entrySet()) {
            assert entry.getValue().size() > 1;
        }
    }

    private void createMapFrom(List<NavigationNode> navigationNodes) {
        for (NavigationNode navigationNode : navigationNodes) {
            map.addVertex(navigationNode.getPosition());
            List<Position> positionsOnLevel = positionsHavingConnectionWithLevel.get((int) navigationNode.getPosition().getHeight());
            if (positionsOnLevel == null) {
                positionsOnLevel = new ArrayList<>();
                positionsHavingConnectionWithLevel.put((int) navigationNode.getPosition().getHeight(), positionsOnLevel);
            }
            positionsOnLevel.add(navigationNode.getPosition());
            for (NavigationNode neighbourNode : navigationNode.getNeighbourNodes()) {
                if ((int) neighbourNode.getPosition().getHeight() != (int) navigationNode.getPosition().getHeight()) {
                    positionsOnLevel.add(neighbourNode.getPosition());
                }
                map.addVertex(neighbourNode.getPosition());
                DefaultWeightedEdge edge = map.addEdge(navigationNode.getPosition(), neighbourNode.getPosition());
                map.setEdgeWeight(edge, 1 / calculateDistance(navigationNode.getPosition(), neighbourNode.getPosition()));
            }
        }
    }

    @Override
    public List<Position> getPath(Position startPosition, Position endPosition) {
        if (positionOnLevelWithoutNodes(startPosition) || positionOnLevelWithoutNodes(endPosition)) {
            return Collections.emptyList();
        }

        connectPositionWithClosestNodesOnLevel(startPosition);
        connectPositionWithClosestNodesOnLevel(endPosition);

        List<DefaultWeightedEdge> pathOfEdges = DijkstraShortestPath.findPathBetween(map, startPosition, endPosition);
        if (pathOfEdges.isEmpty()) {
            return Collections.emptyList();
        }
        List<Position> pathVertices = new ArrayList<>(Collections.singletonList(map.getEdgeSource(pathOfEdges.get(0))));
        for (DefaultWeightedEdge edge : pathOfEdges) {
            pathVertices.add(map.getEdgeTarget(edge));
        }

        map.removeVertex(startPosition);
        map.removeVertex(endPosition);

        return pathVertices;
    }

    private boolean positionOnLevelWithoutNodes(Position position) {
        return positionsHavingConnectionWithLevel.get((int) position.getHeight()) == null;
    }

    private void connectPositionWithClosestNodesOnLevel(Position position) {
        List<Pair<Position, Double>> positionsSortedByDistance = new ArrayList<>();
        List<Position> nodesFromLevel = positionsHavingConnectionWithLevel.get((int) position.getHeight());
        for (Position nodePosition : nodesFromLevel) {
            positionsSortedByDistance.add(Pair.create(nodePosition, calculateDistance(position, nodePosition)));
        }
        Collections.sort(positionsSortedByDistance,
                (firstPositionDistancePair, secondPositionDistancePair)
                        -> (int) (firstPositionDistancePair.getSecond() - secondPositionDistancePair.getSecond()));

        map.addVertex(position);
        DefaultWeightedEdge edge = map.addEdge(position, positionsSortedByDistance.get(0).getFirst());
        map.setEdgeWeight(edge, 1 / positionsSortedByDistance.get(0).getSecond());
        edge = map.addEdge(positionsSortedByDistance.get(0).getFirst(), position);
        map.setEdgeWeight(edge, 1 / positionsSortedByDistance.get(0).getSecond());
        if (map.containsEdge(positionsSortedByDistance.get(0).getFirst(), positionsSortedByDistance.get(1).getFirst())) {
            edge = map.addEdge(position, positionsSortedByDistance.get(1).getFirst());
            map.setEdgeWeight(edge, 1 / positionsSortedByDistance.get(1).getSecond());
            edge = map.addEdge(positionsSortedByDistance.get(1).getFirst(), position);
            map.setEdgeWeight(edge, 1 / positionsSortedByDistance.get(1).getSecond());
        }
    }

    private static Double calculateDistance(Position firstPosition, Position secondPosition) {
        double dLat = Math.toRadians(secondPosition.getLatitude() - firstPosition.getLatitude());
        double dLon = Math.toRadians(secondPosition.getLongitude() - firstPosition.getLongitude());
        double lat1 = Math.toRadians(firstPosition.getLatitude());
        double lat2 = Math.toRadians(secondPosition.getLatitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

}
