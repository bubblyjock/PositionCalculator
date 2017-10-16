package thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance;

import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;

public class FreePathDistanceCalculator implements WiFiDistanceCalculator {

    @Override
    public double calculateDistance(WifiSignalScanResult scanResult) {
        double exp = (27.55 - (20 * Math.log10(scanResult.getSignalFrequency())) + Math.abs(scanResult.getRssi())) / 20.0;
        return Math.pow(10.0, exp);
    }

}
