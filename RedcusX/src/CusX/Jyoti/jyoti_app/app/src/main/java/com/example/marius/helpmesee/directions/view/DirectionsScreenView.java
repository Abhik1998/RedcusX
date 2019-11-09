package com.example.marius.helpmesee.directions.view;

import com.example.marius.helpmesee.app_logic.MvpView;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public interface DirectionsScreenView extends MvpView {

  void setScreenListener(DirectionsScreenListener listener);

  void setDestination(String destination);

  void setDistance(String distance);

  void setDuration(String duration);

}
