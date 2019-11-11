package com.example.marius.helpmesee.directions.model;

import static java.lang.Math.PI;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.example.marius.helpmesee.app_logic.Constants;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

/**
 * Geographic Coordiante Sysyem Point, used for implementing the logic for Craig Reynolds path following algorithm
 */
public class GCSPoint {

  /**
   * Constant representing the fact that predictedFutureLocation is on the right side of a certain segment
   * on the navigation path.
   */
  public static final int RIGHT = -1;
  public static final int LEFT = 1;

  private double latitude;
  private double longitude;

  public GCSPoint(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
   * 0  on the line
   * -1f - right
   * +1f left
   */
  public static int detectPointSide(GCSPoint x, GCSPoint start, GCSPoint end) {
    Point3D x2d = Point3D.convertTo2d(x);
    Point3D start2d = Point3D.convertTo2d(start);
    Point3D end2d = Point3D.convertTo2d(end);

    double determinant =
        (end2d.x - start2d.x) * (x2d.y - start2d.y) - (end2d.y - start2d.y) * (x2d.x - start2d.x);

    return (int) Math.signum(determinant);
  }

  public static int detectPointSide(double xLat, double yLat, GCSPoint start, GCSPoint end) {
    Point3D start2d = Point3D.convertTo2d(start);
    Point3D end2d = Point3D.convertTo2d(end);
    Point3D x2d = Point3D.convertTo2d(xLat, yLat);

    double determinant =
        (end2d.x - start2d.x) * (x2d.y - start2d.y) - (end2d.y - start2d.y) * (x2d.x - start2d.x);

    return (int) Math.signum(determinant);
  }

  /**
   * Returns the distance between two GCS points
   *
   * @return distance between the two LatLong points in meters
   */
  public static double distanceBetweenPoints(GCSPoint start,
      GCSPoint end) {
    double startLat = Math.toRadians(start.getLatitude());
    double endLat = Math.toRadians(end.getLatitude());
    double deltaLat = Math.toRadians((end.getLatitude() - start.getLatitude()));
    double deltaLong = Math.toRadians(end.getLongitude() - start.getLongitude());

    double temp = (Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
        Math.cos(startLat) * Math.cos(endLat) *
            Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2));

    double c = (2 * Math.atan2(Math.sqrt(temp), Math.sqrt(1 - temp)));

    return Constants.EARTH_RADIUS_M * c; //distance in meters
  }

  public static double distanceBetweenPoints(double startLat, double startLon, double endLat,
      double endLon) {
    double startLatRad = Math.toRadians(startLat);
    double endLatRad = Math.toRadians(endLat);
    double deltaLat = Math.toRadians((endLat - startLat));
    double deltaLong = Math.toRadians(endLon - startLon);

    double temp = (Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
        Math.cos(startLatRad) * Math.cos(endLatRad) *
            Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2));

    double c = (2 * Math.atan2(Math.sqrt(temp), Math.sqrt(1 - temp)));

    return Constants.EARTH_RADIUS_M * c; //distance in meters
  }


  /**
   * Returns the shortest distance from point x to the great circle described by start-end
   * Also known as cross track error
   */
  public static double computeShortestDistanceToCirclePath(GCSPoint x, GCSPoint start,
      GCSPoint end) {
    double distance = 0;

    double d13 = distanceBetweenPoints(start, x);
    double angularDistance = d13 / Constants.EARTH_RADIUS_M;

    double brg13Radians = Math.toDegrees(computeBearingSegment(start, x));
    double brg12Radians = Math.toDegrees(computeBearingSegment(start, end));

    distance = Math.asin(sin(angularDistance) * sin(brg13Radians - brg12Radians))
        * Constants.EARTH_RADIUS_M;
    return Math.abs(distance);
  }

  /**
   * A perpendicular is drawn from third point (x) to the (great circle) path, the along-track
   * distance is the distance from the start point to where the perpendicular crosses the path.
   * The distance is negative if the third point is "before" the start point
   */
  public static double computeAlongTrackDistance(GCSPoint x, GCSPoint start,
      GCSPoint end) {
    double d13 = distanceBetweenPoints(start, x);
    double angularDistance = d13 / Constants.EARTH_RADIUS_M;

    double brg13Radians = Math.toRadians(computeBearingSegment(start, x));
    double brg12Radians = Math.toRadians(computeBearingSegment(start, end));

    double deltaX = Math.asin(Math.sin(angularDistance) * Math.sin(brg13Radians - brg12Radians));
    double deltaT = Math.acos(Math.cos(angularDistance) / Math.abs(Math.cos(deltaX)));

    return (deltaT * Math.signum(Math.cos(brg12Radians - brg13Radians))
        * Constants.EARTH_RADIUS_M);
  }


  /**
   * Returns null if there is no  normal point on this segment
   */
  public static GCSPoint isNormalPointOnSegment(GCSPoint x, GCSPoint start, GCSPoint end) {
    double alongTrackDistance = computeAlongTrackDistance(x, start, end);

    //the x point is before line segment
    if (alongTrackDistance < 0) {
      return null;
    }

    double bearingDegree = computeBearingSegment(start, end);
    GCSPoint normalPoint = computeDestinatioinPoint(start, alongTrackDistance, bearingDegree);

    double d1 = distanceBetweenPoints(start, normalPoint);
    double d2 = distanceBetweenPoints(start, end);

    if (d1 > d2) { //normal point is not on segment
      return null;
    }

    return normalPoint;
  }

  public static GCSPoint predictFutureLocation(LatLng startPoint, Float bearing, Float speedMph,
      Float deltaT) {
    double bearingRadians = Math.toRadians(bearing);
    double x = speedMph * Math.sin(bearingRadians) * deltaT / 3600;
    double y = speedMph * Math.cos(bearingRadians) * deltaT / 3600;

    double yDegrees = Math.toDegrees(y);
    double endLat = startPoint.latitude + yDegrees / Constants.EARTH_RADIUS_MILES;

    double startLatRadians = Math.toRadians(startPoint.latitude);
    double endLong = startPoint.longitude
        + 180 / Math.PI / Math.sin(startLatRadians) * x
        / Constants.EARTH_RADIUS_MILES;

    return new GCSPoint(endLat, endLong);
  }


  /**
   * Compute bearing given a segment
   */
  public static double computeBearingSegment(GCSPoint start, GCSPoint end) {

//Source
    double lat1 = start.getLatitude();
    double lng1 = start.getLongitude();

// destination
    double lat2 = end.getLatitude();
    double lng2 = end.getLongitude();

    double fLat = Math.toRadians(lat1);
    double fLong = Math.toRadians(lng1);
    double tLat = Math.toRadians(lat2);
    double tLong = Math.toRadians(lng2);

    double dLon = (tLong - fLong);

    double degree = Math.toDegrees(Math.atan2(sin(dLon) * cos(tLat),
        cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)));

    if (degree >= 0) {
      return degree;
    } else {
      return (360 + degree);
    }
  }

  /**
   * Given a start point, initial bearing, and distance, this will calculate the destina­tion point
   */
  public static GCSPoint computeDestinatioinPoint(GCSPoint startPos, double distanceInMetres,
      double bearingDegrees) {
    double brngRad = Math.toRadians(bearingDegrees);
    double latRad = Math.toRadians(startPos.getLatitude());
    double lonRad = Math.toRadians(startPos.getLongitude());
    double distFrac = distanceInMetres / Constants.EARTH_RADIUS_M;

    double destLatRad = asin(
        sin(latRad) * cos(distFrac) + cos(latRad) * sin(distFrac) * cos(brngRad));
    double temp = atan2(sin(brngRad) * sin(distFrac) * cos(latRad),
        cos(distFrac) - sin(latRad) * sin(destLatRad));
    double destLonRad = ((lonRad + temp + 3 * PI) % (2 * PI) - PI);

    double destLat = Math.toDegrees(destLatRad);
    double destLon = Math.toDegrees(destLonRad);
    return new GCSPoint(destLat, destLon);
  }

  @Override
  public String toString() {
    return new StringBuilder("(").append(latitude).append(",").append(longitude).append(")")
        .toString();
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof GCSPoint)) {
      return false;
    }

    GCSPoint o = (GCSPoint) other;
    return ((o.getLatitude() == latitude) &&
        (o.getLongitude() == longitude));
  }

  /*
     Returns the angle in degrees between segment p1p2 and p2p3
  */
  public static int angleBetween(GCSPoint p1, GCSPoint p2, GCSPoint p3) {
    Point3D p1_2D = Point3D.convertTo2d(p1);
    Point3D p2_2D = Point3D.convertTo2d(p2);
    Point3D p3_2D = Point3D.convertTo2d(p3);

    Point3D seg21 = Point3D.substract(p1_2D, p2_2D);
    Point3D seg23 = Point3D.substract(p3_2D, p2_2D);

    double mag21 = seg21.magnitude();
    double mag23 = seg23.magnitude();

    double dotProductResult = seg21.dotProduct(seg23);

    double result = dotProductResult / (mag21 * mag23);
    double angleRadians = Math.acos(result);
    return (int) Math.toDegrees(angleRadians);
  }

}
