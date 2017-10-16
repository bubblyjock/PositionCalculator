package thesis.master.indoorpositioning.service.position.signalbased.factory;


import thesis.master.indoorpositioning.service.position.signalbased.common.SignalBasedPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.KNNPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.ServerSidePositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance.factory.DistanceCalculatorFactory;
import thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.TrilaterationPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance.factory.WiFiDistanceCalculatorFactory;

public class SignalBasedPositionCalculatorFactory {

    public static SignalBasedPositionCalculator<SignalScanResult> knn() {
        return new KNNPositionCalculator(
                DistanceCalculatorFactory.euclidean(),
                4
        );
    }

    public static SignalBasedPositionCalculator<SignalScanResult> serverSide(String url) {
        return new ServerSidePositionCalculator(url, 2L);
    }

    public static SignalBasedPositionCalculator<WifiSignalScanResult> trilateration() {
        return new TrilaterationPositionCalculator(WiFiDistanceCalculatorFactory.freePath());
    }

}
