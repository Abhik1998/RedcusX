package com.example.marius.helpmesee.directions.model;

import android.location.Location;
import com.example.marius.helpmesee.app_logic.MvpModel;
import com.example.marius.helpmesee.util.ModelsFactory;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public interface DirectionsModelManager extends MvpModel {

  void fetchInstruction(Location newLocation);

  void setModelListener(DirectionsModelListener listener);

  void initialize(List<LatLng> currentPathCoordinates, Float radius, Float deltaT,
      Float currentPhoneBearing);

}
