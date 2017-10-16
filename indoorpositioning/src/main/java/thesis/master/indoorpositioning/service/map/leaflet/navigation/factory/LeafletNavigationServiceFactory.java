package thesis.master.indoorpositioning.service.map.leaflet.navigation.factory;


import android.app.Activity;

import thesis.master.indoorpositioning.service.map.leaflet.navigation.LeafletNavigationService;
import thesis.master.indoorpositioning.service.navigation.factory.NavigationServiceFactory;
import thesis.master.indoorpositioning.service.position.factory.PositionServiceFactory;

public class LeafletNavigationServiceFactory {

    public static LeafletNavigationService defaultLeafletNavigationService(Activity activity) {
        return new LeafletNavigationService(
                activity,
                NavigationServiceFactory.dijkstra(),
                PositionServiceFactory.hybrid(activity));
    }

}
