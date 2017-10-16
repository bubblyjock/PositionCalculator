package thesis.master.indoorpositioning.service.position.sensorbased.movementdetect;

import android.content.Context;
import android.widget.Toast;

import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.ObservableHelper;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.model.Movement;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.shakelistener.ShakeListener;

public class StepDetector implements MovementDetector {

    private final ObservableHelper observableHelper;
    private final Context context;
    private final float stepLength;
    private ShakeListener shakeListener;
    private OrientationProvider orientationProvider;

    private float bearing = 0.0f;

    public StepDetector(Context context, Float stepLength, ShakeListener shakeListener, OrientationProvider orientationProvider) {
        this.observableHelper = new ObservableHelper();
        this.context = context;
        this.stepLength = stepLength;
        this.shakeListener = shakeListener;
        this.orientationProvider = orientationProvider;
    }

    @Override
    public void onResume() {
        shakeListener.register(this);
        orientationProvider.register(this);
        Toast.makeText(context, orientationProvider.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        shakeListener.unregister(this);
        orientationProvider.unregister(this);
    }

    @Override
    public void update(Observable observable) {
        if (observable instanceof OrientationProvider) {
            final OrientationProvider orientationProvider = (OrientationProvider) observable;
            onOrientationChanged(orientationProvider.getOrientation());
        } else if (observable instanceof ShakeListener) {
            onMovementDetected();
        }
    }

    private void onOrientationChanged(float orientation) {
        bearing = orientation;
    }

    private void onMovementDetected() {
        observableHelper.notifyObservers(this);
    }

    @Override
    public Movement getDetectedMovement() {
        return new Movement(stepLength, bearing);
    }

    @Override
    public void register(Observer observer) {
        observableHelper.register(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observableHelper.unregister(observer);
    }
}