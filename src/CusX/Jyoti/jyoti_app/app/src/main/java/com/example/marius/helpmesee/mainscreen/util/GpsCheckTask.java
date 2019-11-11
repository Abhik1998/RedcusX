package com.example.marius.helpmesee.mainscreen.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

/**
 * <Params doInBackgoung, Params onProgressUpdate, Param (result) onPostExecute>
 *   A AsyncTask can be executed only once
 */
public class GpsCheckTask extends AsyncTask<Void, Void, Boolean> {

  private GpsVisiter consumer;

  public interface GpsVisiter {

    void gpsResult(Boolean gpsOn);
  }

  public GpsCheckTask(GpsVisiter consumer) {
    this.consumer = consumer;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    int locationMode = 0;
    String locationProviders;

    Context context = (Context) consumer;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      try {
        locationMode = Settings.Secure
            .getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        e.printStackTrace();
      }

      return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    } else {
      locationProviders = Settings.Secure
          .getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }

  }

  @Override
  protected void onPostExecute(Boolean gpsOn) {
    consumer.gpsResult(gpsOn);
  }
}
