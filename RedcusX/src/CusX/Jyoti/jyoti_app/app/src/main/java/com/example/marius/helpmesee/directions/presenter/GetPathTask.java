package com.example.marius.helpmesee.directions.presenter;

import android.os.AsyncTask;
import android.util.Log;
import com.example.marius.helpmesee.app_logic.Constants;
import com.example.marius.helpmesee.directions.model.PathDto;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class GetPathTask extends AsyncTask<URL, Void, Void> {

  private final PathFoundListener pathFoundListener;
  //if alternatives=true there pathDtos.size > 1
  private List<PathDto> pathDtos = new ArrayList<>();

  public GetPathTask(PathFoundListener pathFoundListener) {
    this.pathFoundListener = pathFoundListener;
  }


  @Override
  //executed on ui a background thread
  /**
   * Downloads the json and parses it
   */
  protected Void doInBackground(URL... urls) {
    try {
      URL requestUrl = urls[0];
      InputStream inputStream = requestUrl.openConnection().getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      StringBuilder buffer = new StringBuilder("");

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        buffer.append(line).append("\n");
      }

      String jsonRep = buffer.toString();

      parseJson(jsonRep);

    } catch (MalformedURLException e) {
      Log.e(Constants.HMS_INFO, "Directions request url isn't valid!");
      e.printStackTrace();
    } catch (IOException e) {
      Log.e(Constants.HMS_INFO, "Could not request directions!");
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    //result
    pathFoundListener.onPathsFound(pathDtos);
  }

  /**
   * Extracts the info from json response into a list of com.example.marius.helpmesee.directions.model.Paths objects
   * @param jsonRep
   * @return
   */
  private void parseJson(String jsonRep) {
    if (jsonRep == null) {
      Log.e(Constants.HMS_INFO, "Could not download the json that contains google directions!");
    }

    try {
      JSONObject rootJsonObj = new JSONObject(jsonRep);
      JSONArray routesJsonArray = rootJsonObj.getJSONArray("routes");

      for (int i = 0; i < routesJsonArray.length(); i++) {
        JSONObject currentRouteJsonO = routesJsonArray.getJSONObject(i);

        JSONArray legs = currentRouteJsonO.getJSONArray("legs");

        JSONObject leg = null;
        JSONObject legStartLocation = null;
        JSONObject legEndLocation = null;
        String originAddress = null;
        LatLng originLatLng = null;
        String destinationAddress = null;
        LatLng destinationLatLng = null;
        int distanceM = 0; //in meters
        int timeS = 0; //in minutes

        for (int j = 0; j < legs.length(); j++) {
          leg = legs.getJSONObject(j); // a portion of the route
          legStartLocation = leg.getJSONObject("start_location");
          legEndLocation = leg.getJSONObject("end_location");
          distanceM += leg.getJSONObject("distance").getInt("value");
          timeS += leg.getJSONObject("duration").getInt("value");

          if (j == 0) {
            originAddress = leg.getString("start_address");
            double lat = legStartLocation.getDouble("lat");
            double lng = legStartLocation.getDouble("lng");

            originLatLng = new LatLng(lat, lng);
          }

          //process other info from this leg
        }

        destinationAddress = leg.getString("end_address");
        double lng = legEndLocation.getDouble("lng");
        double lat = legEndLocation.getDouble("lat");

        destinationLatLng = new LatLng(lat, lng);

        JSONObject o_polyline = currentRouteJsonO.getJSONObject("overview_polyline");
        String encodedLatLangs = o_polyline.getString("points");
        List<LatLng> coordinates = PolyUtil.decode(encodedLatLangs);

        float timeM = formatFloat((float) timeS / 60);
        float distanceKM = formatFloat((float) distanceM / 1000);
        PathDto pathDto = new PathDto(originAddress, destinationAddress, originLatLng, destinationLatLng,
            distanceKM,
            timeM, coordinates);
        pathDtos.add(pathDto);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private float formatFloat(float f) {
    DecimalFormat decimalFormat = new DecimalFormat("#.0");
    f = Float.valueOf(decimalFormat.format(f));

    return f;
  }

}


