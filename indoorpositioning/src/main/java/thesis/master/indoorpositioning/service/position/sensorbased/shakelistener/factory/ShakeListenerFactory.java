package thesis.master.indoorpositioning.service.position.sensorbased.shakelistener.factory;

import android.content.Context;

import thesis.master.indoorpositioning.service.position.sensorbased.shakelistener.ShakeListener;
import thesis.master.indoorpositioning.service.position.sensorbased.shakelistener.StepListener;

public class ShakeListenerFactory {

    public static ShakeListener stepListener(Context context) {
        Float acceleratorAmplitudeThreshold = 0.8f;
        return new StepListener(context, acceleratorAmplitudeThreshold);
    }
}