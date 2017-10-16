package thesis.master.indoorpositioning.service.db.exporter;

import java.util.List;

public interface FileExporter<T> {

    void export(List<T> objectsToBeExported, String fileName);

}
