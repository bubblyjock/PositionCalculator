package thesis.master.indoorpositioning.model;

import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Pattern extends RealmObject {

    private RealmList<Measurement> measurements;

    public RealmList<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(RealmList<Measurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return measurements.containsAll(pattern.measurements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measurements);
    }
}
