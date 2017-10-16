package thesis.master.indoorpositioning.service.position.signalbased.signalscanner.factory;

import android.content.Context;

import java.util.Arrays;

import thesis.master.indoorpositioning.service.position.signalbased.converter.model.BluetoothSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.BleSignalScanner;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.HybridSignalScanner;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.SignalScanner;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.WiFiSignalScanner;

public class SignalScannerFactory {

    public static SignalScanner<SignalScanResult> wifiAndBluetoothScanner(Context context) {
        return new HybridSignalScanner(Arrays.asList(
                SignalScannerFactory.wifiScanner(context),
                SignalScannerFactory.fastBluetoothScanner(context)
        ));
    }

    public static SignalScanner<WifiSignalScanResult> wifiScanner(Context context) {
        return new WiFiSignalScanner(context);
    }

    public static SignalScanner<BluetoothSignalScanResult> fastBluetoothScanner(Context context) {
        return new BleSignalScanner(context, 1L);
    }

    public static SignalScanner<BluetoothSignalScanResult> slowBluetoothScanner(Context context) {
        return new BleSignalScanner(context, 2L);
    }

}
