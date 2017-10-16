package thesis.master.indoorpositioning.service.position.signalbased.converter;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.WifiSignalScanResult;

public class WiFiSignalScanResultsConverter implements SignalScanResultConverter<ScanResult, WifiSignalScanResult> {

    @NonNull
    @Override
    public List<WifiSignalScanResult> convert(List<ScanResult> scanResults) {
        if (scanResults == null || scanResults.isEmpty()) {
            return Collections.emptyList();
        }
        if (scanResults.contains(null)) {
            throw new IllegalArgumentException();
        }
        return StreamSupport.stream(filterResultsWithDuplicatedBSSID(scanResults))
                .map(scanResult -> new WifiSignalScanResult(scanResult.BSSID, scanResult.level, scanResult.frequency))
                .collect(Collectors.toList());
    }

    private List<ScanResult> filterResultsWithDuplicatedBSSID(List<ScanResult> scanResults) {
        Map<String, Long> groupedByBSSIDs = StreamSupport.stream(scanResults)
                .collect(Collectors.groupingBy(scanResult -> scanResult.BSSID, Collectors.counting()));
        List<String> notDuplicatedBSSIDs = StreamSupport.stream(groupedByBSSIDs.entrySet())
                .filter(scanResultEntry -> scanResultEntry.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return StreamSupport.stream(scanResults)
                .filter(scanResult -> notDuplicatedBSSIDs.contains(scanResult.BSSID))
                .collect(Collectors.toList());
    }

}
