package thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.model.Transmitter;
import thesis.master.indoorpositioning.service.db.repository.TransmittersRepository;
import thesis.master.indoorpositioning.service.exception.TransmittersNotFoundException;
import thesis.master.indoorpositioning.service.position.signalbased.common.SignalBasedPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance.WiFiDistanceCalculator;

//TODO Refactor that class to be more human readable
public class TrilaterationPositionCalculator implements SignalBasedPositionCalculator<WifiSignalScanResult> {
    private static final int EARTH_RADIUS = 6371000;

    private final WiFiDistanceCalculator distanceCalculator;
    private final List<String> transmitterIds;
    private final double[][] transmittersPositions;

    private Position previousPosition;

    public TrilaterationPositionCalculator(WiFiDistanceCalculator wiFiDistanceCalculator) {
        this.distanceCalculator = wiFiDistanceCalculator;
        TransmittersRepository transmittersRepository = new TransmittersRepository();
        List<Transmitter> allTransmitters = transmittersRepository.getAll();
        if (allTransmitters.size() == 0) {
            throw new TransmittersNotFoundException();
        }
        this.transmitterIds = StreamSupport.stream(allTransmitters)
                .map(Transmitter::getTransmitterId)
                .collect(Collectors.toList());
        this.transmittersPositions = getPositions(StreamSupport.stream(allTransmitters)
                .sorted((left, right) -> left.getTransmitterId().compareTo(right.getTransmitterId()))
                .collect(Collectors.toList()));
        this.previousPosition = new Position();
    }

    private double[][] getPositions(List<Transmitter> transmitters) {
        List<List<Double>> transmittersPositions = new ArrayList<>();
        for (Transmitter transmitter : transmitters) {
            List<Double> transmitterPositions = new ArrayList<>();
            double longitudeRad = Math.toRadians(transmitter.getPosition().getLongitude());
            double latitudeRad = Math.toRadians(transmitter.getPosition().getLatitude());
            transmitterPositions.add(convertToCartesianX(longitudeRad, latitudeRad));
            transmitterPositions.add(convertToCartesianY(longitudeRad, latitudeRad));
            transmitterPositions.add(convertToCartesianZ(latitudeRad));
            transmittersPositions.add(transmitterPositions);
        }
        double[][] transmittersPositionsArray = new double[transmittersPositions.size()][];
        for (int i = 0; i < transmittersPositions.size(); ++i) {
            transmittersPositionsArray[i] = StreamSupport.stream(transmittersPositions.get(i)).mapToDouble(Double::doubleValue).toArray();
        }
        return transmittersPositionsArray;
    }

    private double convertToCartesianX(double longitude, double latitude) {
        return EARTH_RADIUS * Math.cos(latitude) * Math.cos(longitude);
    }

    private double convertToCartesianY(double longitude, double latitude) {
        return EARTH_RADIUS * Math.cos(latitude) * Math.sin(longitude);
    }

    private double convertToCartesianZ(double latitude) {
        return EARTH_RADIUS * Math.sin(latitude);
    }

    @Override
    public Position calculatePosition(List<WifiSignalScanResult> scanResults) {
        List<WifiSignalScanResult> filteredScanResults = filterNotKnownTransmitters(scanResults);
        double[] distances = getDistances(filteredScanResults);

        if (distances.length < 3) {
            return previousPosition;
        }

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(transmittersPositions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();
        double[] point = optimum.getPoint().toArray();

        Position position = new Position();
        position.setLongitude(convertToLongitude(point));
        position.setLatitude(convertToLatitude(point));
        position.setHeight(0);

        previousPosition = position;

        return position;
    }

    private double convertToLongitude(double[] cartesianPoint) {
        return Math.toDegrees(Math.atan2(cartesianPoint[1], cartesianPoint[0]));
    }

    private double convertToLatitude(double[] cartesianPoint) {
        return Math.toDegrees(Math.asin(cartesianPoint[2] / EARTH_RADIUS));
    }

    private List<WifiSignalScanResult> filterNotKnownTransmitters(List<? extends WifiSignalScanResult> notFilteredScanResults) {
        return StreamSupport.stream(notFilteredScanResults)
                .filter(scanResult -> transmitterIds.contains(scanResult.getTransmitterId()))
                .collect(Collectors.toList());
    }

    private double[] getDistances(List<WifiSignalScanResult> scanResults) {
        List<WifiSignalScanResult> sortedScanResults = StreamSupport.stream(scanResults)
                .sorted((left, right) -> left.getTransmitterId().compareTo(right.getTransmitterId()))
                .collect(Collectors.toList());
        List<Double> distances = new ArrayList<>();
        for (WifiSignalScanResult scanResult : sortedScanResults) {
            distances.add(distanceCalculator.calculateDistance(scanResult));
        }
        return StreamSupport.stream(distances).mapToDouble(Double::doubleValue).toArray();
    }

}
