package thesis.master.indoorpositioning.service.common.observer;

import java.util.HashSet;
import java.util.Set;

public class ObservableHelper {

    private final Set<Observer> observers = new HashSet<>();

    public void register(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException();
        }
        observers.add(observer);
    }

    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Observable observable) {
        for (Observer observer : observers) {
            observer.update(observable);
        }
    }
}
