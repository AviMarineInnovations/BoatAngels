package in.avimarine.boatangels.general;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 19/03/2016.
 */
public class Versioning {

  private static final String TAG = "Versioning";
  private Context c;
  private iDb db;

  public Versioning(Context c) {
    this.c = c;
    db = new FireBase();
  }

  public void getSupportedVersion(OnGetSupportedVersionListener<Long> listener) {
    try {
      db.getSupportedVersion(task -> {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult();
          long supportedVersion;
          if (!document.exists()) {
            Log.d(TAG,"No supported version value found in DB");
            supportedVersion = getInstalledVersionCode();
            db.setSupportedVersion(supportedVersion);
            Log.d(TAG,"Setting the minimal supported version to current version: " + supportedVersion);
          } else {
            supportedVersion = document.getLong("compatibleVersion");
            if (supportedVersion<0){
              supportedVersion = 0;
            }
          }
          listener.onComplete(supportedVersion);
        }
        else {
          Log.e(TAG,"Unable to get version ",task.getException());
        }
      });
    } catch (Exception e) {
      Log.e(TAG, "error getting supported version", e);
    }
  }

  public int getInstalledVersionCode() {
    try {
      PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
      return pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      Log.d(TAG, "Error retreiving versioncode: ", e);
    }
    return -1;
  }
}
