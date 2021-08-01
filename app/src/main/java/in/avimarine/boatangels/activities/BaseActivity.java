package in.avimarine.boatangels.activities;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import in.avimarine.boatangels.general.LocaleUtils;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 20/01/2018.
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
  public BaseActivity() {
    super();
    LocaleUtils.updateConfig(this);
  }
}