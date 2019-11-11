package com.example.marius.helpmesee.app_logic;

import android.content.Intent;
import android.speech.RecognizerIntent;
import java.util.Locale;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class Constants {
  public static final String HMS_INFO = "HmsInfo"; //tag used for logging info
  public static final int SPEECH_INPUT_CODE = 1;
  public static final Intent SPEECH_INTENT;
  public static final float EARTH_RADIUS_MILES = 3964.037911746f;
  public static final float EARTH_RADIUS_M = 6371 * 1000;
  public static final float EARTH_RADIUS_KM = 6371 ;


  //string constants
  public static final String PATH_ORIGIN = "origin";
  public static final String PATH_DESTINATION = "destination";
  public static final String PATH_MODE = "mode";
  public static final String PATH_WALKING = "walking";
  
  //directions screen bundle keys
  public static final String DESTINATION_TEXT = "destination";
  public static final String DURATION_TIME = "duration";
  public static final String DISTANCE_KM = "distance";
  public static final String CURRENT_PATH_STRING = "currentPath";


  /**
   *   This block gets executed when the class is loaded in the memory.
   */
  static {
    SPEECH_INTENT = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    SPEECH_INTENT
        .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    Locale language = Locale.ENGLISH;
    SPEECH_INTENT.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
    SPEECH_INTENT.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...");
  }


//  public static final float PHONE_WIDTH_DP;
//  public static final float PHONE_HEIGHT_DP;
//
//  static {
//    Context currentContext = AppState.getInstance().getCurrentContext();
//    DisplayMetrics displayMetrics = currentContext.getResources().getDisplayMetrics();
//
//
//    //FIXME: these values are not the correct ones
//    PHONE_WIDTH_DP  = displayMetrics.widthPixels / displayMetrics.density;
//    PHONE_HEIGHT_DP  = displayMetrics.heightPixels / displayMetrics.density;
//  }



}
