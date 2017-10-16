package thesis.master.indoorpositioning.service.position.factory;


import android.content.Context;

import thesis.master.indoorpositioning.service.position.PositionService;
import thesis.master.indoorpositioning.service.position.hybrid.HybridPositionService;
import thesis.master.indoorpositioning.service.position.sensorbased.SensorBasedPositionService;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.factory.MovementDetectorFactory;
import thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator.factory.InertialPositionCalculatorFactory;
import thesis.master.indoorpositioning.service.position.signalbased.SignalBasedPositionService;
import thesis.master.indoorpositioning.service.position.signalbased.factory.SignalBasedPositionCalculatorFactory;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.factory.SignalScannerFactory;

public class PositionServiceFactory {

    public static PositionService hybrid(Context context) {
        return new HybridPositionService(
                MovementDetectorFactory.stepDetector(context),
                InertialPositionCalculatorFactory.destination(),
                SignalScannerFactory.wifiScanner(context),
                SignalBasedPositionCalculatorFactory.knn()
        );
    }

    public static PositionService offlineSignalBased(Context context) {
        return new SignalBasedPositionService(
                SignalScannerFactory.wifiAndBluetoothScanner(context),
                SignalBasedPositionCalculatorFactory.knn()
        );
    }

    public static PositionService onlineSignalBased(Context context, String url) {
        return new SignalBasedPositionService(
                SignalScannerFactory.wifiAndBluetoothScanner(context),
                SignalBasedPositionCalculatorFactory.serverSide(url)
        );
    }

    public static PositionService sensors(Context context) {
        return new SensorBasedPositionService(
                MovementDetectorFactory.stepDetector(context),
                InertialPositionCalculatorFactory.destination()
        );
    }

    public static PositionService trilateration(Context context) {
        return new SignalBasedPositionService(
                SignalScannerFactory.wifiScanner(context),
                SignalBasedPositionCalculatorFactory.trilateration()
        );
    }

}
