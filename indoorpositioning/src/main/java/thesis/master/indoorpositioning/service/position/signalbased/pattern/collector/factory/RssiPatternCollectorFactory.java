package thesis.master.indoorpositioning.service.position.signalbased.pattern.collector.factory;


import android.content.Context;

import thesis.master.indoorpositioning.service.position.signalbased.pattern.collector.RssiPatternCollector;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.factory.SignalScannerFactory;

public class RssiPatternCollectorFactory {

    public static RssiPatternCollector wifiPatternCollector(Context context) {
        return new RssiPatternCollector(
                SignalScannerFactory.wifiScanner(context),
                4
        );
    }

    public static RssiPatternCollector bluetoothPatternCollector(Context context) {
        return new RssiPatternCollector(
                SignalScannerFactory.fastBluetoothScanner(context),
                4
        );
    }

    public static RssiPatternCollector wifiAndBluetoothPatternCollector(Context context) {
        return new RssiPatternCollector(
                SignalScannerFactory.wifiAndBluetoothScanner(context),
                4
        );
    }

}
