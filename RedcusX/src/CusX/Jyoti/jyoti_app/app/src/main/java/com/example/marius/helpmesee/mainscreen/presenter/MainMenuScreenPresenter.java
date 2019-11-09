package com.example.marius.helpmesee.mainscreen.presenter;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.example.marius.helpmesee.app_logic.AppFeaturesEnum;
import com.example.marius.helpmesee.app_logic.AppState;
import com.example.marius.helpmesee.app_logic.Constants;
import com.example.marius.helpmesee.directions.presenter.DirectionsScreenPresenter;
import com.example.marius.helpmesee.location.presenter.LocationScreenPresenter;
import com.example.marius.helpmesee.mainscreen.util.GpsCheckTask;
import com.example.marius.helpmesee.mainscreen.util.GpsCheckTask.GpsVisiter;
import com.example.marius.helpmesee.mainscreen.util.InternetCheckTask;
import com.example.marius.helpmesee.mainscreen.util.InternetCheckTask.InternetVisiter;
import com.example.marius.helpmesee.mainscreen.view.MainMenuScreenListener;
import com.example.marius.helpmesee.mainscreen.view.MainMenuScreenView;
import com.example.marius.helpmesee.util.ViewsFactory;
import java.util.ArrayList;
import java.util.List;

public class MainMenuScreenPresenter extends MainMenuScreenListener implements InternetVisiter,
    GpsVisiter {

  private final int FINE_LOCATION_CODE = 1;
  private static final String MAIN_SCREEN_TAG = "MainScreen";

  //the model goes here if any
  private MainMenuScreenView rootView; // ui element of this presenter
  private List<String> appFeaturesList;
  public final AppState appState = AppState.getInstance();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //AppState.getInstance().setCurrentContext(this);
    //td: save the size in SharedPreferences so that detectPhoneSize() is called only once, after the app was first launched

    if (appState.isAppFistTimeLaunched()) {
      detectPhoneSize();
      setSpeechButtonLayoutParams();
      setLeftButtonLayoutParams();
    }

    rootView = (MainMenuScreenView) ViewsFactory.createView(this, AppFeaturesEnum.MAIN_MENU);
    setContentView(rootView.getAndroidLayoutView());

    addAppFeatures();

    rootView.setScreenListener(this);
    rootView.displayAppFeatures(appFeaturesList);


  }

  /**
   * Each screen, but the Main Screen, has a button on the bottom-left side
   * Thus these settings ensures that this button stays on the same positions on all screens
   * so it is more easy for the user to access it
   */
  private void setLeftButtonLayoutParams() {
    int phone_height_dp = AppState.getInstance().getPhone_height_dp();
    int phone_width_dp = AppState.getInstance().getPhone_width_dp();
    int spButtonWidth = phone_width_dp;
    int spButtonHeight = phone_height_dp / 3;

    LayoutParams layoutParamsFP = new LayoutParams(spButtonWidth, spButtonHeight);
    layoutParamsFP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    layoutParamsFP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    layoutParamsFP.bottomMargin = phone_height_dp / 20;
    layoutParamsFP.rightMargin = phone_height_dp / 20;
    layoutParamsFP.leftMargin = phone_height_dp / 20;
    layoutParamsFP.topMargin = phone_height_dp / 20;

    AppState.getInstance().setBottomLeftButtonLP(layoutParamsFP);
  }


  @Override
  protected void onResume() {
    super.onResume();
    checkServicesNeeded();
  }

  private void checkServicesNeeded() {

    while (!checkLocationPermission()) {
      askForLocationPermission();
    }

    InternetCheckTask internetCheckTask = new InternetCheckTask(this);
    internetCheckTask.execute();
  }


  private void addAppFeatures() {
    appFeaturesList = new ArrayList<>();

    for (AppFeaturesEnum feature : AppFeaturesEnum.values()
        ) {
      // we don't want it in this list
      if (feature == AppFeaturesEnum.MAIN_MENU) {
        continue;
      }

      appFeaturesList.add(feature.toString());
    }
  }


  @Override
  public void onFeatureSelected(long id) {
    final String featureValue = appFeaturesList.get((int) id);
    final AppFeaturesEnum selectedFeature = AppFeaturesEnum.stringToFeature(
        featureValue);

    switch (selectedFeature) {
      case DIRECTIONS:
        Log.i(MAIN_SCREEN_TAG, "Launching DIRECTIONS screen");

        startActivity(new Intent(MainMenuScreenPresenter.this, DirectionsScreenPresenter.class));
        break;

      case LOCATION:
        Log.i(MAIN_SCREEN_TAG, "Launching LOCATION screen");

        startActivity(new Intent(MainMenuScreenPresenter.this, LocationScreenPresenter.class));
        break;

      case SCENE_DESCRIPTION:
        Toast.makeText(this, "Feature not implemented yet!", Toast.LENGTH_SHORT).show();
        Log.i(Constants.HMS_INFO, "Feature not implemented yet! ");
//        Log.i(MAIN_SCREEN_TAG, "Launching SCENE_DESCRIPTION screen");
//
//        startActivity(new Intent(MainMenuScreenPresenter.this, SceneDescPresenter.class));
        break;

      case TEXT_RECOGNITION:
        Toast.makeText(this, "Feature not implemented yet!", Toast.LENGTH_SHORT).show();
        Log.i(Constants.HMS_INFO, "Feature not implemented yet! ");
//        Log.i(MAIN_SCREEN_TAG, "Launching TEXT_RECOGNITION screen");
//
//        startActivity(new Intent(MainMenuScreenPresenter.this, TextRecPresenter.class));
        break;

      default:
        Log.e(MAIN_SCREEN_TAG, "Feature doesn't exist!");

    }

    // this.finish();
  }

  private void detectPhoneSize() {
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

    display.getSize(size);

    int width_pixel = size.x;
    int height_pixel = size.y;

    appState.setPhone_height_dp((int) (height_pixel / displayMetrics.density));
    appState.setPhone_width_dp(((int) (width_pixel / displayMetrics.density)));
    appState.setAppFistTimeLaunched(false);

  }

  private void setSpeechButtonLayoutParams() {
    int phone_height_dp = AppState.getInstance().getPhone_height_dp();
    int phone_width_dp = AppState.getInstance().getPhone_width_dp();
    int spButtonWidth = (int) (phone_width_dp);
    int spButtonHeight = phone_height_dp / 3;

    //programmaticaly configured the position of button since it will be a children
    //in a RelativeLayout and I want it to be in the same place in each screen
    LayoutParams layoutParams = new LayoutParams(spButtonWidth, spButtonHeight);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    layoutParams.bottomMargin = phone_height_dp / 20;
    layoutParams.rightMargin = phone_height_dp / 20;
    layoutParams.leftMargin = phone_height_dp / 20;
    layoutParams.topMargin = phone_height_dp / 20;

    appState.setSpeechButtonLayoutParams(layoutParams);
  }

  private boolean checkLocationPermission() {
    // Ask for permission if it wasn't granted yet
    return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED);
  }

  private void askForLocationPermission() {
    ActivityCompat.requestPermissions(
        this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        FINE_LOCATION_CODE
    );
  }

  @Override
  public void gpsResult(Boolean gpsOn) {
    if (!gpsOn) {
      rootView.askTurnGpsOn();
    }
  }

  @Override
  public void internetResult(Boolean internetOn) {
    if (!internetOn) {
      rootView.askTurnInternetOn();
    } else {
      GpsCheckTask gpsCheckTask = new GpsCheckTask(this);
      gpsCheckTask.execute();
    }

  }

  @Override
  public void internetConnectionCheckDone() {
    GpsCheckTask gpsCheckTask = new GpsCheckTask(this);
    gpsCheckTask.execute();
  }

  @Override
  public void execute(String detectedText) {
    if (detectedText.toUpperCase().equals("EXIT")) {
      finishAffinity();
    }
  }
}
