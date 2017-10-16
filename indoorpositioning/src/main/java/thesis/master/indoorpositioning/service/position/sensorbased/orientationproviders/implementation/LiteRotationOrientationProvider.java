package thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation;

import android.content.Context;
import android.util.Log;

public class LiteRotationOrientationProvider extends RotationOrientationProvider {
    //TODO: not implemented yet
    //TODO: differ from rotation with gyro - treat it like GeoRotationProvider

    public LiteRotationOrientationProvider(Context context) {
        super(context);
//        sensorManager.registerListener(this, rotationSensor, OrientationUtils.SENSOR_REFRESH_RATE);
        Log.e("LiteRotationVector", "NOT IMPLEMENTED!");
    }
}