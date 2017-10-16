package thesis.master.service.classifier;

import thesis.master.model.arff.ARFFDetails;
import thesis.master.model.core.Measurement;

import java.util.List;

public interface Classifier {
    void train(ARFFDetails arffDetails);
    String classify(List<Measurement> measurements);
}
