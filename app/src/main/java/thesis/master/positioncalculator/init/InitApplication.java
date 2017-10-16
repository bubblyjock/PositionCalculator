package thesis.master.positioncalculator.init;

import thesis.master.indoorpositioning.init.LibraryInitializer;
import thesis.master.indoorpositioning.service.db.importer.FileUtils;
import thesis.master.indoorpositioning.service.db.importer.RealmJsonImporterFactory;
import thesis.master.indoorpositioning.service.db.repository.NavigationNodesRepository;
import thesis.master.indoorpositioning.service.db.repository.TransmittersRepository;

public class InitApplication extends LibraryInitializer {

    private static final String TRANSMITTERS_FILE = "transmitters.json";
    private static final String NAVIGATION_NODES_JSON = "navigationNodes.json";

    @Override
    public void onCreate() {
        super.onCreate();

        TransmittersRepository transmittersRepository = new TransmittersRepository();
        NavigationNodesRepository navigationNodesRepository = new NavigationNodesRepository();

        RealmJsonImporterFactory.importTransmitters(this)
                .from(TRANSMITTERS_FILE)
                .onlyWhen(() -> FileUtils.fileExists(TRANSMITTERS_FILE, this))
                .onlyWhen(() -> transmittersRepository.getAny() == null)
                .importIntoDb();

        RealmJsonImporterFactory.importNavigationNodes(this)
                .from(NAVIGATION_NODES_JSON)
                .onlyWhen(() -> FileUtils.fileExists(NAVIGATION_NODES_JSON, this))
                .onlyWhen(() -> navigationNodesRepository.getAny() == null)
                .importIntoDb();

    }
}
