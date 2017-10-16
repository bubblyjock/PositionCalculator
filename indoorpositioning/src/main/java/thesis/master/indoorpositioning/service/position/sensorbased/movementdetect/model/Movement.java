package thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.model;

public class Movement {

    private float length;
    private float bearing;

    public Movement(float length, float bearing) {
        this.length = length;
        this.bearing = bearing;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}
