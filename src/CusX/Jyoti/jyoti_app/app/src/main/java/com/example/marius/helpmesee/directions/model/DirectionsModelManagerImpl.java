package com.example.marius.helpmesee.directions.model;

import android.location.Location;
import com.example.marius.helpmesee.directions.model.InstructionProviderTask.InstructionConsumer;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

/*
  Implements the algorithm
 */
public class DirectionsModelManagerImpl implements DirectionsModelManager, InstructionConsumer {

  private DirectionsModelListener directionsModelListener;

  private List<GCSPoint> pathCoordinates;
  /**
   * If we think of the path as a road, then pathRadius determines the road's width,i.e. 2*pathRadius
   */
  private Float pathRadius;

  //used to predict user future location after a certain time given a speed
  private Float deltaT;


  public DirectionsModelManagerImpl() {
    //default values
    deltaT = 5f; //seconds
    pathRadius = 3f; //meters
  }


  /**
   * trigger method for returning a instruction to the user based on his/her location
   */
  @Override
  public void fetchInstruction(Location newLocation) {
//    Float bearing;
//    Float speed = 0f;

    if (newLocation.hasSpeed() && newLocation.hasBearing()) {
      LatLng currentLoc = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

      float speed = newLocation.getSpeed();
      float bearing = newLocation.getBearing();
      new InstructionProviderTask(this)
          .execute(currentLoc, speed, bearing, pathRadius, pathCoordinates, deltaT);
    }

//    if (newLocation.hasSpeed()) {
//      speed = newLocation.getSpeed();
//      previousSpeed = speed;
//    } else {
//      //average walking speed is 1.4 m/s
//      speed = 1.4f;
//    }
//
//    speed = toMph(speed);
//
//    LatLng startPoint = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
//
//    if (newLocation.hasBearing()) {
//      bearing = newLocation.getBearing();
//      /**
//       * Don't provide any instruction if the user is not moving
//       */
//      new InstructionProviderTask(this)
//          .execute(startPoint, speed, bearing, pathRadius, pathCoordinates, deltaT);
//
//      //update
//      previousBearing = bearing;
//    } else {
//      bearing = previousBearing;
//    }

  }

  @Override
  public void setModelListener(DirectionsModelListener listener) {
    directionsModelListener = listener;
  }

  /**
   * called after the path to a certain destination has been found
   *
   * @param currentPathCoordinates - LatLng coordintes from current location to destination
   */
  @Override
  public void initialize(List<LatLng> currentPathCoordinates, Float radius, Float deltaT,
      Float currentPhoneBearing) {
    this.pathCoordinates = toListOfGCSPoints(currentPathCoordinates);
    this.pathRadius = radius;
    this.deltaT = deltaT;

  }


  /**
   * We need this conversion since the path following algorithm uses a list of GCSPoint not of LatLng
   */
  private List<GCSPoint> toListOfGCSPoints(List<LatLng> coordinatesLatLng) {
    List<GCSPoint> pathCoordiantesEV = new ArrayList<>();
    for (LatLng latLng : coordinatesLatLng) {
      pathCoordiantesEV.add(new GCSPoint(latLng.latitude, latLng.longitude));
    }

    return pathCoordiantesEV;
  }

  /**
   * Converts from meters/second to miles/hour
   */
  private Float toMph(Float speed) {
    return speed * (0.000621371f / 0.000277778f);
  }

  @Override
  public void instructionResult(Instruction instruction) {
    directionsModelListener.onInstrFetched(instruction);
  }

}
