package thesis.master.indoorpositioning.service.map.leaflet.position.factory;


import android.app.Activity;

import thesis.master.indoorpositioning.service.map.leaflet.position.LeafletPositionService;
import thesis.master.indoorpositioning.service.position.factory.PositionServiceFactory;

public class LeafletPositionServiceFactory {

    public static LeafletPositionService hybrid(Activity activity) {
        return new LeafletPositionService(
                activity,
                PositionServiceFactory.hybrid(activity)
        );
    }

    public static LeafletPositionService offline(Activity activity) {
        return new LeafletPositionService(
                activity,
                PositionServiceFactory.offlineSignalBased(activity)
        );
    }

    public static LeafletPositionService online(Activity activity, String url) {
        return new LeafletPositionService(
                activity,
                PositionServiceFactory.onlineSignalBased(activity, url)
        );
    }

    public static LeafletPositionService sensors(Activity activity) {
        return new LeafletPositionService(
                activity,
                PositionServiceFactory.sensors(activity)
        );
    }

    public static LeafletPositionService trilateration(Activity activity) {
        return new LeafletPositionService(
                activity,
                PositionServiceFactory.trilateration(activity)
        );
    }

}
