package thesis.master.indoorpositioning.service.db.repository;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import thesis.master.indoorpositioning.model.Transmitter;
import java8.util.function.Consumer;

public class TransmittersRepository implements Repository<String, Transmitter> {

    @Override
    public void insert(Transmitter transmitter) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.insert(transmitter);
            realm.commitTransaction();
        }
    }

    @Override
    public void insert(List<Transmitter> transmitters) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.insert(transmitters);
            realm.commitTransaction();
        }
    }

    @Override
    public void operateOn(String transmitterId, Consumer<Transmitter> consumer) {
        try (Realm realm = Realm.getDefaultInstance()) {
            Transmitter transmitter = realm.where(Transmitter.class).equalTo("transmitterId", transmitterId).findFirst();
            consumer.accept(transmitter);
        }
    }

    @Override
    public void operateOnAll(Consumer<RealmResults<Transmitter>> consumer) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Transmitter> transmitters = realm.where(Transmitter.class).findAll();
            consumer.accept(transmitters);
        }
    }

    @Override
    public Transmitter get(String transmitterId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            Transmitter transmitter = realm.where(Transmitter.class).equalTo("transmitterId", transmitterId).findFirst();
            return realm.copyFromRealm(transmitter);
        }
    }

    @Override
    public Transmitter getAny() {
        try (Realm realm = Realm.getDefaultInstance()) {
            Transmitter transmitter = realm.where(Transmitter.class).findFirst();
            return transmitter == null ? null : realm.copyFromRealm(transmitter);
        }
    }

    @Override
    public List<Transmitter> getAll() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<Transmitter> transmitters = realm.where(Transmitter.class).findAll();
            return realm.copyFromRealm(transmitters);
        }
    }

    @Override
    public void remove(String transmitterId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.where(Transmitter.class).equalTo("transmitterId", transmitterId).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    @Override
    public void removeAll() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.delete(Transmitter.class);
            realm.commitTransaction();
        }
    }

}
