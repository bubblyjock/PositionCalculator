package thesis.master.indoorpositioning.service.position.sensorbased;


import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.ObservableHelper;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.PositionService;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.MovementDetector;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.model.Movement;
import thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator.InertialPositionCalculator;

public class SensorBasedPositionService implements PositionService {

    private final ObservableHelper observableHelper;
    private final MovementDetector movementDetector;
    private final InertialPositionCalculator inertialPositionCalculator;

    private Position currentPosition;

    public SensorBasedPositionService(MovementDetector movementDetector, InertialPositionCalculator inertialPositionCalculator){
        this.observableHelper = new ObservableHelper();
        this.movementDetector = movementDetector;
        this.inertialPositionCalculator = inertialPositionCalculator;
    }

    @Override
    public void onResume() {
        movementDetector.onResume();
        movementDetector.register(this);
    }

    @Override
    public void onPause() {
        movementDetector.unregister(this);
        movementDetector.onPause();
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
    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(Position position) {
        this.currentPosition = position;
    }

    @Override
    public void update(Observable observable) {
        Movement movement = movementDetector.getDetectedMovement();
        if (currentPosition != null) {
            currentPosition = inertialPositionCalculator.calculatePosition(currentPosition, movement.getLength(), movement.getBearing());
            observableHelper.notifyObservers(this);
        }
    }

}
