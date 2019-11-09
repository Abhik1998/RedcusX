package com.example.marius.helpmesee.directions.model;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
//DTO object - part of the model
public class PathDto {

  public String origin;
  public String destination;
  public LatLng originLatLng;
  public LatLng destinationLatLng;
  public float distanceKM; //in km
  public float timeM; //in minutes

  public List<LatLng> coordinatesLatLng;

  public PathDto(String origin, String destination,
      LatLng originLatLng, LatLng destinationLatLng, float distanceKM, float timeM,
      List<LatLng> coordinatesLatLng) {
    this.origin = origin;
    this.destination = destination;
    this.originLatLng = originLatLng;
    this.destinationLatLng = destinationLatLng;
    this.distanceKM = distanceKM;
    this.timeM = timeM;
    this.coordinatesLatLng = coordinatesLatLng;
  }


}
