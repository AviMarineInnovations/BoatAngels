package in.avimarine.boatangels;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import in.avimarine.boatangels.general.LocaleUtils;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 20/01/2018.
 */

public class App extends Application {

  private static final String TAG = "App";
  public void onCreate(){
    super.onCreate();
    LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG,"Config changed");
    LocaleUtils.updateConfig(this, newConfig);
  }
}
