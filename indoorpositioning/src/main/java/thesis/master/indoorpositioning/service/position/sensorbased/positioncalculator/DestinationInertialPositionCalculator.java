package thesis.master.indoorpositioning.service.position.sensorbased.positioncalculator;

import thesis.master.indoorpositioning.model.Position;

public class DestinationInertialPositionCalculator implements InertialPositionCalculator {

    private static final double EARTH_RADIUS = 6371000;

    // Calculation algorithm from http://www.movable-type.co.uk/scripts/latlong.html#destPoint
    @Override
    public Position calculatePosition(Position initialPosition, double distance, double bearing) {
        double d = distance / EARTH_RADIUS;
        double b = Math.toRadians(bearing);

        double lat1 = Math.toRadians(initialPosition.getLatitude());
        double lng1 = Math.toRadians(initialPosition.getLongitude());

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d)
                + Math.cos(lat1) * Math.sin(d) * Math.cos(b));

        double lng2 = lng1 + Math.atan2(Math.sin(b) * Math.sin(d) * Math.cos(lat1),
                Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));

        Position destinationPosition = new Position();
        destinationPosition.setLatitude(Math.toDegrees(lat2));
        destinationPosition.setLongitude((Math.toDegrees(lng2) + 540) % 360 - 180);
        destinationPosition.setHeight(initialPosition.getHeight());

        return destinationPosition;
    }
}
