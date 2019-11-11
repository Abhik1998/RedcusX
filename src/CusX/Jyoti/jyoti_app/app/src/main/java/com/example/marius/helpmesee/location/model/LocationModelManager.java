package com.example.marius.helpmesee.location.model;

import com.example.marius.helpmesee.app_logic.MvpModel;
import java.util.List;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public interface LocationModelManager  extends MvpModel{
  void addContact(String contactName, String phoneNumber);

  List<Contact> getContacts();
}
