package thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import thesis.master.indoorpositioning.model.Measurement;

public interface ServerSidePositionService {

    @POST("/classifier/navigationNodeId")
    Call<String> getNavigationNodeId(@Body List<Measurement> measurements);

    @POST("/classifier/level")
    Call<Double> getLevel(@Body List<Measurement> measurements);

}
