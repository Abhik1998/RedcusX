package com.example.marius.helpmesee.text_recognition;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */
public class TextRecPresenter extends TextRecListener {
  private TextRecView rootView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    rootView = new TextRecViewImpl(this, null);
    setContentView(rootView.getAndroidLayoutView());
  }

  @Override
  public void execute(String detectedText) {

  }
}
