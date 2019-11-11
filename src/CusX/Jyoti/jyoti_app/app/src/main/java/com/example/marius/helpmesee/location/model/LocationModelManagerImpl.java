package com.example.marius.helpmesee.location.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class LocationModelManagerImpl implements LocationModelManager {
  private SharedPreferences prefs;
  private Context context;

  public LocationModelManagerImpl(Context context){
    this.context = context;
    prefs = PreferenceManager.getDefaultSharedPreferences(context);
  }

  @Override
  public void addContact(String contactName, String phoneNumber) {
    prefs.edit().putString(contactName, phoneNumber).apply();
  }

  @Override
  public List<Contact> getContacts() {
    Map<String, ?> friendsCollection = prefs.getAll();
    ArrayList<Contact> friendsList = new ArrayList<>();

    for (Entry<String, ?> contact: friendsCollection.entrySet()) {
      friendsList.add(new Contact(contact.getKey(), (String)contact.getValue()));
    }

    return friendsList;
  }
}
