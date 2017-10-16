package thesis.master.indoorpositioning.service.position.signalbased.converter.model;

public class SignalScanResult {

    private String transmitterId;

    private Integer rssi;

    public SignalScanResult() {
    }

    public SignalScanResult(String transmitterId, Integer rssi) {
        this.transmitterId = transmitterId;
        this.rssi = rssi;
    }

    public String getTransmitterId() {
        return transmitterId;
    }

    public void setTransmitterId(String transmitterId) {
        this.transmitterId = transmitterId;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

}
