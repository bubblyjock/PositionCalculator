package thesis.master.indoorpositioning.service.navigation;

import java.util.List;

import thesis.master.indoorpositioning.model.Position;

public interface NavigationService {

    List<Position> getPath(Position startPosition, Position endPosition);

}
