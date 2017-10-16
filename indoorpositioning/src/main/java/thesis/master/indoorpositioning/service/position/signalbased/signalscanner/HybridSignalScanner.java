package thesis.master.indoorpositioning.service.position.signalbased.signalscanner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;

public class HybridSignalScanner implements SignalScanner<SignalScanResult> {
    private Observable<List<SignalScanResult>> hybridScanResultsObservable;

    public HybridSignalScanner(List<SignalScanner<? extends SignalScanResult>> signalScanners) {
        assert signalScanners.size() > 1;
        hybridScanResultsObservable = Observable.combineLatest(
                StreamSupport.stream(signalScanners)
                        .map(SignalScanner::getSignalScanResults)
                        .collect(Collectors.toList()),
                this::combineIntoSingleScanResults);
    }

    private List<SignalScanResult> combineIntoSingleScanResults(Object[] signalScanResultsAsObjectsArray) {
        List<SignalScanResult> singleScanResults = new ArrayList<>();
        List<SignalScanResult>[] signalScanResultArray = Arrays.copyOf(signalScanResultsAsObjectsArray, signalScanResultsAsObjectsArray.length, List[].class);
        for (List<SignalScanResult> signalScanResults : signalScanResultArray) {
            singleScanResults.addAll(signalScanResults);
        }
        return singleScanResults;
    }

    @Override
    public Observable<List<SignalScanResult>> getSignalScanResults() {
        return hybridScanResultsObservable;
    }
}
