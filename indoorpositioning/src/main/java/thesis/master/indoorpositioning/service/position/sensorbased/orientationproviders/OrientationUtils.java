package thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders;

import android.hardware.SensorManager;

public final class OrientationUtils {

    public static final int SENSOR_REFRESH_RATE = SensorManager.SENSOR_DELAY_GAME;

    private OrientationUtils() {

    }

    public static float getNormalizedAzimuth(float radianAzimuth) {
        float degAzimuth = (float) Math.toDegrees(radianAzimuth);
        degAzimuth = (degAzimuth + 360) % 360;
        return degAzimuth;
    }

    public static float lowPassFilter(float a, float lastValue, float newValue) {
        return a * newValue + (1 - a) * lastValue;
    }

    public static float getMagnitude(float... array) {
        double squareSum = 0;
        for (float i : array) {
            squareSum += i * i;
        }
        return (float) Math.sqrt(squareSum);
    }

    public static float getAccelerationMagnitude(float... array) {
        return getMagnitude(array) - SensorManager.GRAVITY_EARTH;
    }
}