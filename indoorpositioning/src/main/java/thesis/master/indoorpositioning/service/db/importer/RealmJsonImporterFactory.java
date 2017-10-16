package thesis.master.indoorpositioning.service.db.importer;


import android.content.Context;

import thesis.master.indoorpositioning.model.NavigationNode;
import thesis.master.indoorpositioning.model.Transmitter;

public class RealmJsonImporterFactory {

    public static RealmJsonImporter<Transmitter> importTransmitters(Context context) {
        return new RealmJsonImporter<>(Transmitter.class, context);
    }

    public static RealmJsonImporter<NavigationNode> importNavigationNodes(Context context) {
        return new RealmJsonImporter<>(NavigationNode.class, context);
    }

}
