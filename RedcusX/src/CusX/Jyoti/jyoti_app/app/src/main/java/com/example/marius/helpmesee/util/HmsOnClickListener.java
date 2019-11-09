package com.example.marius.helpmesee.util;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by Marius Olariu <mariuslucian.olariu@gmail.com>
 */

/**
 * For the moment there is no need to use this since most devices come with TalkBack
 *
 */

public abstract class HmsOnClickListener implements OnClickListener {

  private final int SINGLE_TAP = 1;
  private final int DOUBLE_TAP = 2;
  private final int CLICK_DELAY_THRESHOLD = 500; //ms


  private boolean firstTapOccured;
  private Message message;
  private View currentView;

  //performance warning
  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg)
    {
      super.handleMessage(msg);

      switch(msg.what)
      {
        case SINGLE_TAP:
          //Log.i(Constants.HMS_INFO, "single tap");
          firstTapOccured = false;
          onSingleClick(currentView);

          break;
        case DOUBLE_TAP:
          //Log.i(Constants.HMS_INFO, "double tap");
          onDoubleClick(currentView);
          break;
      }
    }

  };

  @Override
  public void onClick(View view) {
    onClickSynchronized(view);
  }

  private synchronized void onClickSynchronized(View view) {

    //view might be different for a ListView
    if (currentView != null){
     currentView = view;
    }

    if (!firstTapOccured){
      firstTapOccured = true;
      message = message == null? new Message() : mHandler.obtainMessage();
      //mHandler.removeMessages(SINGLE_TAP);
      message.what = SINGLE_TAP;
      mHandler.sendMessageDelayed(message, CLICK_DELAY_THRESHOLD);
    }else{
      mHandler.removeMessages(SINGLE_TAP); //second tap occured before first one was processed
      message = mHandler.obtainMessage();
      message.what=DOUBLE_TAP;
      mHandler.sendMessageAtFrontOfQueue(message);
      firstTapOccured=false;

    }

  }

  public abstract void onSingleClick(View view);

  public abstract void onDoubleClick(View view);
}
