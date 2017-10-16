package thesis.master.indoorpositioning.service.db.importer;

import java8.util.function.Supplier;

public interface FileImporter {

    FileImporter from(String fileName);

    FileImporter onlyWhen(Supplier<Boolean> conditionSupplier);

    FileImporter clearBeforeWhen(Supplier<Boolean> conditionSupplier);

    void importIntoDb();

}
