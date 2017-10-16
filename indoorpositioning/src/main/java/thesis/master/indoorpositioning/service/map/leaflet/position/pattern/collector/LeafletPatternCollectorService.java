package thesis.master.indoorpositioning.service.map.leaflet.position.pattern.collector;


import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.realm.RealmList;
import java8.util.function.Functions;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.R;
import thesis.master.indoorpositioning.model.NavigationNode;
import thesis.master.indoorpositioning.model.Pattern;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.lifecycle.Pausable;
import thesis.master.indoorpositioning.service.common.permission.PermissionManager;
import thesis.master.indoorpositioning.service.common.utils.NavigationNodeTrimmer;
import thesis.master.indoorpositioning.service.db.exporter.FileExporter;
import thesis.master.indoorpositioning.service.db.exporter.RealmJsonExporter;
import thesis.master.indoorpositioning.service.db.importer.RealmJsonImporterFactory;
import thesis.master.indoorpositioning.service.db.repository.NavigationNodesRepository;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.collector.RssiPatternCollector;
import thesis.master.indoorpositioning.service.position.signalbased.pattern.collector.factory.RssiPatternCollectorFactory;

public class LeafletPatternCollectorService implements Pausable {
    private final AppCompatActivity activity;
    private final RssiPatternCollector patternCollector;

    private final WebView webView;
    private final Gson gson;
    private final Button collectPatternButton;
    private final Button saveToFileButton;
    private Disposable disposable;

    private boolean isCollectStarted;
    private Position currentPosition;
    private Map<Position, RealmList<Pattern>> positionsWithPatterns;
    private Map<Position, NavigationNode> positionsWithNavigationNode;

    public LeafletPatternCollectorService(AppCompatActivity activity) {
        PermissionManager.getInstance(activity).request(PermissionManager.LOCATION_PERMISSIONS, PermissionManager.STORAGE_PERMISSIONS);
        this.activity = activity;
        this.patternCollector = RssiPatternCollectorFactory.wifiPatternCollector(activity);
        gson = new GsonBuilder().create();
        activity.setContentView(R.layout.activity_collect_patterns_map);

        webView = (WebView) this.activity.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                NavigationNodesRepository navigationNodesRepository = new NavigationNodesRepository();
                List<Position> savedPositions = StreamSupport.stream(navigationNodesRepository.getAll())
                        .map(NavigationNode::getPosition)
                        .collect(Collectors.toList());
                webView.post(() -> webView.loadUrl(String.format("javascript:loadSavedPositions('%s')", gson.toJson(savedPositions))));
            }
        });
        webView.loadUrl("file:///android_asset/www/map.html");

        collectPatternButton = (Button) this.activity.findViewById(R.id.collectPatternButtonMap);
        collectPatternButton.setOnClickListener(view -> collectPattern());

        saveToFileButton = (Button) this.activity.findViewById(R.id.saveButtonMap);
        saveToFileButton.setOnClickListener(view -> {
            savePatternsToFile();
        });

        isCollectStarted = false;
        currentPosition = null;
        positionsWithPatterns = new HashMap<>();
        NavigationNodesRepository navigationNodesRepository = new NavigationNodesRepository();
        this.positionsWithNavigationNode = StreamSupport.stream(navigationNodesRepository.getAll())
                .collect(Collectors.toMap(NavigationNode::getPosition, Functions.identity()));
    }

    @Override
    public void onResume() {
        disposable = patternCollector.getPatternsObservable()
                .subscribe(pattern -> {
                    if (isCollectStarted) {
                        enablePatternButtons();
                        savePattern(pattern);
                        isCollectStarted = false;
                    }
                });
    }

    private void savePattern(Pattern pattern) {
        if (pattern.getMeasurements().isEmpty()) {
            return;
        }
        List<Pattern> patterns = positionsWithPatterns.get(currentPosition);
        if (patterns == null) {
            positionsWithPatterns.put(currentPosition, new RealmList<>(pattern));
        } else {
            patterns.add(pattern);
        }
    }

    @Override
    public void onPause() {
        disposable.dispose();
    }

    private void collectPattern() {
        webView.post(() -> webView.loadUrl("javascript:android.collectPattern(getCurrentPosition())"));
    }

    @JavascriptInterface
    public void collectPattern(String positionJson) {
        Position position = gson.fromJson(positionJson, Position.class);
        if (position != null) {
            currentPosition = position;
            disablePatternButtons();
            isCollectStarted = true;
        } else {
            Toast.makeText(activity, "First set position!", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePatternsToFile() {
        FileExporter<NavigationNode> realmJsonExporter = new RealmJsonExporter<>(activity);
        realmJsonExporter.export(getCollectedPatterns(), "navigationNodes");
        RealmJsonImporterFactory.importNavigationNodes(activity)
                .from("navigationNodes.json")
                .clearBeforeWhen(() -> true)
                .importIntoDb();
    }

    private List<NavigationNode> getCollectedPatterns() {
        StreamSupport.stream(positionsWithNavigationNode.entrySet()).forEach(entry -> {
            if (entry.getValue().getPatterns() == null) {
                entry.getValue().setPatterns(new RealmList<>());
            }
            RealmList<Pattern> patterns = positionsWithPatterns.get(entry.getKey());
            if (patterns != null) {
                entry.getValue().getPatterns().addAll(patterns);
            }
        });

        positionsWithPatterns.clear();

        NavigationNodeTrimmer navigationNodeTrimmer = new NavigationNodeTrimmer();
        return navigationNodeTrimmer.trimNeighbourNodes(StreamSupport.stream(positionsWithNavigationNode.entrySet())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
    }

    private void disablePatternButtons() {
        activity.runOnUiThread(() -> {
            collectPatternButton.setEnabled(false);
            saveToFileButton.setEnabled(false);
        });
    }

    private void enablePatternButtons() {
        activity.runOnUiThread(() -> {
            collectPatternButton.setEnabled(true);
            saveToFileButton.setEnabled(true);
        });
    }
}
