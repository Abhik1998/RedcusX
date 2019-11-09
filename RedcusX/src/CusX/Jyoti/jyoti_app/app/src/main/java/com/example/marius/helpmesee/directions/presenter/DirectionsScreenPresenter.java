package com.example.marius.helpmesee.directions.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.example.marius.helpmesee.R;
import com.example.marius.helpmesee.app_logic.AppFeaturesEnum;
import com.example.marius.helpmesee.app_logic.Constants;
import com.example.marius.helpmesee.directions.model.DirectionsModelListener;
import com.example.marius.helpmesee.directions.model.DirectionsModelManager;
import com.example.marius.helpmesee.directions.model.DirectionsModelManagerImpl;
import com.example.marius.helpmesee.directions.model.Instruction;
import com.example.marius.helpmesee.directions.model.PathDto;
import com.example.marius.helpmesee.directions.view.DirectionsScreenListener;
import com.example.marius.helpmesee.directions.view.DirectionsScreenView;
import com.example.marius.helpmesee.directions.view.DirectionsScreenViewImpl;
import com.example.marius.helpmesee.util.ModelsFactory;
import com.example.marius.helpmesee.util.ViewsFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

public class DirectionsScreenPresenter extends DirectionsScreenListener implements
    OnMapReadyCallback, PathFoundListener, DirectionsModelListener {

  //view
  private DirectionsScreenView rootView;

  //constants
  private static final int DEFAULT_ZOOM = 15;
  private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
  private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
      UPDATE_INTERVAL_IN_MILLISECONDS / 2;

  // Keys for storing activity state.
  private static final String KEY_CAMERA_POSITION = "camera_position";
  private static final String KEY_LOCATION = "location";
  private static final String DIRECTIONS_SCREEN_TAG = "DirectionsScreen";
  private final int REQUEST_CHECK_SETTINGS = 20;


  //interaction with map object
  private LocationRequest locationRequest;
  private GoogleMap googleMap;
  private LocationCallback locationCallback;
  // The entry point to the Fused Location Provider.
  private FusedLocationProviderClient fusedLocationProviderClient;

  //logic
  private Location currentUserLocation;
  private PathProvider pathProvider;
  private boolean requestedLocationUpdates;
  private DirectionsModelManager directionsMM;
  private SensorManager sensorManager;
  private Sensor sensorOrientation;
  private Float currentPhoneBearing;
  private HmsSensorEventListener orientationSensorListener;
  private List<LatLng> currentPathCoordinates;
  private Bundle instanceBundle;
  //predict future location in DELTA_T seconds
  private final static Float DELTA_T = 3f;
  private final static Float PATH_RADIUS = 3.5f;
  private long startTime;
  /**
   * used to avoid useless animation of camera
   */
  private float previousBearing;

  //used for real-time debugging
  private Marker futureLocationMarker;
  private Marker normalLocationMarker;
  private Marker targetLocationMarker;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    rootView = (DirectionsScreenView) ViewsFactory.createView(this, AppFeaturesEnum.DIRECTIONS);
    rootView.setScreenListener(this);
    setContentView(rootView.getAndroidLayoutView());

    directionsMM = (DirectionsModelManager) ModelsFactory
        .createModel(this, AppFeaturesEnum.DIRECTIONS);
    directionsMM.setModelListener(this);

    /**
     * A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity <br>
     *   Note: isn't this a breaking of the MVP architecture?
     *   https://developer.android.com/reference/android/app/Fragment
     *   1 dirtiness point
     * Note: I could move the part where I get the fragment into the view but then I will end up with
     *       a dependency on the activity (i.e. 1 dirtiness point)
     *       https://www.techyourchance.com/activities-android/
     */
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    createLocationCallback();
    createLocationRequest();
    buildLocationRequestSettings();

    initialize();
  }

  private void createLocationCallback() {
    //algorithm : compute bearing of direction between the two closest points to user location
    // compare user's direction of moving bearing with the bearing between two stopPoints
    // notify user to go left or right

    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult lRs) {
        if (lRs == null) {
          return;
        }
        for (Location newLocation : lRs.getLocations()) {

          double latitude = newLocation.getLatitude();
          double longitude = newLocation.getLongitude();
          float bearing = newLocation.getBearing();
          // Log.i(DIRECTIONS_SCREEN_TAG,
          //     /*time + " (" + lat + ", " + lng + ")" +*/ " Bearing: " + bearing + "\n Accuracy: " + accuracy);
          directionsMM.fetchInstruction(newLocation);
          //Toast.makeText(DirectionsScreenPresenter.this, "Bearing: " + newLocation.getSpeed(), Toast.LENGTH_SHORT).show();
          // Log.i(DIRECTIONS_SCREEN_TAG,  + latitude + "," + longitude);

          //avoid useless calls to method
          if ((bearing != 0) && (bearing != previousBearing)) {
            adjustCamera(bearing, latitude, longitude);
            previousBearing = bearing;
          }

        }
      }

    };
  }


  private void adjustCamera(float bearing, double latitude, double longitude) {
    if (googleMap == null) {
      return;
    }
    CameraPosition camPos = CameraPosition
        .builder(googleMap.getCameraPosition())
        .bearing(bearing)
        .target(new LatLng(latitude, longitude))
        .build();
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

  }

  private void initialize() {
    pathProvider = new PathProvider(this);
    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    orientationSensorListener = new HmsSensorEventListener();
  }

  /**
   * Saves the state of the map when the activity is paused.
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    if (googleMap != null) {
      outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
      outState.putParcelable(KEY_LOCATION, currentUserLocation);
    }

    if (currentPathCoordinates != null) { //if user has a path already set
      rootView.onSaveViewState(outState);

      String currentPathString = currentPathCoordinates.toString();
      outState.putString(Constants.CURRENT_PATH_STRING, currentPathString);
    }

    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    this.instanceBundle = savedInstanceState;
  }


  private List<LatLng> toListLatLng(String currentPathString) {
    ArrayList<LatLng> coordinates = new ArrayList<>();
    int size = currentPathString.length();

    currentPathString = currentPathString.substring(1, size - 2);
    //creates an array of  with elements of form: latidude,longitude
    String[] latLngArr = currentPathString.split("\\)?,?\\s?lat/lng: \\(");

    int length = latLngArr.length;
    for (int i = 1; i < length; i++) {
      String[] latLng = latLngArr[i].split(",");
      Double latitude = Double.valueOf(latLng[0]);
      Double longitude = Double.valueOf(latLng[1]);

      coordinates.add(new LatLng(latitude, longitude));
    }

    return coordinates;
  }


  @SuppressLint("MissingPermission")
  @Override
  /**
   * Permision check is done when the app is launched
   */
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;

    googleMap.setMyLocationEnabled(true);

    //my location button is bad for accesibility as described by Scanner
    googleMap.getUiSettings().setMyLocationButtonEnabled(false);

    getDeviceLocation();

    //handle a configuration change like screen resize
    if ((instanceBundle != null) &&
        (instanceBundle.getSerializable(Constants.CURRENT_PATH_STRING) != null)) {
      String currentPathString = instanceBundle.getString(Constants.CURRENT_PATH_STRING);
      if (currentPathString != null) {
        currentPathCoordinates = toListLatLng(currentPathString);
        drawPathOnMap(currentPathCoordinates);
      }
      directionsMM.initialize(currentPathCoordinates, PATH_RADIUS, DELTA_T, currentPhoneBearing);

      requestedLocationUpdates = true;

      rootView.onRestoreInstanceState(instanceBundle);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (requestedLocationUpdates) {
      startLocationUpdates();
    }
  }

  protected void onPause() {
    super.onPause();
    if (requestedLocationUpdates) {
      stopLocationUpdates();
    }
  }

  @SuppressLint("MissingPermission")
  private void startLocationUpdates() {
    fusedLocationProviderClient.requestLocationUpdates(locationRequest,
        locationCallback,
        Looper.myLooper()/* Looper */);
  }

  private void stopLocationUpdates() {
    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
  }

  /**
   * Location updates are received each second
   */
  private void createLocationRequest() {
    locationRequest = new LocationRequest();
    locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS); // 2000 ms
    locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS); // 1000 ms
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private void buildLocationRequestSettings() {
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest);
    SettingsClient client = LocationServices.getSettingsClient(this);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
      @Override
      public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
        Log.i(DIRECTIONS_SCREEN_TAG, "Location settings are ok!");
      }
    });

    task.addOnFailureListener(this, new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        if (e instanceof ResolvableApiException) {
          try {
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(DirectionsScreenPresenter.this,
                REQUEST_CHECK_SETTINGS);
          } catch (IntentSender.SendIntentException sendEx) {
          }
        }
      }
    });
  }


  /**
   * Gets the current location of the device and moves camera on user's location.
   */
  private void getDeviceLocation() {
    try {
      Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
      locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
          if (task.isSuccessful()) {
            // Set the map's camera position to the current location of the device.
            currentUserLocation = task.getResult();
            final double latitude = currentUserLocation.getLatitude();
            final double longitude = currentUserLocation.getLongitude();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude,
                    longitude), DEFAULT_ZOOM));

            Log.i(DIRECTIONS_SCREEN_TAG,
                "User location:here (" + latitude + ", " + longitude + ")");


          } else {
            Log.e(DIRECTIONS_SCREEN_TAG, "Exception: %s", task.getException());
          }
        }
      });
    } catch (SecurityException e) {
      Log.e(DIRECTIONS_SCREEN_TAG, e.getMessage());
    }
  }

  @Override
  public void findPath(final String destination) {
    //setUserCurrentBearing();

    //get the path
    //send message to view to draw it
    //update device location
    try {
      Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
      locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
          if (task.isSuccessful()) {
            // Set the map's camera position to the current location of the device.
            currentUserLocation = task.getResult();
            final double latitude = currentUserLocation.getLatitude();
            final double longitude = currentUserLocation.getLongitude();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude,
                    longitude), DEFAULT_ZOOM));

            Log.i(DIRECTIONS_SCREEN_TAG,
                "User location before computing the path: (" + latitude + ", " + longitude + ")");

            pathProvider.setDestination(destination);

            final String originLatLng =
                latitude + "," + longitude;
            pathProvider.setOrigin(originLatLng);

            pathProvider.start();

          } else {
            Log.e(DIRECTIONS_SCREEN_TAG, "Exception: %s", task.getException());
          }
        }
      });
    } catch (SecurityException e) {
      Log.e(DIRECTIONS_SCREEN_TAG, e.getMessage());
    }

  }

  private void setUserCurrentBearing() {
    sensorManager.registerListener(orientationSensorListener, sensorOrientation,
        SensorManager.SENSOR_DELAY_NORMAL);
    //   It passes 0 as initial bearing, too fast
    currentPhoneBearing = orientationSensorListener.getCurrentBearing();
    sensorManager.unregisterListener(orientationSensorListener);
  }


  @Override
  public void onPathsFound(List<PathDto> pathDtos) {
    if (pathDtos != null && pathDtos.size() > 0) {
      currentPathCoordinates = pathDtos.get(0).coordinatesLatLng;
      PathDto pathDto = pathDtos.get(0);

      String duration = DirectionsHelper.prettyReadDuration(pathDto.timeM);
      rootView.setDuration(duration);

      String distance = String.valueOf(pathDto.distanceKM) + "km";
      rootView.setDistance(distance);

      drawPathOnMap(currentPathCoordinates);

      directionsMM.initialize(currentPathCoordinates, PATH_RADIUS, DELTA_T, currentPhoneBearing);

      requestedLocationUpdates = true;
      startLocationUpdates();

    } else {
      Log.e(DIRECTIONS_SCREEN_TAG, "Couldn't find a valid path!");
      // td:  add an id for each request to text textToSpeech and check status
      textToSpeech.speak("Couldn't find a valid path! Please try a more detailed description!",
          TextToSpeech.QUEUE_ADD, null);
      Toast.makeText(this, "Couldn't find a valid path! Please try a more detailed description!",
          Toast.LENGTH_SHORT).show();
    }
  }

  private void drawPathOnMap(List<LatLng> coordinatesLatLng) {
//here is a npe, google map
    googleMap.clear();

    PolylineOptions polylineOptions = new PolylineOptions()
        .geodesic(true)
        .color(Color.BLUE)
        .width(10);

    // debug markers used for observing in real-time the predictedFutureLocation, normalPoint and target
//    LatLng startPoint = coordinatesLatLng.get(0);
//    futureLocationMarker = googleMap
//        .addMarker(
//            new MarkerOptions().position(new LatLng(startPoint.latitude, startPoint.longitude)));
//
//    targetLocationMarker = googleMap
//        .addMarker(
//            new MarkerOptions().position(new LatLng(startPoint.latitude, startPoint.longitude))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//    normalLocationMarker = googleMap
//        .addMarker(
//            new MarkerOptions().position(new LatLng(startPoint.latitude, startPoint.longitude))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    for (LatLng p : coordinatesLatLng) {
      polylineOptions.add(p);
    }

    //draw markers representing the straight segments of path on map for debugging purposes
//    for (LatLng p : coordinatesLatLng) {
//      String locSnippet = p.latitude + ", " + p.longitude;
//      MarkerOptions markerOptions = new MarkerOptions().position(p).title("m").snippet(locSnippet)
//          .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//      Marker marker = googleMap.addMarker(markerOptions);
//      marker.showInfoWindow();
//    }

//    googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//      @Override
//      public boolean onMarkerClick(Marker marker) {
//        LatLng latLng = marker.getPosition();
//        Log.i(DIRECTIONS_SCREEN_TAG, "Clicked marker:" + latLng);
//        return false;
//      }
//    });

    googleMap.addPolyline(polylineOptions);
    final LatLng destinationLatLng = coordinatesLatLng.get(coordinatesLatLng.size() - 1);

    //add finish marker - icon: a flag
    googleMap.addMarker(new MarkerOptions()
        .position(destinationLatLng)
        .icon(BitmapDescriptorFactory
            .fromResource(R.mipmap.stop_flag)));

    textToSpeech.speak("Path found, you can start!", TextToSpeech.QUEUE_ADD, null);
  }

  @Override
  public void hideSoftKeyboard() {
    InputMethodManager inputManager =
        (InputMethodManager) this.
            getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(
        this.getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
  }

  @Override
  public void onInstrFetched(Instruction currentInstrForUser) {
    switch (currentInstrForUser) {
      case STRAIGHT:
        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

          /*
            ensures that instructions "continue straight" are not spammed
          */
        long TIMER_THRESHOLD = 10000;
        if (elapsed >= TIMER_THRESHOLD) {
          //        Log.i(DIRECTIONS_SCREEN_TAG, "elapsedTime: " + elapsed);
          textToSpeech.speak(currentInstrForUser.toString(), TextToSpeech.QUEUE_ADD, null);
          startTime = System.currentTimeMillis();
        }

        break;

      case END:
        //user arrived at destination, clear map
        textToSpeech.speak(currentInstrForUser.toString(), TextToSpeech.QUEUE_ADD, null);
        stopLocationUpdates();
        requestedLocationUpdates = false;
        googleMap.clear();

        break;
      default: // other types of instructions
        textToSpeech.speak(currentInstrForUser.toString(), TextToSpeech.QUEUE_ADD, null);
    }

    // Log.i(Constants.HMS_INFO, currentInstrForUser.toString());

    //Update debuggin markers position on the map
//    double latitude = currentInstrForUser.predictedFutureLocation.getLatitude();
//    double longitude = currentInstrForUser.predictedFutureLocation.getLongitude();
//    futureLocationMarker.setPosition(new LatLng(latitude, longitude));
//    normalLocationMarker.setPosition(
//        new LatLng(currentInstrForUser.normalPoint.getLatitude(), currentInstrForUser.normalPoint.getLongitude()));
//    targetLocationMarker
//        .setPosition(new LatLng(currentInstrForUser.target.getLatitude(), currentInstrForUser.target.getLongitude()));
  }


  @Override
  public void execute(String detectedText) {
    textToSpeech.speak("Destination: " + detectedText, TextToSpeech.QUEUE_ADD, null);
    rootView.setDestination(detectedText);
  }
}

class HmsSensorEventListener implements SensorEventListener {

  private float currentBearing;

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    float bearingAngle = sensorEvent.values[0];
    currentBearing = bearingAngle;
    Log.i(Constants.HMS_INFO,
        "Bearing detected using software sensor Orientation: " + bearingAngle);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int i) {

  }

  public float getCurrentBearing() {
    return currentBearing;
  }
}