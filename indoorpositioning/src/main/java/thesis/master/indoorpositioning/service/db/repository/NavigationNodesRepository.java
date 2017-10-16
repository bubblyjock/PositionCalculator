package thesis.master.indoorpositioning.service.db.repository;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import thesis.master.indoorpositioning.model.NavigationNode;
import java8.util.function.Consumer;

public class NavigationNodesRepository implements Repository<String, NavigationNode> {

    @Override
    public void insert(NavigationNode navigationNode) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.insert(navigationNode);
            realm.commitTransaction();
        }
    }

    @Override
    public void insert(List<NavigationNode> navigationNodes) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.insert(navigationNodes);
            realm.commitTransaction();
        }
    }

    @Override
    public void operateOn(String navigationNodeId, Consumer<NavigationNode> consumer) {
        try (Realm realm = Realm.getDefaultInstance()) {
            NavigationNode navigationNode = realm.where(NavigationNode.class).equalTo("id", navigationNodeId).findFirst();
            consumer.accept(navigationNode);
        }
    }

    @Override
    public void operateOnAll(Consumer<RealmResults<NavigationNode>> consumer) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<NavigationNode> navigationNodes = realm.where(NavigationNode.class).findAll();
            consumer.accept(navigationNodes);
        }
    }

    @Override
    public NavigationNode get(String navigationNodeId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            NavigationNode navigationNode = realm.where(NavigationNode.class).equalTo("id", navigationNodeId).findFirst();
            return realm.copyFromRealm(navigationNode);
        }
    }

    @Override
    public NavigationNode getAny() {
        try (Realm realm = Realm.getDefaultInstance()) {
            NavigationNode navigationNode = realm.where(NavigationNode.class).findFirst();
            return navigationNode == null ? null : realm.copyFromRealm(navigationNode);
        }
    }

    @Override
    public List<NavigationNode> getAll() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<NavigationNode> navigationNodes = realm.where(NavigationNode.class).findAll();
            return realm.copyFromRealm(navigationNodes);
        }
    }


    @Override
    public void remove(String navigationNodeId) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.where(NavigationNode.class).equalTo("id", navigationNodeId).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

    @Override
    public void removeAll() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.delete(NavigationNode.class);
            realm.commitTransaction();
        }
    }

}
