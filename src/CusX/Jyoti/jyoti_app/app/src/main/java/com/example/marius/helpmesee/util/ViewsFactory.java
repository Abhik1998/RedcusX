package com.example.marius.helpmesee.util;

import android.content.Context;
import com.example.marius.helpmesee.app_logic.AppFeaturesEnum;
import com.example.marius.helpmesee.app_logic.AppState;
import com.example.marius.helpmesee.app_logic.MvpView;
import com.example.marius.helpmesee.directions.view.DirectionsScreenView;
import com.example.marius.helpmesee.directions.view.DirectionsScreenViewImpl;
import com.example.marius.helpmesee.location.view.LocationScreenViewImpl;
import com.example.marius.helpmesee.mainscreen.view.MainMenuScreenViewImpl;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class ViewsFactory {

  public static MvpView createView(Context context, AppFeaturesEnum featureId) {
    MvpView screenView = null;

    switch (featureId) {
      case DIRECTIONS:
        screenView = new DirectionsScreenViewImpl(context, null);
        break;

      case LOCATION:
        screenView = new LocationScreenViewImpl(context, null);
        break;

        //add the other features when they are implemented
      default:
        screenView = new MainMenuScreenViewImpl(context, null);
    }

    return screenView;
  }

}
