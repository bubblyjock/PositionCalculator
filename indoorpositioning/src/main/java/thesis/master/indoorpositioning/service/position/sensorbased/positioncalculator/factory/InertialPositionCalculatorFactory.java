package thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator.factory;


import thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator.DestinationInertialPositionCalculator;
import thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator.InertialPositionCalculator;

public class InertialPositionCalculatorFactory {

    public static InertialPositionCalculator destination() {
        return new DestinationInertialPositionCalculator();
    }

}
