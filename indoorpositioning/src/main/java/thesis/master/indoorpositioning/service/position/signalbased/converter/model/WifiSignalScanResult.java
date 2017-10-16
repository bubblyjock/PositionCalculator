package thesis.master.indoorpositioning.service.position.signalbased.converter.model;


public class WifiSignalScanResult extends SignalScanResult {

    private Integer signalFrequency;

    public WifiSignalScanResult() {
    }

    public WifiSignalScanResult(String transmitterId, Integer rssi, Integer signalFrequency) {
        super(transmitterId, rssi);
        this.signalFrequency = signalFrequency;
    }

    public Integer getSignalFrequency() {
        return signalFrequency;
    }

    public void setSignalFrequency(Integer signalFrequency) {
        this.signalFrequency = signalFrequency;
    }

}
