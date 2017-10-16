package thesis.master.indoorpositioning.service.position.signalbased.common;

import java.util.List;

import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;

public interface SignalBasedPositionCalculator<T extends SignalScanResult> {

    Position calculatePosition(List<T> scanResults);

}
