package thesis.master.indoorpositioning.service.position.sensorbased.movementdetect;

import thesis.master.indoorpositioning.service.common.lifecycle.Pausable;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.model.Movement;

public interface MovementDetector extends Observable, Observer, Pausable {

    Movement getDetectedMovement();
}
