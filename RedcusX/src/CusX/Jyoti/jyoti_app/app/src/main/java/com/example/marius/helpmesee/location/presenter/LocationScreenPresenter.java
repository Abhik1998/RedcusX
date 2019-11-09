package com.example.marius.helpmesee.location.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import com.example.marius.helpmesee.app_logic.AppFeaturesEnum;
import com.example.marius.helpmesee.app_logic.Constants;
import com.example.marius.helpmesee.location.model.Contact;
import com.example.marius.helpmesee.location.model.LocationModelManager;
import com.example.marius.helpmesee.location.model.LocationModelManagerImpl;
import com.example.marius.helpmesee.location.view.LocationScreenView;
import com.example.marius.helpmesee.location.view.LocationScreenViewImpl;
import com.example.marius.helpmesee.util.ModelsFactory;
import com.example.marius.helpmesee.util.ViewsFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class LocationScreenPresenter extends LocationScreenListener {

  private LocationScreenView rootView;
  private LocationModelManager locationModelManager;


  //logic
  private static final String LOCATION_SCREEN_TAG = "LocationScreen";
  private FusedLocationProviderClient fusedLocationProviderClient;
  private static final int PICK_CONTACT_REQUEST = 90;
  private static final int PERMISSIONS_REQUEST_CODE = 91;
  private Location currentLocation;
  private boolean isThisActivityInForeground;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //null is passed beacause the layout is the parent of all views, i.e. container=none
    rootView = (LocationScreenView) ViewsFactory.createView(this, AppFeaturesEnum.LOCATION);
    rootView.setListener(this);

    setContentView(rootView.getAndroidLayoutView());
    locationModelManager = (LocationModelManager) ModelsFactory.createModel(this, AppFeaturesEnum.LOCATION);
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    askForNeededPermissions();
  }

  private void askForNeededPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(
          new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
          PERMISSIONS_REQUEST_CODE);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    isThisActivityInForeground = true;
    detectUserCurrentAddress();
  }

  @Override
  protected void onPause() {
    super.onPause();
    isThisActivityInForeground = false;
  }

  @Override
  public void execute(String detectedText) {
    if (detectedText.toUpperCase().equals("SEND")) {
      textToSpeech.speak("Sending messages to friends ", TextToSpeech.QUEUE_ADD, null);
      locationModelManager.getContacts();
      sendMessages();
    }else{
      textToSpeech.speak("Cannot process command: " + detectedText, TextToSpeech.QUEUE_ADD, null);
    }
  }

  @Override
  public void sendMessages() {
    StringBuilder messageBody = new StringBuilder("Hello,\n");
    messageBody.append("I'm here: ");
    messageBody.append("(").append(currentLocation.getLatitude()).append(",")
        .append(currentLocation.getLongitude()).append(").\n");
    messageBody.append(" Can you come and pick me up please?");
    List<Contact> contacts = locationModelManager.getContacts();

    if (contacts.size() == 0) {
      textToSpeech.speak("Add friends contact info first!", TextToSpeech.QUEUE_ADD, null);
    } else {
      Log.i(LOCATION_SCREEN_TAG, "Sending messages to following contacts: ");
    }

    String message = messageBody.toString();
    for (Contact c : contacts) {
      Log.i(LOCATION_SCREEN_TAG, c.toString());
       sendMessage(c.phoneNumber, message);
    }

  }


  private void detectUserCurrentAddress() {
    try {
      Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
      locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
          if (task.isSuccessful()) {
            currentLocation = task.getResult();

            Address currentAddress = getAdressFromLocation(currentLocation,
                LocationScreenPresenter.this);

            String prettyPrintedAddress = prettyPrintAddress(currentAddress);

            if ((!textToSpeech.isSpeaking()) && (isThisActivityInForeground)) {
              textToSpeech.speak(prettyPrintedAddress, TextToSpeech.QUEUE_ADD, null);
            }

            Log.i(LOCATION_SCREEN_TAG, "Pretty: " + prettyPrintedAddress);

            rootView.displayUserCurrentLocation(prettyPrintedAddress);

          } else {
            Log.e(LOCATION_SCREEN_TAG, "Exception: %s", task.getException());
          }
        }
      });
    } catch (SecurityException e) {
      Log.e(LOCATION_SCREEN_TAG, e.getMessage());
    }
  }

  /**
   * Returns a string where each element from address is separted by comma
   */
  public static String prettyPrintAddress(Address currentAddress) {
    StringBuilder buffer = new StringBuilder();

    int lastAddressLineIndex = currentAddress.getMaxAddressLineIndex();
    for (int i = 0; i <= lastAddressLineIndex; i++) {
      buffer.append(currentAddress.getAddressLine(i)).append(",");
    }

    return buffer.toString();
  }

  /**
   */
  private Address getAdressFromLocation(Location location, Context context) {
    Geocoder geocoder = new Geocoder(context);

    try {
      List<Address> addresses = geocoder
          .getFromLocation(location.getLatitude(), location.getLongitude(), 1);

      if (addresses.size() > 0) {
        //Log.i(Constants.HMS_INFO, "Detected current address: " + address);
        return addresses.get(0);
      }

    } catch (IOException e) {
      e.printStackTrace();

      Log.d(Constants.HMS_INFO, "Couldn't reverse geocode from location: " + location);
    }

    return null;
  }

  @Override
  public void addContact() {
    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    startActivityForResult(intent, PICK_CONTACT_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case PICK_CONTACT_REQUEST:
        if (resultCode == Activity.RESULT_OK) {
          Uri contactData = data.getData();
          Cursor cursorContact = getContentResolver().query(contactData, null, null, null, null);
          if (cursorContact.moveToFirst()) {
            String contactName = cursorContact
                .getString(cursorContact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursorContact.getString(cursorContact.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = cursorContact
                .getString(cursorContact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String contactPhoneNumber = null;

            if ("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
              Cursor cursorPhoneNumbers = this.getContentResolver()
                  .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                      ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null,
                      null);
              while (cursorPhoneNumbers.moveToNext()) {
                contactPhoneNumber = cursorPhoneNumbers.getString(
                    cursorPhoneNumbers
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
              }
              cursorPhoneNumbers.close();
            }

            if (contactPhoneNumber != null) {
              Log.i(LOCATION_SCREEN_TAG,
                  "Picked contact:" + contactName + "-" + contactPhoneNumber);
              locationModelManager.addContact(contactName, contactPhoneNumber);
            }
          }

          cursorContact.close();
        }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {

    if (PERMISSIONS_REQUEST_CODE == requestCode) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) { //ask again
        askForNeededPermissions();
        textToSpeech.speak("The app cannot work without the required permissions!",
            TextToSpeech.QUEUE_ADD, null);
      }
    }
  }

  public void sendMessage(String phoneNo, String msg) {
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNo, null, msg, null, null);
    } catch (Exception ex) {
      Log.i(LOCATION_SCREEN_TAG,
          "Could not send help msg to : " + phoneNo + ".\n Reason: " + ex.getMessage());
      ex.printStackTrace();
    }
  }


}
