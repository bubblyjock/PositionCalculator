package thesis.master.indoorpositioning.service.position.signalbased.signalscanner;

import java.util.List;

import io.reactivex.Observable;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;

public interface SignalScanner<T extends SignalScanResult> {

    Observable<List<T>> getSignalScanResults();

}
