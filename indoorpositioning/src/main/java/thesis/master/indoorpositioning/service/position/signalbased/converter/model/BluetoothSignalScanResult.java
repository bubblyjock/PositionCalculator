package thesis.master.indoorpositioning.service.position.signalbased.converter.model;


public class BluetoothSignalScanResult extends SignalScanResult {

    private Integer tx;

    public BluetoothSignalScanResult() {
    }

    public BluetoothSignalScanResult(String transmitterId, Integer rssi, Integer tx) {
        super(transmitterId, rssi);
        this.tx = tx;
    }

    public Integer getTx() {
        return tx;
    }

    public void setTx(Integer tx) {
        this.tx = tx;
    }

}
