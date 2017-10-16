package thesis.master.positioncalculator.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thesis.master.indoorpositioning.service.map.leaflet.navigation.LeafletNavigationService;
import thesis.master.indoorpositioning.service.map.leaflet.navigation.factory.LeafletNavigationServiceFactory;

public class NavigationMapActivity extends AppCompatActivity {

    private LeafletNavigationService leafletNavigationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.leafletNavigationService = LeafletNavigationServiceFactory.defaultLeafletNavigationService(this);
    }

}
