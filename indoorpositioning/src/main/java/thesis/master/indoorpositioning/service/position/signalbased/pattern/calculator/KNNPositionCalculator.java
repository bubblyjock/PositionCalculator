package thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java8.util.Comparators;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.model.NavigationNode;
import thesis.master.indoorpositioning.model.Pattern;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.db.repository.NavigationNodesRepository;
import thesis.master.indoorpositioning.service.exception.NavigationNodesNotFoundException;
import thesis.master.indoorpositioning.service.position.signalbased.common.SignalBasedPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance.DistanceCalculator;

public class KNNPositionCalculator implements SignalBasedPositionCalculator<SignalScanResult> {

    private final DistanceCalculator distanceCalculator;
    private final List<NavigationNode> navigationNodes;
    private final int k;

    public KNNPositionCalculator(DistanceCalculator distanceCalculator, int k) {
        NavigationNodesRepository navigationNodesRepository = new NavigationNodesRepository();
        this.navigationNodes = navigationNodesRepository.getAll();
        if (this.navigationNodes.isEmpty()) {
            throw new NavigationNodesNotFoundException();
        }
        this.distanceCalculator = distanceCalculator;
        this.k = k;
    }

    @Override
    public Position calculatePosition(List<SignalScanResult> scanResults) {
        List<Pair<Position, Double>> positionsWithWeights = calculatePositionsWithWeights(scanResults);
        List<Pair<Position, Double>> kPositions = findKNearestPositions(positionsWithWeights);
        return findMostFrequentPosition(kPositions);
    }

    private List<Pair<Position, Double>> calculatePositionsWithWeights(List<? extends SignalScanResult> scanResults) {
        List<Pair<Position, Double>> positionsWithDistances = new ArrayList<>();
        for (NavigationNode navigationNode : navigationNodes) {
            List<Pattern> patterns = navigationNode.getPatterns();
            for (Pattern pattern : patterns) {
                Pair<Double, Integer> distanceWithWeight = distanceCalculator.calculateDistance(pattern, scanResults);
                positionsWithDistances.add(Pair.create(navigationNode.getPosition(), distanceWithWeight.second / distanceWithWeight.first));
            }
        }
        return positionsWithDistances;
    }

    private List<Pair<Position, Double>> findKNearestPositions(List<Pair<Position, Double>> positionsWithWeights) {
        return StreamSupport.stream(positionsWithWeights)
                .sorted(Comparators.reversed((first, second) -> first.second.compareTo(second.second)))
                .limit(k)
                .collect(Collectors.toList());
    }

    private Position findMostFrequentPosition(List<Pair<Position, Double>> kPositions) {
        Map<Position, Double> positionsWithSummedWeights = StreamSupport.stream(kPositions)
                .collect(Collectors.groupingBy(pair -> pair.first, Collectors.summingDouble(pair -> pair.second)));

        return StreamSupport.stream(positionsWithSummedWeights.entrySet())
                .max(Comparators.comparing(Map.Entry::getValue))
                .get()
                .getKey();
    }

}
