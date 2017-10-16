package thesis.master.indoorpositioning.service.db.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import thesis.master.indoorpositioning.model.Measurement;

public class MeasurementSerializer implements JsonSerializer<Measurement> {

    @Override
    public JsonElement serialize(Measurement src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("transmitterId", src.getTransmitterId());
        jsonObject.addProperty("rssi", src.getRssi());
        return jsonObject;
    }

}
