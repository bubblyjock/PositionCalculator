package thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders;

import android.hardware.SensorEventListener;

import thesis.master.indoorpositioning.service.common.observer.Observable;

public interface OrientationProvider extends SensorEventListener, Observable {

    float getOrientation();
}