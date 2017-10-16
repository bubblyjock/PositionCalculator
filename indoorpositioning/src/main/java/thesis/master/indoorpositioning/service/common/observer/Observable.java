package thesis.master.indoorpositioning.service.common.observer;

public interface Observable {

    void register(Observer observer);

    void unregister(Observer observer);

}
