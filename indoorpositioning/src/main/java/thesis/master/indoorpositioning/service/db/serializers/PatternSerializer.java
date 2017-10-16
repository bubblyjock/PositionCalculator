package thesis.master.indoorpositioning.service.db.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import thesis.master.indoorpositioning.model.Pattern;

public class PatternSerializer implements JsonSerializer<Pattern> {

    @Override
    public JsonElement serialize(Pattern src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("measurements", context.serialize(src.getMeasurements()));
        return jsonObject;
    }

}
