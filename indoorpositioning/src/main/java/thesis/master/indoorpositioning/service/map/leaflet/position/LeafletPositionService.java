package thesis.master.indoorpositioning.service.map.leaflet.position;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import thesis.master.indoorpositioning.R;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.lifecycle.Pausable;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.common.permission.PermissionManager;
import thesis.master.indoorpositioning.service.position.PositionService;
import thesis.master.indoorpositioning.service.position.sensorbased.SensorBasedPositionService;

public class LeafletPositionService implements Observer, Pausable {
    private final PositionService positionService;

    private final WebView webView;

    public LeafletPositionService(Activity activity, PositionService positionService) {
        PermissionManager.getInstance(activity).request(PermissionManager.LOCATION_PERMISSIONS);
        this.positionService = positionService;

        activity.setContentView(R.layout.activity_leaflet);
        webView = (WebView) activity.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(this, "android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.loadUrl("file:///android_asset/www/map.html");

        FloatingActionButton updateLocationButton = (FloatingActionButton) activity.findViewById(R.id.updateLocationButton);
        updateLocationButton.setOnClickListener(view -> setMapCenterAsCurrentLocation());
        if (positionService instanceof SensorBasedPositionService) {
            updateLocationButton.setVisibility(View.VISIBLE);
        } else {
            updateLocationButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        positionService.onResume();
        positionService.register(this);
    }

    @Override
    public void onPause() {
        positionService.unregister(this);
        positionService.onPause();
    }

    @Override
    public void update(Observable observable) {
        Position position = positionService.getCurrentPosition();
        updateLocation(position.getLatitude(), position.getLongitude(), (int) position.getHeight());
    }

    private void updateLocation(double lat, double lng, int level) {
        String parameter = "javascript:updateLocation(" + lat + ", " + lng + ", " + level + ")";
        webView.loadUrl(parameter);
    }

    private void setMapCenterAsCurrentLocation() {
        webView.loadUrl("javascript:android.onData(setLocationOnMapCenter())");

    }

    @JavascriptInterface
    public void onData(String data) {
        String[] array = data.split(",");

        if (array.length == 3) {
            Position position = new Position();
            position.setLatitude(Double.parseDouble(array[0]));
            position.setLongitude(Double.parseDouble(array[1]));
            position.setHeight(Double.parseDouble(array[2]));
            positionService.setCurrentPosition(position);
        }
    }

}
