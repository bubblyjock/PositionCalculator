package thesis.master.indoorpositioning.model;

import java.util.Objects;

import io.realm.RealmObject;

public class Measurement extends RealmObject {

    private String transmitterId;

    private Integer rssi;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return Objects.equals(transmitterId, that.transmitterId) &&
                Objects.equals(rssi, that.rssi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transmitterId, rssi);
    }
}
