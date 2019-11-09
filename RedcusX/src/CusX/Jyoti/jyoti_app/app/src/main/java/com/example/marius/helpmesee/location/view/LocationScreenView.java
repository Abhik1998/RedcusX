package com.example.marius.helpmesee.location.view;

import com.example.marius.helpmesee.app_logic.MvpView;
import com.example.marius.helpmesee.location.presenter.LocationScreenListener;
import com.example.marius.helpmesee.location.presenter.LocationScreenPresenter;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public interface LocationScreenView extends MvpView {

  void displayUserCurrentLocation(String userCurrentLocation);

  void setListener(LocationScreenListener listener);
}
