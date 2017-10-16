package thesis.master.service.classification;

import org.springframework.stereotype.Service;
import thesis.master.model.arff.ARFFDetails;
import thesis.master.model.core.Measurement;
import thesis.master.service.arff.ARFFCreator;
import thesis.master.service.classifier.Classifier;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class LevelClassifierService {

    @Resource
    private ARFFCreator arffCreator;

    @Resource
    private Classifier classifier;

    @PostConstruct
    public void init() throws IOException {
        ARFFDetails arffDetails = arffCreator.levelAsClass();
        classifier.train(arffDetails);
    }

    public Double classify(List<Measurement> measurements) {
        return Double.parseDouble(classifier.classify(measurements));
    }

}
