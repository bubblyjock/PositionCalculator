package thesis.master.indoorpositioning.service.map.leaflet.navigation;


import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.util.List;
import java.util.Locale;

import thesis.master.indoorpositioning.R;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.lifecycle.Pausable;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.common.permission.PermissionManager;
import thesis.master.indoorpositioning.service.navigation.NavigationService;
import thesis.master.indoorpositioning.service.position.PositionService;

public class LeafletNavigationService implements Observer, Pausable {

    private final NavigationService navigationService;
    private final PositionService positionService;

    private final WebView webView;
    private final Gson gson;

    public LeafletNavigationService(Activity activity, NavigationService navigationService, PositionService positionService) {
        PermissionManager.getInstance(activity).request(PermissionManager.LOCATION_PERMISSIONS);
        this.navigationService = navigationService;
        this.positionService = positionService;

        this.gson = new GsonBuilder().create();
        activity.setContentView(R.layout.activity_leaflet_map_navigation);
        webView = (WebView) activity.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(this, "android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.loadUrl("file:///android_asset/www/map_navigation.html");

        FloatingActionButton calculatePathButton = (FloatingActionButton) activity.findViewById(R.id.calculatePathButton);
        calculatePathButton.setOnClickListener(view -> calculatePath());

        FloatingActionButton clearPathButton = (FloatingActionButton) activity.findViewById(R.id.clearPathButton);
        clearPathButton.setOnClickListener(view -> clearPath());
    }

    private void calculatePath() {
        webView.loadUrl("javascript:android.onData(getStartPosition(), getEndPosition())");
    }

    private void clearPath() {
        webView.post(() -> webView.loadUrl("javascript:clearPath()"));
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
        webView.loadUrl(String.format(Locale.getDefault(), "javascript:updateLocation(%f, %f, %d)", lat, lng, level));
    }

    @JavascriptInterface
    public void onData(String startPositionJson, String endPositionJson) throws JSONException {
        Position startPosition = gson.fromJson(startPositionJson, Position.class);
        Position endPosition = gson.fromJson(endPositionJson, Position.class);

        final List<Position> path = navigationService.getPath(startPosition, endPosition);

        webView.post(() -> webView.loadUrl(String.format("javascript:drawPath('%s')", gson.toJson(path))));
    }
}
