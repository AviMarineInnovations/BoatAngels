package in.avimarine.boatangels.activities;

import android.support.v7.app.AppCompatActivity;
import in.avimarine.boatangels.general.LocaleUtils;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 20/01/2018.
 */

public class BaseActivity extends AppCompatActivity {
  public BaseActivity() {
    super();
    LocaleUtils.updateConfig(this);
  }
}