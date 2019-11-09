package com.example.marius.helpmesee.util;

import android.content.Context;
import com.example.marius.helpmesee.app_logic.AppFeaturesEnum;
import com.example.marius.helpmesee.app_logic.MvpModel;
import com.example.marius.helpmesee.directions.model.DirectionsModelManagerImpl;
import com.example.marius.helpmesee.location.model.LocationModelManagerImpl;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class ModelsFactory {

  public static MvpModel createModel(Context context, AppFeaturesEnum featureId) {
    MvpModel mvpModel = null;
    switch (featureId) {
      case DIRECTIONS:
        mvpModel = new DirectionsModelManagerImpl();

        break;

      case LOCATION:
        mvpModel = new LocationModelManagerImpl(context);

        break;
      default:
    }

    return mvpModel;
  }

}
