package com.example.marius.helpmesee.directions.model;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.util.Log;
import com.example.marius.helpmesee.app_logic.Constants;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
//Helper class for GcsPoint
public class Point3D {

  public double x;
  public double y;
  public double z;

  public Point3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public static Point3D convertTo2d(GCSPoint gcsPoint) {
    double latRadians = Math.toRadians(gcsPoint.getLatitude());
    double longRadians = Math.toRadians(gcsPoint.getLongitude());
    double x = ((Constants.EARTH_RADIUS_KM * cos(
        latRadians)) * cos(
        longRadians));
    double y = (Constants.EARTH_RADIUS_KM * cos(
        latRadians) * sin(
        longRadians));
    double z = (Constants.EARTH_RADIUS_KM * sin(latRadians));

    return new Point3D(x, y, z);
  }

  public static Point3D convertTo2d(double xLat, double yLat) {
    double latRadians = Math.toRadians(xLat);
    double longRadians = Math.toRadians(yLat);
    double x = ((Constants.EARTH_RADIUS_KM * cos(
        latRadians)) * cos(
        longRadians));
    double y = (Constants.EARTH_RADIUS_KM * cos(
        latRadians) * sin(
        longRadians));
    double z = (Constants.EARTH_RADIUS_KM * sin(latRadians));

    return new Point3D(x, y, z);
  }
  public static GCSPoint convertToGCS(Point3D point) {
    float lat = (float) asin(point.z / Constants.EARTH_RADIUS_KM);
    float lon = (float) atan2(point.y, point.x);

    return new GCSPoint(Math.toDegrees(lat),
        Math.toDegrees(lon));
  }

  public static GCSPoint convertToGCS(double x, double y, double z) {
    double lat = asin(z / Constants.EARTH_RADIUS_KM);
    double lon = atan2(y, z);

    return new GCSPoint(Math.toDegrees(lat),
        Math.toDegrees(lon));
  }

  public void substract(Point3D other) {
    x -= other.x;
    y -= other.y;
    z -= other.z;
  }

  public static Point3D substract(Point3D p1, Point3D p2) {
    Point3D p3 = new Point3D(0, 0, 0);
    p3.x = p1.x - p2.x;
    p3.y = p1.y - p2.y;
    p3.z = p1.z - p2.z;

    return p3;
  }

  public double magnitude() {
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
  }

  public void normalize() {
    double mag = magnitude();

    if (mag != 0) {
      x /= mag;
      y /= mag;
      z /= mag;
    } else {
      Log.i(Constants.HMS_INFO, "Couldn't normalize 3d vector ");
    }
  }

  public double dotProduct(Point3D other) {
    return x * other.x + y * other.y + z * other.z;
  }


}
