package thesis.master.indoorpositioning.service.position;

import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.lifecycle.Pausable;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.Observer;

public interface PositionService extends Observer, Observable, Pausable {

    Position getCurrentPosition();

    void setCurrentPosition(Position position);

}
