package thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance;

import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;

public interface WiFiDistanceCalculator {

    double calculateDistance(WifiSignalScanResult scanResult);

}
