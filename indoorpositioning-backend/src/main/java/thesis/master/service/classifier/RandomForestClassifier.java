package thesis.master.service.classifier;

import jsat.classifiers.Classifier;
import jsat.classifiers.trees.DecisionTree;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class RandomForestClassifier extends AbstractJSATClassifier {

    private Classifier classifier;

    public RandomForestClassifier() {
        this.classifier = new DecisionTree();
    }

    @Override
    Classifier getClassifier() {
        return classifier;
    }
}
