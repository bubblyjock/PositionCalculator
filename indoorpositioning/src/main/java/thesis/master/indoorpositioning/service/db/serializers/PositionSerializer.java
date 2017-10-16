package thesis.master.indoorpositioning.service.db.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import thesis.master.indoorpositioning.model.Position;

public class PositionSerializer implements JsonSerializer<Position> {

    @Override
    public JsonElement serialize(Position src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("longitude", src.getLongitude());
        jsonObject.addProperty("latitude", src.getLatitude());
        jsonObject.addProperty("height", src.getHeight());
        return jsonObject;
    }
}
