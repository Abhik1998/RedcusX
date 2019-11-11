package com.example.marius.helpmesee.directions.view;

import com.example.marius.helpmesee.util.HmsActivity;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public abstract class DirectionsScreenListener extends HmsActivity {

  public abstract void findPath(String destination);

  /**
   * Called when one of the suggested destinations is clicked
   */
  public abstract void hideSoftKeyboard();
}
