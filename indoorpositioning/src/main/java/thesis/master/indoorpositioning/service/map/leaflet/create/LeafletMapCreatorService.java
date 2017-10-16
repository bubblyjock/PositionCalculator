package thesis.master.indoorpositioning.service.map.leaflet.create;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.RealmList;
import java8.util.function.Functions;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import thesis.master.indoorpositioning.R;
import thesis.master.indoorpositioning.model.NavigationNode;
import thesis.master.indoorpositioning.model.Pattern;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.permission.PermissionManager;
import thesis.master.indoorpositioning.service.common.utils.NavigationNodeTrimmer;
import thesis.master.indoorpositioning.service.db.exporter.FileExporter;
import thesis.master.indoorpositioning.service.db.exporter.RealmJsonExporter;
import thesis.master.indoorpositioning.service.db.importer.RealmJsonImporterFactory;
import thesis.master.indoorpositioning.service.db.repository.NavigationNodesRepository;

public class LeafletMapCreatorService {

    private final WebView webView;
    private final AppCompatActivity activity;
    private final Gson gson;
    private final NavigationNodeTrimmer navigationNodeTrimmer;
    private final NavigationNodesRepository navigationNodesRepository;
    private Map<Position, NavigationNode> savedNavigationNodes;

    public LeafletMapCreatorService(AppCompatActivity activity) {
        PermissionManager.getInstance(activity).request(PermissionManager.STORAGE_PERMISSIONS);
        this.activity = activity;
        this.gson = new GsonBuilder().create();
        this.navigationNodeTrimmer = new NavigationNodeTrimmer();
        this.navigationNodesRepository = new NavigationNodesRepository();
        this.savedNavigationNodes = StreamSupport.stream(navigationNodesRepository.getAll())
                .collect(Collectors.toMap(NavigationNode::getPosition, Functions.identity()));
        activity.setContentView(R.layout.activity_leaflet_map_creator);
        webView = (WebView) activity.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(this, "android");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.post(() -> webView.loadUrl(String.format("javascript:loadNavigationNodes('%s')", gson.toJson(navigationNodeTrimmer.trimNeighbourNodes(new ArrayList<>(savedNavigationNodes.values()))))));
            }
        });
        webView.loadUrl("file:///android_asset/www/map_creator.html");

        FloatingActionButton saveMapButton = (FloatingActionButton) activity.findViewById(R.id.saveMapButton);
        saveMapButton.setOnClickListener(view -> saveCreatedMap());
    }

    private void saveCreatedMap() {
        webView.loadUrl("javascript:android.onData(getCreatedMap())");
    }

    @JavascriptInterface
    public void onData(String jsonMap) throws JSONException {
        HashMap<Position, NavigationNode> nodes = new HashMap<>();
        JSONArray connections = new JSONArray(jsonMap);
        for (int i = 0; i < connections.length(); ++i) {
            JSONObject connection = connections.getJSONObject(i);
            addConnection(nodes, connection.getJSONObject("firstNode"), connection.getJSONObject("secondNode"));
        }
        FileExporter<NavigationNode> navigationNodesExporter = new RealmJsonExporter<>(activity);
        navigationNodesExporter.export(navigationNodeTrimmer.trimNeighbourNodes(new ArrayList<>(nodes.values())), "navigationNodes");
        RealmJsonImporterFactory.importNavigationNodes(activity)
                .from("navigationNodes.json")
                .clearBeforeWhen(() -> true)
                .importIntoDb();
        savedNavigationNodes = StreamSupport.stream(navigationNodesRepository.getAll())
                .collect(Collectors.toMap(NavigationNode::getPosition, Functions.identity()));
    }

    private void addConnection(HashMap<Position, NavigationNode> nodes, JSONObject firstNode, JSONObject secondNode) throws JSONException {
        Position firstNodePosition = createPositionFromJSONNode(firstNode);
        Position secondNodePosition = createPositionFromJSONNode(secondNode);

        NavigationNode firstNavigationNode = nodes.get(firstNodePosition);
        NavigationNode secondNavigationNode = nodes.get(secondNodePosition);
        if (firstNavigationNode == null) {
            firstNavigationNode = createNavigationNode(firstNodePosition);
        }
        if (secondNavigationNode == null) {
            secondNavigationNode = createNavigationNode(secondNodePosition);
        }
        firstNavigationNode.getNeighbourNodes().add(secondNavigationNode);
        secondNavigationNode.getNeighbourNodes().add(firstNavigationNode);
        nodes.put(firstNodePosition, firstNavigationNode);
        nodes.put(secondNodePosition, secondNavigationNode);
    }

    private Position createPositionFromJSONNode(JSONObject node) throws JSONException {
        Position nodePosition = new Position();
        nodePosition.setLongitude(node.getDouble("longitude"));
        nodePosition.setLatitude(node.getDouble("latitude"));
        nodePosition.setHeight(node.getDouble("height"));
        return nodePosition;
    }

    private NavigationNode createNavigationNode(Position nodePosition) {
        NavigationNode navigationNode = new NavigationNode();
        navigationNode.setId(UUID.randomUUID().toString());
        navigationNode.setPosition(nodePosition);
        navigationNode.setPatterns(getPatterns(nodePosition));
        navigationNode.setNeighbourNodes(new RealmList<>());
        return navigationNode;
    }

    private RealmList<Pattern> getPatterns(Position position) {
        NavigationNode navigationNode = savedNavigationNodes.get(position);
        if (navigationNode == null) {
            return new RealmList<>();
        }
        return navigationNode.getPatterns();
    }

}
