package com.example.marius.helpmesee.util;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.example.marius.helpmesee.app_logic.CommandProcessor;
import com.example.marius.helpmesee.app_logic.Constants;
import com.example.marius.helpmesee.app_logic.SpecificScreenVoiceCommand;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 *   Provides the default implementation for speech input button. All custom listeners of this app must subclass it
 */

/**
 * Each presenter/activity of Hms should have the following methods
 */
public abstract class HmsActivity extends AppCompatActivity implements  TextToSpeech.OnInitListener,
    SpecificScreenVoiceCommand {
  protected TextToSpeech textToSpeech;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    textToSpeech = new TextToSpeech(this, this);
  }

  public void startRecording() {
    try {
      startActivityForResult(Constants.SPEECH_INTENT, Constants.SPEECH_INPUT_CODE);
    } catch (Exception e) {
      Log.e(Constants.HMS_INFO,
          "Speech recognition not supported on this device!\n Method: startRecording()");
      e.printStackTrace();
    }
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case Constants.SPEECH_INPUT_CODE:
        if ((resultCode == RESULT_OK) && (data != null)) {
          ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          String detectedText = text.get(0);
          Log.i(Constants.HMS_INFO, "Detected text:" + detectedText);

          CommandProcessor.getInstance().processCommand(detectedText, this);
        }else{
          textToSpeech.speak("Could not interpret voice command!", TextToSpeech.QUEUE_ADD, null);
        }

        break;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (textToSpeech != null) {
      textToSpeech.stop();
      textToSpeech.shutdown();
    }
  }

  @Override
  /**
   * A TextToSpeech instance can only be used to synthesize text once it has completed its initialization.
   * Implement the TextToSpeech.OnInitListener to be notified of the completion of the initialization.
   */
  public void onInit(int status) {
    if (status == TextToSpeech.SUCCESS){
      textToSpeech.setLanguage(Locale.ENGLISH);
      //can set pitch and rate here as well
      textToSpeech.setSpeechRate(0.9f);
    }else{
      Log.e(Constants.HMS_INFO, "Text to speech instance initialization failed!" );
      Toast.makeText(this,"Text to speech instance initialization failed!", Toast.LENGTH_SHORT).show();
    }
  }
}