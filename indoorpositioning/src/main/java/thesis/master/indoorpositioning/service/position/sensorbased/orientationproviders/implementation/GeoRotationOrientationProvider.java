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

public class GeoRotationOrientationProvider implements OrientationProvider {

    private final ObservableHelper observableHelper = new ObservableHelper();
    private final SensorManager sensorManager;
    private final Sensor geoRotationSensor;
    private final float[] rotationMatrix = new float[16];
    private final float[] orientationData = new float[3];
    private float[] rawGeoRot = new float[3];
    private float bearing;

    public GeoRotationOrientationProvider(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        geoRotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            rawGeoRot = event.values.clone();
            processRotationData();
            observableHelper.notifyObservers(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public float getOrientation() {
        return bearing;
    }

    private void processRotationData() {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rawGeoRot);
        SensorManager.getOrientation(rotationMatrix, orientationData);
        bearing = getNormalizedAzimuth(orientationData[0]);
    }

    @Override
    public void register(Observer observer) {
        sensorManager.registerListener(this, geoRotationSensor, OrientationUtils.SENSOR_REFRESH_RATE);
        observableHelper.register(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observableHelper.unregister(observer);
        sensorManager.unregisterListener(this);
    }
}