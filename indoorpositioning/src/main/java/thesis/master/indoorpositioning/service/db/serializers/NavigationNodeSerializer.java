package thesis.master.indoorpositioning.service.db.serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import thesis.master.indoorpositioning.model.NavigationNode;

public class NavigationNodeSerializer implements JsonSerializer<NavigationNode> {
    @Override
    public JsonElement serialize(NavigationNode src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.add("position", context.serialize(src.getPosition()));
        jsonObject.add("neighbourNodes", context.serialize(src.getNeighbourNodes()));
        jsonObject.add("patterns", context.serialize(src.getPatterns()));
        return jsonObject;
    }
}
