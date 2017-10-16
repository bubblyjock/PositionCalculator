package thesis.master.positioncalculator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import thesis.master.positioncalculator.R;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    //TODO Maybe use lombok to reduce boilerplate code?
    //TODO Write tests to existing code
    //TODO Merge BLE
    //TODO Maybe Kalman filter for sensors?
    //TODO Javadoc for library
    //TODO Add simple navigation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onHybridPositionMapClick(View view) {
        startActivity(new Intent(this, HybridPositionMapActivity.class));
    }

    public void onCollectPatternsMapClick(View view) {
        startActivity(new Intent(this, CollectPatternsMapActivity.class));
    }

    public void onSensorPositionButton(View view) {
        startActivity(new Intent(this, SensorsMapActivity.class));
    }

    public void onTrilaterationPositionButton(View view) {
        startActivity(new Intent(this, TrilaterationMapActivity.class));
    }

    public void onPatternPositionButton(View view) {
        startActivity(new Intent(this, PatternMapActivity.class));
    }

    public void onConnectionsMapButton(View view) {
        startActivity(new Intent(this, CreateConnectionsMapActivity.class));
    }

    public void onNavigationMapButton(View view) {
        startActivity(new Intent(this, NavigationMapActivity.class));
    }
}