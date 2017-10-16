package thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance.factory;


import thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance.FreePathDistanceCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.trilateration.calculator.distance.WiFiDistanceCalculator;

public class WiFiDistanceCalculatorFactory {

    public static WiFiDistanceCalculator freePath() {
        return new FreePathDistanceCalculator();
    }

}
