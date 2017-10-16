package thesis.master.indoorpositioning.service.position.signalbased.signalscanner.model;

import android.bluetooth.BluetoothDevice;

public class BluetoothAdapterScanResult {

    private BluetoothDevice bluetoothDevice;

    private int rssi;

    private byte[] scanRecord;

    public BluetoothAdapterScanResult(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }
}
