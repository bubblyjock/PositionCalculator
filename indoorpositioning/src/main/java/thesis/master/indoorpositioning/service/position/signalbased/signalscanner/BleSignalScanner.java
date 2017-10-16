package thesis.master.indoorpositioning.service.position.signalbased.signalscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import thesis.master.indoorpositioning.service.position.signalbased.converter.SignalScanResultConverter;
import thesis.master.indoorpositioning.service.position.signalbased.converter.factory.SignalScanResultsConverterFactory;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.BluetoothSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.model.BluetoothAdapterScanResult;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BleSignalScanner implements SignalScanner<BluetoothSignalScanResult> {

    private final Context context;
    private final Long bufferingTime;

    private final SignalScanResultConverter<BluetoothAdapterScanResult, BluetoothSignalScanResult> scanResultConverter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback scanResultCallback;
    private Observable<List<BluetoothSignalScanResult>> scanResultsObservable;

    public BleSignalScanner(Context context, Long bufferingTime) {
        this.context = context;
        this.bufferingTime = bufferingTime;
        this.scanResultConverter = SignalScanResultsConverterFactory.bluetoothScanResultsConverter();
        this.scanResultsObservable = bufferedBleScanResultsObservable();
        initBluetooth();
    }

    private Observable<List<BluetoothSignalScanResult>> bufferedBleScanResultsObservable() {
        return Observable
                .create((ObservableEmitter<BluetoothAdapterScanResult> source) -> {
                    scanResultCallback = (bluetoothDevice, rssi, scanRecord) -> source.onNext(new BluetoothAdapterScanResult(bluetoothDevice, rssi, scanRecord));
                    bluetoothAdapter.startLeScan(scanResultCallback);
                })
                .observeOn(Schedulers.computation())
                .buffer(bufferingTime, TimeUnit.SECONDS)
                .doOnDispose(() -> bluetoothAdapter.stopLeScan(scanResultCallback))
                .flatMap(list -> Observable.fromIterable(list).distinct(adapterScanResult -> adapterScanResult.getBluetoothDevice().getAddress()).toList().toObservable())
                .map(scanResultConverter::convert)
                .share();
    }

    private void initBluetooth() {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBluetoothIntent);
        }
    }

    @Override
    public Observable<List<BluetoothSignalScanResult>> getSignalScanResults() {
        return scanResultsObservable;
    }

}
