package com.example.marius.helpmesee.app_logic;


import android.widget.RelativeLayout.LayoutParams;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class AppState {

  private static final AppState ourInstance = new AppState();
  //should be set by the foreground activity
  // private Context currentContext;

  private int phone_width_dp;
  private int phone_height_dp;

  private boolean appFistTimeLaunched = true;

  private LayoutParams speechButtonLayoutParams;
  private LayoutParams bottomLeftButtonLP;


  public static AppState getInstance() {
    return ourInstance;
  }

  private AppState() {
  }

  public int getPhone_width_dp() {
    return phone_width_dp;
  }

  public void setPhone_width_dp(int phone_width_dp) {
    this.phone_width_dp = phone_width_dp;
  }

  public int getPhone_height_dp() {
    return phone_height_dp;
  }

  public void setPhone_height_dp(int phone_height_dp) {
    this.phone_height_dp = phone_height_dp;
  }

  public boolean isAppFistTimeLaunched() {
    return appFistTimeLaunched;
  }

  public void setAppFistTimeLaunched(boolean appFistTimeLaunched) {
    this.appFistTimeLaunched = appFistTimeLaunched;
  }

  public LayoutParams getSpeechButtonLayoutParams() {
    return speechButtonLayoutParams;
  }

  public void setSpeechButtonLayoutParams(LayoutParams speechButtonLayoutParams) {
    this.speechButtonLayoutParams = speechButtonLayoutParams;
  }

  public void setBottomLeftButtonLP(LayoutParams bottomLeftButtonLP) {
    this.bottomLeftButtonLP = bottomLeftButtonLP;
  }

  public LayoutParams getBottomLeftButtonLP() {
    return bottomLeftButtonLP;
  }
}
