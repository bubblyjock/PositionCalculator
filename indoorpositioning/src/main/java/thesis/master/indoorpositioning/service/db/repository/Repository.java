package thesis.master.indoorpositioning.service.db.repository;

import java.util.List;

import io.realm.RealmModel;
import io.realm.RealmResults;
import java8.util.function.Consumer;

interface Repository<I, T extends RealmModel> {

    void insert(T model);

    void insert(List<T> models);

    void operateOn(I id, Consumer<T> consumer);

    void operateOnAll(Consumer<RealmResults<T>> consumer);

    T get(I id);

    T getAny();

    List<T> getAll();

    void remove(I id);

    void removeAll();

}
