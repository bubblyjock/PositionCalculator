package thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import thesis.master.indoorpositioning.service.common.observer.ObservableHelper;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationUtils;

import static thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationUtils.getNormalizedAzimuth;
import static thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationUtils.lowPassFilter;

public class AccMagOrientationProvider implements OrientationProvider {

    public static final float ALPHA = 0.1f;
    private final ObservableHelper observableHelper = new ObservableHelper();
    private final SensorManager sensorManager;
    private final Sensor accelerometerSensor;
    private final Sensor magneticSensor;

    private final float[] rotationMatrix = new float[16];
    private final float[] orientationData = new float[3];
    private final float[] filteredAcc = new float[3];
    private final float[] filteredMag = new float[3];
    private float[] rawAcc = new float[3];
    private float[] rawMag = new float[3];
    private float bearing;

    public AccMagOrientationProvider(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            rawAcc = event.values.clone();
            processAccelerationData();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            rawMag = event.values.clone();
            processMagneticData();
        }

        calculateDeviceOrientation(filteredAcc, filteredMag);
        observableHelper.notifyObservers(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public float getOrientation() {
        return bearing;
    }

    private void processAccelerationData() {
        for (int i = 0; i < rawAcc.length; ++i) {
            filteredAcc[i] = lowPassFilter(ALPHA, filteredAcc[i], rawAcc[i]);
        }
    }

    private void processMagneticData() {
        for (int i = 0; i < rawMag.length; ++i) {
            filteredMag[i] = lowPassFilter(ALPHA, filteredMag[i], rawMag[i]);
        }
    }

    private void calculateDeviceOrientation(float[] acc, float[] mag) {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, acc, mag)) {
            SensorManager.getOrientation(rotationMatrix, orientationData);
            bearing = getNormalizedAzimuth(orientationData[0]);
        }
    }

    @Override
    public void register(Observer observer) {
        sensorManager.registerListener(this, accelerometerSensor, OrientationUtils.SENSOR_REFRESH_RATE);
        sensorManager.registerListener(this, magneticSensor, OrientationUtils.SENSOR_REFRESH_RATE);
        observableHelper.register(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observableHelper.unregister(observer);
        sensorManager.unregisterListener(this);
    }
}