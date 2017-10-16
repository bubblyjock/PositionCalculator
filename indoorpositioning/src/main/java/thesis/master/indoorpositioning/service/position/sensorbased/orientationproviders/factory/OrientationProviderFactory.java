package thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.factory;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.OrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation.AccMagOrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation.ComplementaryOrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation.GeoRotationOrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation.LiteRotationOrientationProvider;
import thesis.master.indoorpositioning.service.position.sensorbased.orientationproviders.implementation.RotationOrientationProvider;

public class OrientationProviderFactory {

    public static OrientationProvider bestAvailable(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor geoRotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

        if (accelerometerSensor == null || magneticSensor == null) {
            return null;
        }

        if (gyroscopeSensor != null && rotationSensor != null) {
//             1. BEST OPTION: Android Rotation Vector + filtering
            return OrientationProviderFactory.rotation(context);
        } else if (gyroscopeSensor != null) {
//             2. COMPLEMENTARY FILTER: gyro + acc + mag
            return OrientationProviderFactory.complementary(context);
        } else if (geoRotationSensor != null) {
//             3. GeoRotation + filtering
            return OrientationProviderFactory.geoRotation(context);
        } else if (rotationSensor != null) {
//            3a. Rotation Vector without gyro - same as 3. geoRot
            return OrientationProviderFactory.liteRotation(context);
            // TODO: implement proper OrientationProvider - differ from rotation with gyro
        } else {
//            only acc + mag left :(
            return OrientationProviderFactory.accMag(context);
        }
    }

    public static OrientationProvider rotation(Context context) {
        return new RotationOrientationProvider(context);
    }

    public static OrientationProvider complementary(Context context) {
        return new ComplementaryOrientationProvider(context);
    }

    public static OrientationProvider geoRotation(Context context) {
        return new GeoRotationOrientationProvider(context);
    }

    public static OrientationProvider liteRotation(Context context) {
        return new LiteRotationOrientationProvider(context);
    }

    public static OrientationProvider accMag(Context context) {
        return new AccMagOrientationProvider(context);
    }
}
