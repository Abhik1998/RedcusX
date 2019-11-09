package com.example.marius.helpmesee.directions.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.example.marius.helpmesee.R;
import com.example.marius.helpmesee.app_logic.AppState;
import com.example.marius.helpmesee.app_logic.Constants;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class DirectionsScreenViewImpl implements DirectionsScreenView {

  private Context context;
  private DirectionsScreenListener screenListener;

  private Bundle viewState;

  private View rootView; //layout
  private Button speechInputButton;
  private Button findPathButton;
  private AutoCompleteTextView destActv;
  private TextView distanceTV;
  private TextView durationTV;

  public DirectionsScreenViewImpl(Context context, ViewGroup container) {
    this.context = context;
    rootView = LayoutInflater.from(context).inflate(R.layout.directions_screen_layout, container);
    viewState = new Bundle();

    initialize();
    setButtonsSize();
    addListenersToButtons();
  }

  private void initialize() {
    speechInputButton = (Button) rootView.findViewById(R.id.speechButton_directions);
    findPathButton = (Button) rootView.findViewById(R.id.findPathB);
    destActv = (AutoCompleteTextView) rootView.findViewById(R.id.destACTV);
    distanceTV = (TextView) rootView.findViewById(R.id.distanceTV);
    durationTV = (TextView) rootView.findViewById(R.id.durationTV);

    String[] testLocations = context.getResources().getStringArray(R.array.test_locations);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, testLocations);
    destActv.setAdapter(adapter);

    destActv.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        screenListener.hideSoftKeyboard();

      }

    });
  }

  private void setButtonsSize() {
    LayoutParams speechLp = AppState.getInstance().getSpeechButtonLayoutParams();
    LayoutParams bottomLeftButtonLP = AppState.getInstance().getBottomLeftButtonLP();

    speechInputButton.setLayoutParams(speechLp);
    findPathButton.setLayoutParams(bottomLeftButtonLP);
  }

  private void addListenersToButtons() {
    findPathButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isDesinationSet()) {
          String destination = destActv.getText().toString();
          screenListener.findPath(destination);
        }
      }
    });

    speechInputButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        screenListener.startRecording();
      }
    });

  }

  @Override
  public View getAndroidLayoutView() {
    return rootView;
  }

  @Override
  public Bundle getViewState() {
    return viewState;
  }

  @Override
  public void onSaveViewState(Bundle outState) {
    String destinationText = destActv.getText().toString();

    if (!TextUtils.isEmpty(destinationText)) {
      outState.putString(Constants.DESTINATION_TEXT, destinationText);
    }

    String distanceText = distanceTV.getText().toString();
    if (!TextUtils.isEmpty(distanceText)) {
      outState.putString(Constants.DISTANCE_KM, distanceText);
      String duration = durationTV.getText().toString();
      outState.putString(Constants.DURATION_TIME, duration);
    }

  }

  @Override
  public void onRestoreInstanceState(Bundle inState) {
    String destination = inState.getString(Constants.DESTINATION_TEXT);
    destActv.setText(destination);

    String distance = inState.getString(Constants.DISTANCE_KM);
    distanceTV.setText(distance);

    String duration = inState.getString(Constants.DURATION_TIME);
    durationTV.setText(duration);
  }

  private boolean isDesinationSet() {
    boolean valid = true;

    String destination = destActv.getText().toString();

    if (TextUtils.isEmpty(destination)) {
      destActv.setError("Required!");
      valid = false;
    }

    return valid;
  }

  @Override
  public void setScreenListener(DirectionsScreenListener listener) {
    screenListener = listener;
  }

  @Override
  //this method will set the text decoded from speech input
  public void setDestination(String destination) {
    destActv.setText(destination);
  }

  @Override
  public void setDistance(String distance) {
    distanceTV.setText(distance);
  }

  @Override
  public void setDuration(String duration) {
    durationTV.setText(duration);
  }

}
