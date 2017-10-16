package thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance.factory;


import thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance.DistanceCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator.distance.EuclideanDistanceCalculator;

public class DistanceCalculatorFactory {

    public static DistanceCalculator euclidean() {
        return new EuclideanDistanceCalculator();
    }

}
