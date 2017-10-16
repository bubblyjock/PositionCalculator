package thesis.master.indoorpositioning.service.db.importer;


import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.exceptions.RealmException;
import java8.util.function.Supplier;
import thesis.master.indoorpositioning.service.exception.ImportIntoDbException;

import static thesis.master.indoorpositioning.service.db.importer.FileUtils.getFirstNonNull;
import static thesis.master.indoorpositioning.service.db.importer.FileUtils.importFromAssets;
import static thesis.master.indoorpositioning.service.db.importer.FileUtils.importFromExternalStorage;
import static thesis.master.indoorpositioning.service.db.importer.FileUtils.importFromInternalStorage;

public class RealmJsonImporter<T extends RealmModel> implements FileImporter {

    private final List<Supplier<Boolean>> importConditions = new ArrayList<>();
    private final List<Supplier<Boolean>> clearConditions = new ArrayList<>();
    private final Class<T> classType;
    private final Context context;
    private String fileName;

    RealmJsonImporter(Class<T> classType, Context context) {
        this.classType = classType;
        this.context = context;
    }

    @Override
    public FileImporter from(String fileName) {
        assert this.fileName == null;
        this.fileName = fileName;
        return this;
    }

    @Override
    public FileImporter onlyWhen(Supplier<Boolean> conditionSupplier) {
        this.importConditions.add(conditionSupplier);
        return this;
    }

    @Override
    public FileImporter clearBeforeWhen(Supplier<Boolean> conditionSupplier) {
        clearConditions.add(conditionSupplier);
        return this;
    }

    @Override
    public void importIntoDb() {
        assert StringUtils.isNotBlank(fileName);

        if (clearConditions.size() > 0 && allConditionsMet(clearConditions)) {
            clearBeforeImport();
        }

        if (!allConditionsMet(importConditions)) {
            return;
        }

        InputStream stream = getFirstNonNull(
                importFromAssets(fileName, context),
                importFromInternalStorage(fileName, context),
                importFromExternalStorage(fileName, context)
        );
        if (stream == null) {
            throw new ImportIntoDbException("Could not find file for " + classType.getName());
        }
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(classType, stream);
            realm.commitTransaction();
        } catch (RealmException e) {
            e.printStackTrace();
            throw new ImportIntoDbException("Error while importing " + classType.getName());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean allConditionsMet(List<Supplier<Boolean>> conditions) {
        for (Supplier<Boolean> condition : conditions) {
            if (!condition.get()) {
                return false;
            }
        }
        return true;
    }

    private void clearBeforeImport() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.delete(classType);
            realm.commitTransaction();
        }
    }

}
