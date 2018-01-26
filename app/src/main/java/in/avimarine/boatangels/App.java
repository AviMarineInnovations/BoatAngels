package in.avimarine.boatangels;

import android.app.Application;
import android.content.res.Configuration;
import in.avimarine.boatangels.general.LocaleUtils;
import java.util.Locale;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 20/01/2018.
 */

public class App extends Application {
  public void onCreate(){
    super.onCreate();

    LocaleUtils.setLocale(new Locale("iw"));
    LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    LocaleUtils.updateConfig(this, newConfig);
  }
}
