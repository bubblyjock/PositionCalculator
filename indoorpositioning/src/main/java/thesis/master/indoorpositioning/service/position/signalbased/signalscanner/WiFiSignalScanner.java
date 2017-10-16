package thesis.master.indoorpositioning.service.position.signalbased.signalscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import thesis.master.indoorpositioning.service.position.signalbased.converter.SignalScanResultConverter;
import thesis.master.indoorpositioning.service.position.signalbased.converter.factory.SignalScanResultsConverterFactory;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;

public class WiFiSignalScanner implements SignalScanner<WifiSignalScanResult> {

    private final Context context;

    private final SignalScanResultConverter<ScanResult, WifiSignalScanResult> scanResultConverter;
    private WifiManager wifiManager;
    private BroadcastReceiver broadcastReceiver;
    private HandlerThread handlerThread;
    private Observable<List<WifiSignalScanResult>> scanResultsObservable;

    public WiFiSignalScanner(Context context) {
        this.context = context;
        this.scanResultConverter = SignalScanResultsConverterFactory.wifiScanResultsConverter();
        this.scanResultsObservable = scanResultsObservable();
        initWifi();
    }

    private Observable<List<WifiSignalScanResult>> scanResultsObservable() {
        return Observable.create((ObservableEmitter<List<ScanResult>> source) -> {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    source.onNext(wifiManager.getScanResults());
                    wifiManager.startScan();
                }
            };
            handlerThread = new HandlerThread("wifiBroadcastReceiverHandlerThread");
            handlerThread.start();
            Looper looper = handlerThread.getLooper();
            Handler handler = new Handler(looper);
            context.registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);
            wifiManager.startScan();
        })
                .observeOn(Schedulers.computation())
                .sample(50, TimeUnit.MILLISECONDS)
                .doOnDispose(() -> {
                    context.unregisterReceiver(broadcastReceiver);
                    handlerThread.quit();
                })
                .map(scanResultConverter::convert)
                .share();
    }

    private void initWifi() {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            scanResultsObservable = Observable.empty();
        }
    }

    @Override
    public Observable<List<WifiSignalScanResult>> getSignalScanResults() {
        return scanResultsObservable;
    }

}
