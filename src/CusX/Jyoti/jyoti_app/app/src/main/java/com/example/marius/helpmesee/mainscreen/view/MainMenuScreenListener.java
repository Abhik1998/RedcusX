package com.example.marius.helpmesee.mainscreen.view;

import com.example.marius.helpmesee.util.HmsActivity;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public abstract class MainMenuScreenListener extends HmsActivity {

  public abstract void onFeatureSelected(long id);

  /**
   * Launch check for gps status
   */
  public abstract void internetConnectionCheckDone();

}
