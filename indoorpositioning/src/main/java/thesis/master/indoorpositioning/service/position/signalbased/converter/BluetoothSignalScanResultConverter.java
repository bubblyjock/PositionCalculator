package thesis.master.indoorpositioning.service.position.signalbased.converter;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import java8.util.Objects;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.BluetoothSignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.model.BluetoothAdapterScanResult;

public class BluetoothSignalScanResultConverter implements SignalScanResultConverter<BluetoothAdapterScanResult, BluetoothSignalScanResult> {
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String EDDYSTONE_UID_LAYOUT = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    private static final String EDDYSTONE_URL_LAYOUT = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-21v";
    private static final String KONTAKT_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static final String ALTBEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private final List<BeaconParser> parsers = new ArrayList<>();

    public BluetoothSignalScanResultConverter() {
        List<String> layouts = Arrays.asList(IBEACON_LAYOUT, EDDYSTONE_UID_LAYOUT, EDDYSTONE_URL_LAYOUT, ALTBEACON_LAYOUT, KONTAKT_LAYOUT);
        for (String layout : layouts) {
            parsers.add(new BeaconParser().setBeaconLayout(layout));
        }
    }

    @NonNull
    @Override
    public List<BluetoothSignalScanResult> convert(List<BluetoothAdapterScanResult> scanResults) {
        if (scanResults == null || scanResults.isEmpty()) {
            return Collections.emptyList();
        }
        if (scanResults.contains(null)) {
            throw new IllegalArgumentException();
        }
        return filteredDuplicatedTransmitterId(scanResults);
    }

    private BluetoothSignalScanResult convert(BluetoothAdapterScanResult scanResult) {
        Beacon beacon = parseScanResult(scanResult);

        if (beacon != null) {
            String transmitterId = TextUtils.join("/", beacon.getIdentifiers());
            return new BluetoothSignalScanResult(transmitterId, scanResult.getRssi(), beacon.getTxPower());
        }

        return null;
    }

    private Beacon parseScanResult(BluetoothAdapterScanResult scanResult) {
        Beacon beacon = null;
        for (BeaconParser beaconParser : parsers) {
            Beacon parsedBeacon = beaconParser.fromScanData(scanResult.getScanRecord(), scanResult.getRssi(), scanResult.getBluetoothDevice());
            if (parsedBeacon != null) {
                beacon = parsedBeacon;
                break;
            }
        }
        return beacon;
    }

    private List<BluetoothSignalScanResult> filteredDuplicatedTransmitterId(List<BluetoothAdapterScanResult> scanResults) {
        List<BluetoothSignalScanResult> possibleDuplicates = StreamSupport.stream(scanResults)
                .map(this::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Long> groupedByTransmitterId = StreamSupport.stream(possibleDuplicates)
                .collect(Collectors.groupingBy(SignalScanResult::getTransmitterId, Collectors.counting()));
        List<String> notDuplicatedTransmitterIds = StreamSupport.stream(groupedByTransmitterId.entrySet())
                .filter(scanResultEntry -> scanResultEntry.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return StreamSupport.stream(possibleDuplicates)
                .filter(scanResult -> notDuplicatedTransmitterIds.contains(scanResult.getTransmitterId()))
                .collect(Collectors.toList());
    }

}
