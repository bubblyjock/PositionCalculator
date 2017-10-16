package thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.factory;


import android.content.Context;

import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.MovementDetector;
import thesis.master.indoorpositioning.service.position.sensorbased.movementdetect.StepDetector;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.factory.OrientationProviderFactory;
import thesis.master.indoorpositioning.service.position.sensorbased.shakelistener.factory.ShakeListenerFactory;

public class MovementDetectorFactory {

    public static MovementDetector stepDetector(Context context) {
        final float stepLength = 0.7f;
        return new StepDetector(
                context,
                stepLength,
                ShakeListenerFactory.stepListener(context),
                OrientationProviderFactory.bestAvailable(context));
    }
}
