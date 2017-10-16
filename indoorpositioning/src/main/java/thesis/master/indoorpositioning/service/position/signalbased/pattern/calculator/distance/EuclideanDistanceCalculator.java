package thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance;

import android.util.Pair;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.model.Measurement;
import thesis.master.indoorpositioning.model.Pattern;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;

public class EuclideanDistanceCalculator implements DistanceCalculator {

    @Override
    public Pair<Double, Integer> calculateDistance(Pattern pattern, List<? extends SignalScanResult> scanResults) {
        List<Integer> patternRssiMeasurements = new ArrayList<>();
        List<Integer> scannedRssiMeasurements = new ArrayList<>();
        int numberOfSamplesInvolved = 0;
        for (Measurement measurement : pattern.getMeasurements()) {
            for (SignalScanResult scanResult : scanResults) {
                if (measurement.getTransmitterId().equals(scanResult.getTransmitterId())) {
                    patternRssiMeasurements.add(measurement.getRssi());
                    scannedRssiMeasurements.add(scanResult.getRssi());
                    numberOfSamplesInvolved++;
                    break;
                }
            }
        }

        if (patternRssiMeasurements.isEmpty()) {
            return Pair.create(Double.MAX_VALUE, 0);
        }

        double[] patternRssiMeasurementsArray = StreamSupport.stream(patternRssiMeasurements).mapToDouble(Integer::doubleValue).toArray();
        double[] scannedRssiMeasurementsArray = StreamSupport.stream(scannedRssiMeasurements).mapToDouble(Integer::doubleValue).toArray();

        double euclideanDistance = new EuclideanDistance().compute(patternRssiMeasurementsArray, scannedRssiMeasurementsArray);

        return Pair.create(euclideanDistance / patternRssiMeasurements.size(), numberOfSamplesInvolved);
    }
}
