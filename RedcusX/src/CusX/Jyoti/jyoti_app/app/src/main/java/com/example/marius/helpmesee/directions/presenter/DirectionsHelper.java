package com.example.marius.helpmesee.directions.presenter;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import com.example.marius.helpmesee.app_logic.Constants;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;

/**
 * Standalone methods that provide functionality
 */
public class DirectionsHelper {


  /**
   * Converts minutes into Xh-Ym-Zs format
   */
  public static String prettyReadDuration(float timeM) {
    StringBuilder duration = new StringBuilder();

    int hours = (int) (timeM / 60);
    if (hours != 0) {
      duration.append(hours).append("h");
      timeM -= hours * 60;
    }

    int minutes = (int) timeM;

    if (minutes != 0) {
      duration.append(minutes).append("m");
      timeM -= minutes;
    }

    int seconds = (int) (timeM * 60);

    if (seconds != 0) {
      duration.append(seconds).append("s");
    }

    return duration.toString();
  }

  private static double degreeToRadians(double latLong) {
    return (Math.PI * latLong / 180.0);
  }

  private static double radiansToDegree(double latLong) {
    return (latLong * 180.0 / Math.PI);
  }

  public static double getBearing(LatLng start, LatLng end) {

//Source
    double lat1 = start.latitude;
    double lng1 = start.longitude;

// destination
    double lat2 = end.latitude;
    double lng2 = end.longitude;

    double fLat = degreeToRadians(lat1);
    double fLong = degreeToRadians(lng1);
    double tLat = degreeToRadians(lat2);
    double tLong = degreeToRadians(lng2);

    double dLon = (tLong - fLong);

    double degree = radiansToDegree(Math.atan2(sin(dLon) * cos(tLat),
        cos(fLat) * sin(tLat) - sin(fLat) * cos(tLat) * cos(dLon)));

    if (degree >= 0) {
      return degree;
    } else {
      return 360 + degree;
    }
  }

}
