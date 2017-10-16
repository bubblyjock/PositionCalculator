package thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance;

import android.util.Pair;

import java.util.List;

import thesis.master.indoorpositioning.model.Pattern;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;


public interface DistanceCalculator {

    Pair<Double, Integer> calculateDistance(Pattern pattern, List<? extends SignalScanResult> scanResults);
}
