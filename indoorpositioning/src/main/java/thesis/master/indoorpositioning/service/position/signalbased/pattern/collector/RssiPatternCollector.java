package thesis.master.indoorpositioning.service.position.signalbased.pattern.collector;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.realm.RealmList;
import thesis.master.indoorpositioning.model.Measurement;
import thesis.master.indoorpositioning.model.Pattern;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.SignalScanner;

public class RssiPatternCollector {
    private final SignalScanner<? extends SignalScanResult> signalScanner;
    private final int noOfMeasurementRepeats;

    private Observable<Pattern> patternsObservable;

    public RssiPatternCollector(SignalScanner<? extends SignalScanResult> signalScanner, int noOfMeasurementRepeats) {
        assert signalScanner != null;
        assert noOfMeasurementRepeats > 0;
        this.signalScanner = signalScanner;
        this.noOfMeasurementRepeats = noOfMeasurementRepeats;
        initPatternsObservable();
    }

    private void initPatternsObservable() {
        patternsObservable = signalScanner.getSignalScanResults()
                .buffer(noOfMeasurementRepeats)
                .map(this::averageRssiPattern);
    }

    private Pattern averageRssiPattern(List<? extends List<? extends SignalScanResult>> bufferedScanResults) {
        Map<String, List<Integer>> groupedMeasurements = groupMeasurementsByTransmitterId(bufferedScanResults);
        return createAverageRssiPattern(groupedMeasurements);
    }

    private Map<String, List<Integer>> groupMeasurementsByTransmitterId(List<? extends List<? extends SignalScanResult>> bufferedScanResults) {
        Map<String, List<Integer>> groupedMeasurements = new HashMap<>();
        for (List<? extends SignalScanResult> bufferedScanResult : bufferedScanResults) {
            for (SignalScanResult signalScanResult : bufferedScanResult) {
                List<Integer> signalHistory = groupedMeasurements.get(signalScanResult.getTransmitterId());
                if (signalHistory == null) {
                    groupedMeasurements.put(signalScanResult.getTransmitterId(), new ArrayList<>(Collections.singletonList(signalScanResult.getRssi())));
                } else {
                    signalHistory.add(signalScanResult.getRssi());
                }
            }
        }
        return groupedMeasurements;
    }

    @NonNull
    private Pattern createAverageRssiPattern(Map<String, List<Integer>> groupedMeasurements) {
        Pattern pattern = new Pattern();
        pattern.setMeasurements(new RealmList<>());
        for (Map.Entry<String, List<Integer>> measurementEntry : groupedMeasurements.entrySet()) {
            Measurement measurement = new Measurement();
            measurement.setTransmitterId(measurementEntry.getKey());
            measurement.setRssi(averageAfterRemovingExtremes(measurementEntry.getValue()));
            pattern.getMeasurements().add(measurement);
        }
        return pattern;
    }

    private Integer averageAfterRemovingExtremes(List<Integer> rssiHistory) {
        if (rssiHistory.size() > 2) {
            removeExtremes(rssiHistory);
        }
        return average(rssiHistory);
    }

    private void removeExtremes(List<Integer> rssiHistory) {
        removeMaxFrom(rssiHistory);
        removeMinFrom(rssiHistory);
    }

    private void removeMaxFrom(List<Integer> rssiHistory) {
        rssiHistory.remove(Collections.max(rssiHistory));
    }

    private void removeMinFrom(List<Integer> rssiHistory) {
        rssiHistory.remove(Collections.min(rssiHistory));
    }

    private int average(List<Integer> rssiHistory) {
        int sum = 0;
        for (int rssiValue : rssiHistory) {
            sum += rssiValue;
        }
        return sum / rssiHistory.size();
    }

    public Observable<Pattern> getPatternsObservable() {
        return patternsObservable;
    }

}
