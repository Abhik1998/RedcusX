package com.example.marius.helpmesee.directions.presenter;

import android.util.Log;
import com.example.marius.helpmesee.app_logic.Constants;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class PathProvider {
  private static final String DIRECTIONS_ROOT_URL = "https://maps.googleapis.com/maps/api/directions/json?";
  private static final String GOOGLE_DIRECTIONS_KEY = "AIzaSyBA8OV5JoXoCQe-LvYZ06tm0YmWGIUxxEE";

  private final PathFoundListener pathFoundListener;
  private final HashMap<String,String> requestParameters;


  public PathProvider(PathFoundListener pathFoundListener) {
    this.pathFoundListener = pathFoundListener;
    requestParameters = new HashMap<>();

    //
    requestParameters.put(Constants.PATH_ORIGIN,"");
    requestParameters.put(Constants.PATH_DESTINATION,"");
    requestParameters.put(Constants.PATH_MODE,Constants.PATH_WALKING);
  }

  public void start(){
    URL requestUrl = null;

    try {
      requestUrl = createRequestUrl();

      //A task can be executed only once
      new GetPathTask(pathFoundListener).execute(requestUrl);
    } catch (UnsupportedEncodingException e) {
      Log.e(Constants.HMS_INFO, "Could not create Url, Unsupported encoding: UTF-8" );
      e.printStackTrace();
    } catch (MalformedURLException e) {
      Log.e(Constants.HMS_INFO, "Could not create Url, the format of url is incorrect" );
      e.printStackTrace();
    }

  }

  private URL createRequestUrl() throws UnsupportedEncodingException, MalformedURLException {
    //SB not synchronized (I don't think is necessary)
    StringBuilder requestUrlSB = new StringBuilder(
        "");

    requestUrlSB.append(DIRECTIONS_ROOT_URL);

    for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
      String paramName = entry.getKey();
      String value = escapeString(entry.getValue());

      String paramNameEncoded = URLEncoder.encode(paramName, "utf-8");
      String valueEncoded = URLEncoder.encode(value, "utf-8");
      String option = paramNameEncoded + "=" + valueEncoded + "&";

      requestUrlSB.append(option);
    }

    requestUrlSB.append("key=" + GOOGLE_DIRECTIONS_KEY);


    return new URL(requestUrlSB.toString());
  }

  /**
   *
   * @param origin - current location of user, i.e. (latitude,longitude)
   */
  public void setOrigin(String origin){
    requestParameters.put(Constants.PATH_ORIGIN, origin);
  }

  public void setDestination(String destination){
    requestParameters.put(Constants.PATH_DESTINATION, destination);
  }

  /**
   * In the ' ' (i.e. space) should be replaced by a '+'
   * @param s
   * @return
   */
  private String escapeString(String s){
    return  s.replace(' ', '+');
  }
}
