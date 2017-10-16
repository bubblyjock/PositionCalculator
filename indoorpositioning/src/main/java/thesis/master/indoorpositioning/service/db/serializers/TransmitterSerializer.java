package thesis.master.indoorpositioning.service.db.serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import thesis.master.indoorpositioning.model.Transmitter;

public class TransmitterSerializer implements JsonSerializer<Transmitter> {

    @Override
    public JsonElement serialize(Transmitter src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("transmitterId", src.getTransmitterId());
        jsonObject.add("position", context.serialize(src.getPosition()));
        return jsonObject;
    }

}
