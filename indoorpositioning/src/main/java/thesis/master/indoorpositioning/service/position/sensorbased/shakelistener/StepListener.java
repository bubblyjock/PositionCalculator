package thesis.master.indoorpositioning.service.position.sensorbased.shakelistener;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import thesis.master.indoorpositioning.service.common.observer.ObservableHelper;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationUtils;

import static thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationUtils.getAccelerationMagnitude;
import static thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationUtils.lowPassFilter;

public class StepListener implements ShakeListener {

    private static final float FILTER_ALPHA = 0.1f;
    private final float acceleratorAmplitudeThreshold;

    private final SensorManager sensorManager;
    private final Sensor accelerometerSensor;
    private final ObservableHelper observableHelper = new ObservableHelper();

    private final float[] filteredAcc = new float[3];
    private float[] rawAcc = new float[3];
    private float magnitude;
    private float localMin = 0.0f;
    private float localMax = 0.0f;
    private int directionChange = 0;
    private float lastMagnitude = 0.0f;

    public StepListener(Context context, Float acceleratorAmplitudeThreshold) {
        this.acceleratorAmplitudeThreshold = acceleratorAmplitudeThreshold;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            rawAcc = event.values.clone();

            processAccelerationData(event);

            tryToDetectStep();
        }
    }

    private void processAccelerationData(SensorEvent event) {
        for (int i = 0; i < event.values.length; i++) {
            filteredAcc[i] = lowPassFilter(FILTER_ALPHA, filteredAcc[i], rawAcc[i]);
        }

        magnitude = getAccelerationMagnitude(filteredAcc);
    }

    private void tryToDetectStep() {
        float sign = Math.signum(magnitude) + Math.signum(lastMagnitude);
        if (sign == 0 || sign == 1) {
            directionChange++;
        }
        if (directionChange == 2) {
            directionChange = 0;
            if (localMax - localMin > acceleratorAmplitudeThreshold) {
                onMovementDetected();
            }
            localMin = 0;
            localMax = 0;
        }

        if (magnitude < 0) {
            if (magnitude < localMin) {
                localMin = magnitude;
            }
        } else {
            if (magnitude > localMax) {
                localMax = magnitude;
            }
        }
        lastMagnitude = magnitude;
    }

    private void onMovementDetected() {
        observableHelper.notifyObservers(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void register(Observer observer) {
        sensorManager.registerListener(this, accelerometerSensor, OrientationUtils.SENSOR_REFRESH_RATE);
        observableHelper.register(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observableHelper.unregister(observer);
        sensorManager.unregisterListener(this);
    }
}
