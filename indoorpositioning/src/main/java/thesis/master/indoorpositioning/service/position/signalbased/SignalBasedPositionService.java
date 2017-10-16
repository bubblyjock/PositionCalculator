package thesis.master.indoorpositioning.service.position.signalbased;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import thesis.master.indoorpositioning.model.Position;
import thesis.master.indoorpositioning.service.common.observer.Observable;
import thesis.master.indoorpositioning.service.common.observer.ObservableHelper;
import thesis.master.indoorpositioning.service.common.observer.Observer;
import thesis.master.indoorpositioning.service.position.PositionService;
import thesis.master.indoorpositioning.service.position.signalbased.common.SignalBasedPositionCalculator;
import thesis.master.indoorpositioning.service.position.signalbased.converter.model.SignalScanResult;
import thesis.master.indoorpositioning.service.position.signalbased.signalscanner.SignalScanner;

public class SignalBasedPositionService<T extends SignalScanResult> implements PositionService {

    private final ObservableHelper observableHelper;
    private final SignalScanner<T> signalScanner;
    private final SignalBasedPositionCalculator<T> signalBasedPositionCalculator;

    private Disposable disposable;
    private Position currentPosition;

    public SignalBasedPositionService(SignalScanner<T> signalScanner, SignalBasedPositionCalculator<T> signalBasedPositionCalculator) {
        this.observableHelper = new ObservableHelper();
        this.signalScanner = signalScanner;
        this.signalBasedPositionCalculator = signalBasedPositionCalculator;
    }

    @Override
    public void onResume() {
        disposable = signalScanner.getSignalScanResults()
                .filter(list -> !list.isEmpty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scanResults -> {
                    currentPosition = signalBasedPositionCalculator.calculatePosition(scanResults);
                    notifyObservers();
                });
    }

    @Override
    public void onPause() {
        disposable.dispose();
    }

    @Override
    public void update(Observable observable) {
       //TODO remove
    }

    @Override
    public void register(Observer observer) {
        observableHelper.register(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observableHelper.unregister(observer);
    }

    private void notifyObservers() {
        observableHelper.notifyObservers(this);
    }

    @Override
    public Position getCurrentPosition() {
        //TODO return reference to copy of current position
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(Position position) {
        this.currentPosition = position;
    }

}
