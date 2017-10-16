package thesis.master.indoorpositioning.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Transmitter extends RealmObject {

    @PrimaryKey
    private String transmitterId;

    private Position position;

    public String getTransmitterId() {
        return transmitterId;
    }

    public void setTransmitterId(String transmitterId) {
        this.transmitterId = transmitterId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
