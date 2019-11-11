package com.example.marius.helpmesee.mainscreen.util;

import android.os.AsyncTask;
import android.util.Log;
import com.example.marius.helpmesee.app_logic.Constants;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class InternetCheckTask extends AsyncTask<Void, Void, Boolean> {


  private InternetVisiter consumer;

  public interface InternetVisiter {

    void internetResult(Boolean internetOn);
  }

  public InternetCheckTask(InternetVisiter consumer) {
    this.consumer = consumer;
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    try {
      Socket googleDataCenterSock = new Socket();
      googleDataCenterSock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
      googleDataCenterSock.close();
      return true;
    } catch (IOException e) {
      Log.e(Constants.HMS_INFO, "Internet connection isn't available!" );
      return false;
    }
  }

  @Override
  protected void onPostExecute(Boolean internetOn) {
    consumer.internetResult(internetOn);
  }
}