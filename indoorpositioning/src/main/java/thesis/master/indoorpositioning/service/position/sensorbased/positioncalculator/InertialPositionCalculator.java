package thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator;

import thesis.master.indoorpositioning.model.Position;

public interface InertialPositionCalculator {

    Position calculatePosition(final Position initialPosition, final double distance, final double bearing);
}
