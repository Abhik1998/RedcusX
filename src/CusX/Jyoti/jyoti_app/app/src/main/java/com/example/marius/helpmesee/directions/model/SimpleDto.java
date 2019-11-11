package com.example.marius.helpmesee.directions.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class SimpleDto {

  private LatLng currentUserLocation;
  public GCSPoint predictedFutureLocation;
  public GCSPoint normalPoint;
  public GCSPoint target;
  public GCSPoint segmentStart;
  public GCSPoint segmentEnd;
  public Instruction instruction;

  public SimpleDto(LatLng currentUserLocation,
      GCSPoint predictedFutureLocation,
      GCSPoint normalPoint,
      GCSPoint target,
      GCSPoint segmentStart,
      GCSPoint segmentEnd,
      Instruction instruction) {
    this.currentUserLocation = currentUserLocation;
    this.predictedFutureLocation = predictedFutureLocation;
    this.normalPoint = normalPoint;
    this.target = target;
    this.segmentStart = segmentStart;
    this.segmentEnd = segmentEnd;
    this.instruction = instruction;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();

    buffer.append("UserLoc: ").append("(").append(currentUserLocation.latitude).append(",").append(currentUserLocation.longitude).append(")").append("\n");
    buffer.append("PredictedFutureLoc: ").append(predictedFutureLocation.getLatitude()).append(",").append(predictedFutureLocation.getLongitude()).append("\n");
    buffer.append("NormalPoint: ").append(normalPoint.getLatitude()).append(",").append(normalPoint.getLongitude()).append("\n");
    buffer.append("Target: ").append(target.getLatitude()).append(",").append(target.getLongitude()).append("\n");
    buffer.append("SegmentStart: ").append(segmentStart.getLatitude()).append(",").append(segmentStart.getLongitude()).append("\n");
    buffer.append("SegmentEnd: ").append(segmentEnd.getLatitude()).append(",").append(segmentEnd.getLongitude()).append("\n");
    buffer.append("Instruction: ").append(instruction.toString());

    return buffer.toString();
  }
}
