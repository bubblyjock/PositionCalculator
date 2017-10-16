package thesis.master.service.classifier;

import jsat.ARFFLoader;
import jsat.DataSet;
import jsat.classifiers.CategoricalResults;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseVector;
import lombok.extern.slf4j.Slf4j;
import thesis.master.model.arff.ARFFDetails;
import thesis.master.model.core.Measurement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractJSATClassifier implements Classifier {

    private ARFFDetails arffDetails;

    @Override
    public void train(ARFFDetails arffDetails) {
        log.info("Training classifier started.");
        this.arffDetails = arffDetails;
        DataSet trainDataSet = ARFFLoader.loadArffFile(arffDetails.getArffFile());
        //We specify '0' as the class we would like to make the target class.
        ClassificationDataSet classificationDataSet = new ClassificationDataSet(trainDataSet, 0);
        getClassifier().trainC(classificationDataSet);
        log.info("Training classifier finished.");
    }

    @Override
    public String classify(List<Measurement> measurements) {
        log.info("Classifying measurements {}", measurements);
        DataPoint dataPoint = convertToDataPoint(measurements);
        CategoricalResults result = getClassifier().classify(dataPoint);
        return arffDetails.getClassNames().get(result.mostLikely());
    }

    private DataPoint convertToDataPoint(List<Measurement> measurements) {
        log.info("{}", measurements);
        Map<String, Integer> measurementsMap = measurements.stream().collect(Collectors.toMap(Measurement::getTransmitterId, Measurement::getRssi));
        List<Double> rssiVector = arffDetails.getAttributeNames().stream()
                .map(attributeName -> {
                    Integer rssiValue = measurementsMap.get(attributeName);
                    return rssiValue == null ? Double.NaN : rssiValue.doubleValue();
                })
                .collect(Collectors.toList());
        return new DataPoint(new DenseVector(rssiVector));
    }

    abstract jsat.classifiers.Classifier getClassifier();
}
