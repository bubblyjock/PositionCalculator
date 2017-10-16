package thesis.master.indoorpositioning.model;

import java.util.Objects;

import io.realm.RealmObject;

public class Position extends RealmObject {

    private double longitude;

    private double latitude;

    private double height;

    public Position() {
    }

    public Position(double latitude, double longitude, double height) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.longitude, longitude) == 0 &&
                Double.compare(position.latitude, latitude) == 0 &&
                Double.compare(position.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, height);
    }
}
