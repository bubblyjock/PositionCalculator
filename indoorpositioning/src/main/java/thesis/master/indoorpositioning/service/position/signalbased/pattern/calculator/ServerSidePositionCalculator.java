package thesis.master.indoorpositioning.service.position.signalbased.pattern.calculator;

import java.util.List;
import java.util.Map;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import thesis.master.indoorpositioning.model.Measurement;
import thesis.master.indoorpositioning.model.NavigationNode;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.db.repository.NavigationNodesRepository;
import thesis.master.indoorpositioning.service.position.signalbased.common.SignalBasedPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;

public class ServerSidePositionCalculator implements SignalBasedPositionCalculator<SignalScanResult> {

    private ServerSidePositionService serverSidePositionService;
    private Map<String, Position> navigationNodesPositions;
    private Position currentPosition;
    private StopWatch stopWatch;
    private Long pollDelay;

    public ServerSidePositionCalculator(String url, Long pollDelay) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.serverSidePositionService = retrofit.create(ServerSidePositionService.class);

        NavigationNodesRepository navigationNodesRepository = new NavigationNodesRepository();
        this.navigationNodesPositions = StreamSupport.stream(navigationNodesRepository.getAll()).collect(Collectors.toMap(NavigationNode::getId, NavigationNode::getPosition));

        this.currentPosition = new Position();

        this.stopWatch = new StopWatch();

        this.pollDelay = pollDelay;
    }

    @Override
    public Position calculatePosition(List<SignalScanResult> scanResults) {
        if (stopWatch.getElapsedTime() < pollDelay) {
            return currentPosition;
        }
        stopWatch.restart();
        List<Measurement> measurements = StreamSupport.stream(scanResults)
                .map(this::createMeasurement)
                .collect(Collectors.toList());

        serverSidePositionService.getNavigationNodeId(measurements).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() == null) {
                    return;
                }
                Position position2D = navigationNodesPositions.get(response.body());
                if (position2D == null) {
                    return;
                }
                currentPosition = new Position(position2D.getLatitude(), position2D.getLongitude(), currentPosition.getHeight());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });

        serverSidePositionService.getLevel(measurements).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.body() == null) {
                    return;
                }
                currentPosition = new Position(currentPosition.getLatitude(), currentPosition.getLongitude(), response.body());
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return currentPosition;
    }

    private Measurement createMeasurement(SignalScanResult signalScanResult) {
        Measurement measurement = new Measurement();
        measurement.setTransmitterId(signalScanResult.getTransmitterId());
        measurement.setRssi(signalScanResult.getRssi());
        return measurement;
    }

    private class StopWatch {
        private long startTime = System.currentTimeMillis();

        void restart() {
            startTime = System.currentTimeMillis();
        }

        long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
    }
}
