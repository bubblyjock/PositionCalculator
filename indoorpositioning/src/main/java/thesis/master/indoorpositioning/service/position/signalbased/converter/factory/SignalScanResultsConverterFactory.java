package thesis.master.indoorpositioning.service.position.signalbased.converter.factory;

import android.net.wifi.ScanResult;

import thesis.master.indoorpositioning.service.position.signalbased.converter.BluetoothSignalScanResultConverter;
import thesis.master.indoorpositioning.service.position.signalbased.converter.SignalScanResultConverter;
import thesis.master.indoorpositioning.service.position.signalbased.converter.WiFiSignalScanResultsConverter;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.BluetoothSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.model.BluetoothAdapterScanResult;

public class SignalScanResultsConverterFactory {

    public static SignalScanResultConverter<ScanResult, WifiSignalScanResult> wifiScanResultsConverter() {
        return new WiFiSignalScanResultsConverter();
    }

    public static SignalScanResultConverter<BluetoothAdapterScanResult, BluetoothSignalScanResult> bluetoothScanResultsConverter() {
        return new BluetoothSignalScanResultConverter();
    }

}
