package com.example.marius.helpmesee.tests;

import static org.mockito.Mockito.when;

import android.location.Address;
import com.example.marius.helpmesee.location.presenter.LocationScreenPresenter;
import com.google.android.gms.location.LocationResult;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class InstructionProvidedTests {

  @Mock
  Address currentAddress;

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  //input: Biblioteca Centrala a Universitatii Politehnica, Bulevardul Vasile Pârvan 2, Timișoara, Romania
  @Test
  public void testPrettyPrint() {
    when(currentAddress.getAddressLine(0))
        .thenReturn("Biblioteca Centrala a Universitatii Politehnica");
    when(currentAddress.getAddressLine(1)).thenReturn("Bulevardul Vasile Pârvan 2");
    when(currentAddress.getAddressLine(2)).thenReturn("Timișoara");
    when(currentAddress.getAddressLine(3)).thenReturn("Romania");

    when(currentAddress.getMaxAddressLineIndex()).thenReturn(3);

    String s = LocationScreenPresenter.prettyPrintAddress(currentAddress);

    System.out.println(s);

  }

}
