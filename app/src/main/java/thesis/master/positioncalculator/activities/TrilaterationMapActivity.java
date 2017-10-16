package thesis.master.positioncalculator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thesis.master.indoorpositioning.service.map.leaflet.position.LeafletPositionService;
import thesis.master.indoorpositioning.service.map.leaflet.position.factory.LeafletPositionServiceFactory;

public class TrilaterationMapActivity extends AppCompatActivity {

    private LeafletPositionService leafletPositionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.leafletPositionService = LeafletPositionServiceFactory.trilateration(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        leafletPositionService.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        leafletPositionService.onPause();
    }

}
