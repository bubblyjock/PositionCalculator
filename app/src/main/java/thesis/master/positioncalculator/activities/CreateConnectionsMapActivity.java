package thesis.master.positioncalculator.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thesis.master.indoorpositioning.service.map.leaflet.create.LeafletMapCreatorService;

public class CreateConnectionsMapActivity extends AppCompatActivity {

    private LeafletMapCreatorService leafletMapCreatorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.leafletMapCreatorService = new LeafletMapCreatorService(this);
    }
}
