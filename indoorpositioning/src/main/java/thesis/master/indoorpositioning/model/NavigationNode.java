package thesis.master.indoorpositioning.model;

import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NavigationNode extends RealmObject {

    @PrimaryKey
    private String id;

    private Position position;

    private RealmList<NavigationNode> neighbourNodes;

    private RealmList<Pattern> patterns;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public RealmList<NavigationNode> getNeighbourNodes() {
        return neighbourNodes;
    }

    public void setNeighbourNodes(RealmList<NavigationNode> neighbourNodes) {
        this.neighbourNodes = neighbourNodes;
    }

    public RealmList<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(RealmList<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NavigationNode that = (NavigationNode) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
