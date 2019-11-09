package com.example.marius.helpmesee.mainscreen.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.example.marius.helpmesee.R;
import com.example.marius.helpmesee.app_logic.AppState;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

public class MainMenuScreenViewImpl implements MainMenuScreenView {

  @Override
  public void onSaveViewState(Bundle outState) {

  }

  @Override
  public void onRestoreInstanceState(Bundle inState) {

  }

  private MainMenuScreenListener mainMenuScreenListener;

  private final int WIFI = 0;
  private final int ROAMING = 1;

  private View rootView; //the layout
  private ListView featuresListView;
  private Button speechInputButton;
  private AlertDialog internetAlertDialog;
  private AlertDialog gpsAlertDialog;

  private Context context;

  /**
   * @param context - It allows access to application-specific resources and classes, <br> as well
   * as up-calls for application-level operations such as launching activities, <br> broadcasting
   * and receiving intents
   * @param container - A ViewGroup is a special view that can contain other views (called children.) <br>
   * The view group is the base class for layouts and views containers.
   */
  public MainMenuScreenViewImpl(Context context, ViewGroup container) {
    this.context = context;
    rootView = LayoutInflater.from(context).inflate(R.layout.main_screen_layout, container);

    featuresListView = (ListView) rootView.findViewById(R.id.featuresList);
    speechInputButton = (Button) rootView.findViewById(R.id.speechButton_main);

    initialize();
  }

  private void initialize() {

    speechInputButton.setLayoutParams(AppState.getInstance().getSpeechButtonLayoutParams());

    speechInputButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        mainMenuScreenListener.startRecording();
      }
    });

    createInternetAlertDialog();
    createGpsAlertDialog();

  }


  @Override
  public View getAndroidLayoutView() {
    return rootView;
  }

  @Override
  public Bundle getViewState() {
    return null;
  }

  private void createInternetAlertDialog() {

    final Builder builderIAD = new Builder(context);
    CharSequence[] options = {"Wi-Fi", "Mobile data"};

    builderIAD.setTitle(R.string.network_off_title);
    //builderIAD.setMessage(R.string.network_off_message);
    /**
     * DialogInterface - Interface that defines a dialog-type class that can be shown, dismissed, or canceled, <br>
     * and may have buttons that can be clicked.
     */

    final HmsDialogInterfListener hmsDialogInterfListener = new HmsDialogInterfListener();

    builderIAD.setSingleChoiceItems(options, 0, hmsDialogInterfListener);

    builderIAD.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        int selectedOption = hmsDialogInterfListener.getSelectedOption();

        switch (selectedOption) {
          case WIFI:
            Intent wifiIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            context.startActivity(wifiIntent);
            break;

          case ROAMING:
            //fm: does not launch the activity where you can turn mobile data on
            Intent roamingIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            context.startActivity(roamingIntent);
            break;
        }

        mainMenuScreenListener.internetConnectionCheckDone();
      }
    });


    internetAlertDialog = builderIAD.create();

  }

  private class HmsDialogInterfListener implements DialogInterface.OnClickListener {

    private int selectedOption;

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
      switch (i) {
        case WIFI:
          selectedOption = 0;
          break;

        case ROAMING:
          selectedOption = 1;
          break;
      }
    }

    public int getSelectedOption() {
      return selectedOption;
    }
  }

  private void createGpsAlertDialog() {

    final Builder builderGAD = new Builder(context);
    builderGAD.setTitle(R.string.gps_off_title);
    builderGAD.setMessage(R.string.gps_off_message);
    builderGAD.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(locationIntent);
      }
    });

    gpsAlertDialog = builderGAD.create();
  }


  @Override
  public void setScreenListener(MainMenuScreenListener listener) {
    mainMenuScreenListener = listener;

    //map interface of our custom listener with the one of ListView
    featuresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      /**
       * @params position -  the id in the listview row
       * @params id - can be the id of  row in db (that's why is long)
       */
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mainMenuScreenListener != null) {
          mainMenuScreenListener.onFeatureSelected(position);
        }
      }
    });
  }


  @Override
  public void displayAppFeatures(List<String> features) {
//    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
//        android.R.layout.simple_list_item_1);
    //arrayAdapter.addAll(features);

    HmsArrayAdapter arrayAdapter = new HmsArrayAdapter(context,R.layout.row_layout, features);

    featuresListView.setAdapter(arrayAdapter);
  }

  @Override
  public void askTurnInternetOn() {
    internetAlertDialog.show();
  }

  @Override
  public void askTurnGpsOn() {
    gpsAlertDialog.show();
  }

}
