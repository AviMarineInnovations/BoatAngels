package in.avimarine.boatangels;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import in.avimarine.boatangels.general.LocaleUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
//import io.fabric.sdk.android.Fabric;

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
    FirebaseApp.initializeApp(this);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    Log.d(TAG,"Config changed");
    LocaleUtils.updateConfig(this, newConfig);
  }
}
