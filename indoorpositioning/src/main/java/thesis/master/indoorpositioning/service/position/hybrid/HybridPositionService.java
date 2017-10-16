package thesis.master.indoorpositioning.service.position.hybrid;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.ObservableHelper;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.PositionService;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.MovementDetector;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.model.Movement;
import thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator.InertialPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.common.SignalBasedPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.SignalScanner;


public class HybridPositionService<T extends SignalScanResult> implements PositionService {
    private final ObservableHelper observableHelper;
    private final MovementDetector movementDetector;
    private final InertialPositionCalculator inertialPositionCalculator;
    private final SignalScanner<T> signalScanner;
    private final SignalBasedPositionCalculator<T> signalBasedPositionCalculator;

    private Position currentPosition;
    private Position previousSignalPosition;
    private Disposable disposable;

    public HybridPositionService(
            MovementDetector movementDetector,
            InertialPositionCalculator inertialPositionCalculator,
            SignalScanner<T> signalScanner,
            SignalBasedPositionCalculator<T> signalBasedPositionCalculator
    ) {
        this.observableHelper = new ObservableHelper();
        this.movementDetector = movementDetector;
        this.inertialPositionCalculator = inertialPositionCalculator;
        this.signalScanner = signalScanner;
        this.signalBasedPositionCalculator = signalBasedPositionCalculator;
    }

    @Override
    public void onResume() {
        disposable = signalScanner.getSignalScanResults()
                .filter(list -> !list.isEmpty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleSignalScanUpdate);

        movementDetector.onResume();
        movementDetector.register(this);
    }

    @Override
    public void onPause() {
        movementDetector.unregister(this);
        movementDetector.onPause();

        disposable.dispose();
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(Position position) {
        this.currentPosition = position;
    }

    @Override
    public void register(Observer observer) {
        observableHelper.register(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observableHelper.unregister(observer);
    }

    @Override
    public void update(Observable observable) {
        handleSensorUpdate();
    }

    private void handleSensorUpdate() {
        Movement step = movementDetector.getDetectedMovement();
        if (currentPosition != null) {
            currentPosition = inertialPositionCalculator.calculatePosition(currentPosition, step.getLength(), step.getBearing());
            observableHelper.notifyObservers(this);
        }
    }

    private void handleSignalScanUpdate(List<T> signalScanResults) {
        Position probablePosition = signalBasedPositionCalculator.calculatePosition(signalScanResults);
        if (probablePosition == null) {
            return;
        }

        if (!probablePosition.equals(previousSignalPosition) || currentPosition == null) {
            currentPosition = probablePosition;
            previousSignalPosition = probablePosition;
            observableHelper.notifyObservers(this);
        }
    }
}
