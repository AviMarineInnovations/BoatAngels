package in.avimarine.boatangels.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.general.DialogUtils;
import in.avimarine.boatangels.general.Versioning;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 11/10/2018.
 */
public class SplashActivity extends AppCompatActivity {

  private static final String TAG = "SplashActivity";
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Versioning versioning = new Versioning(this);
    versioning.getSupportedVersion(result -> {
      if (versioning.getInstalledVersionCode()<result)
      {
        Log.d(TAG,"Installed version, "+versioning.getInstalledVersionCode()+" is earlier than the supported version: " + result);
        alertOnUnsupportedVersion();
      } else {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }

  private void alertOnUnsupportedVersion() {
    Log.d(TAG, "Version not supported, starting dialog");
    Dialog d = DialogUtils.createDialog(SplashActivity.this, R.string.version_not_supported_dialog_title,
        R.string.version_not_supported_dialog_message, (dialog, which) -> {
          final String appPackageName = SplashActivity.this.getPackageName();
          try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                .parse("market://details?id=" + appPackageName)));
          } catch (android.content.ActivityNotFoundException e) {
            Log.e(TAG,"Error FirebaseDB OnConnect",e);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
          }
        }, (dialog, which) -> finish());
    d.show();
  }
}
