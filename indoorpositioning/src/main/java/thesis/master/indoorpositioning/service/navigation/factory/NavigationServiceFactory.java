package thesis.master.indoorpositioning.service.navigation.factory;


import thesis.master.indoorpositioning.service.navigation.DijkstraNavigationService;
import thesis.master.indoorpositioning.service.navigation.NavigationService;

public class NavigationServiceFactory {

    public static NavigationService dijkstra() {
        return new DijkstraNavigationService();
    }

}
