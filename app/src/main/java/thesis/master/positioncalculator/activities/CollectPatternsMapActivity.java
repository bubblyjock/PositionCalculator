package thesis.master.positioncalculator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thesis.master.indoorpositioning.service.map.leaflet.position.pattern.collector.LeafletPatternCollectorService;

public class CollectPatternsMapActivity extends AppCompatActivity {

    private LeafletPatternCollectorService leafletPatternCollectorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.leafletPatternCollectorService = new LeafletPatternCollectorService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        leafletPatternCollectorService.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        leafletPatternCollectorService.onPause();
    }
}