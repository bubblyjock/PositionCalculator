package thesis.master.indoorpositioning.service.db.exporter;


import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import io.realm.RealmObject;
import thesis.master.indoorpositioning.service.db.serializers.MeasurementSerializer;
import thesis.master.indoorpositioning.service.db.serializers.NavigationNodeSerializer;
import thesis.master.indoorpositioning.service.db.serializers.PatternSerializer;
import thesis.master.indoorpositioning.service.db.serializers.PositionSerializer;
import thesis.master.indoorpositioning.service.db.serializers.TransmitterSerializer;
import thesis.master.indoorpositioning.service.exception.ExportInfoFileException;

public class RealmJsonExporter<T> implements FileExporter<T> {

    private final Context context;

    public RealmJsonExporter(Context context) {
        this.context = context;
    }

    @Override
    public void export(List<T> objectsToBeExported, String fileName) {
        Gson gson = initGson();
        File jsonFile = new File(context.getExternalFilesDir(null), fileName + ".json");
        String jsonString = gson.toJson(objectsToBeExported);
        try {
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExportInfoFileException("Error while exporting into " + fileName);
        }
    }

    private Gson initGson() {
        try {
            return new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .registerTypeAdapter(Class.forName("io.realm.TransmitterRealmProxy"), new TransmitterSerializer())
                    .registerTypeAdapter(Class.forName("io.realm.PositionRealmProxy"), new PositionSerializer())
                    .registerTypeAdapter(Class.forName("io.realm.PatternRealmProxy"), new PatternSerializer())
                    .registerTypeAdapter(Class.forName("io.realm.MeasurementRealmProxy"), new MeasurementSerializer())
                    .registerTypeAdapter(Class.forName("io.realm.NavigationNodeRealmProxy"), new NavigationNodeSerializer())
                    .create();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Gson();
    }
}
