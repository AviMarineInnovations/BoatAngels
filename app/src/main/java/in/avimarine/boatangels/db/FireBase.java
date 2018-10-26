package in.avimarine.boatangels.db;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
//import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import in.avimarine.boatangels.BuildConfig;
import in.avimarine.boatangels.GlideApp;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.GlobalSettings;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.geographical.Weather;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class FireBase implements iDb {

  private static final String TAG = "FireBase";
  private final FirebaseFirestore mFirestore;
  private final FirebaseStorage storage;
  private static User currentUser;


  public FireBase() {
    mFirestore = FirebaseFirestore.getInstance();
    storage = FirebaseStorage.getInstance();
    if (BuildConfig.DEBUG) {
      FirebaseFirestore.setLoggingEnabled(true);
    } else {
      FirebaseFirestore.setLoggingEnabled(false);
    }
  }

  @Override
  public void addBoat(Boat b) {
    CollectionReference cr = mFirestore.collection("boats");
    DocumentReference dr = cr.document(b.getUuid());
    dr.set(b);
  }

//  Reference paths and names can contain any sequence of valid Unicode characters, but certain restrictions are imposed including:
//  Total length of reference.fullPath must be between 1 and 1024 bytes when UTF-8 encoded.
//  No Carriage Return or Line Feed characters.
//  Avoid using #, [, ], *, or ?, as these do not work well with other tools such as the Firebase Realtime Database or gsutil.
//  @Override
//  public void getPicture(String path, OnSuccessListener<byte[]> osl, OnFailureListener ofl, long maxSize){
//    // Points to the root reference
//    StorageReference storageRef = storage.getReference();
//    StorageReference imageRef = storageRef.child(path);
//    final long ONE_MEGABYTE = 1024 * 1024;
//    imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(osl).addOnFailureListener(ofl);
//  }

  public void loadImgToImageView(Context c, ImageView iv, String path,@DrawableRes int loadingImg ,@DrawableRes int errorImg){
    StorageReference storageRef = storage.getReference();
    StorageReference imageRef = storageRef.child(path);
    final long ONE_MEGABYTE = 1024 * 1024;


    if (isValidContextForGlide(c)) {
      // Load the image using Glide
      GlideApp.with(c)
          .load(imageRef)
          .error(errorImg)
          .placeholder(loadingImg)
          .into(iv);
    }
  }
  public static boolean isValidContextForGlide(final Context context) {
    if (context == null) {
      return false;
    }
    if (context instanceof Activity) {
      final Activity activity = (Activity) context;
      if(Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2){
        if (activity.isDestroyed() || activity.isFinishing()) {
          return false;
        }
      }
      else {
        if (activity.isChangingConfigurations() || activity.isFinishing()) {
          return false;
        }
      }
    }
    return true;
  }
  @Override
  public void getBoat(String uuid, OnCompleteListener<DocumentSnapshot> listener) {
    mFirestore.collection("boats").document(uuid).get().addOnCompleteListener(listener);
  }

  @Override
  public void getMarina(String uuid, OnCompleteListener<DocumentSnapshot> listener) {
    mFirestore.collection("marinas").document(uuid).get().addOnCompleteListener(listener);
  }


  @Override
  public void getBoatsInMarina(String marina, OnCompleteListener<QuerySnapshot> listener) {
    mFirestore.collection("boats").whereEqualTo("marinaName", marina).get()
        .addOnCompleteListener(listener);
  }

  @Override
  public void getBoats(OnCompleteListener<QuerySnapshot> listener) {
    mFirestore.collection("boats").get().addOnCompleteListener(listener);
  }


  @Override
  public void getInspection(String uuid, OnCompleteListener<DocumentSnapshot> listener) {
    mFirestore.collection("inspections").document(uuid).get().addOnCompleteListener(listener);
  }

  @Override
  public void getLatestInspection(String boatUuid, OnCompleteListener<QuerySnapshot> listener) {
    mFirestore.collection("inspections").whereEqualTo("boatUuid", boatUuid).orderBy("inspectionTime",
        Direction.DESCENDING).limit(1).get().addOnCompleteListener(listener);
  }

  @Override
  public void addInspection(Inspection i) {
    mFirestore.collection("inspections").document(i.getUuid()).set(i);
  }

  @Override
  public void setUser(User user) {
    mFirestore.collection("users").document(user.getUid()).set(user);
  }

  @Override
  public void getUser(String uid, OnCompleteListener<DocumentSnapshot> listener) {
    mFirestore.collection("users").document(uid).get().addOnCompleteListener(listener);
  }

  @Override
  public void getMarinasInCountry(String country, OnCompleteListener<QuerySnapshot> listener) {
    mFirestore.collection("marinas").whereEqualTo("country", country).get()
        .addOnCompleteListener(listener);
  }

  @Override
  public void addMarina(Marina m) {
    mFirestore.collection("marinas").document(m.getUuid()).set(m);
  }

  @Override
  public void setCurrentUser(User u) {
    currentUser = u;
  }

  @Override
  public User getCurrentUser() {
    return currentUser;
  }

  @Override
  public void updateWeather(String uuid, Weather w) {
    mFirestore.collection("marinas").document(uuid).update("weather", w);
  }

  @Override
  public void getSupportedVersion(OnCompleteListener<DocumentSnapshot> listener) {
    mFirestore.collection("globalSettings").document("versions").get().addOnCompleteListener(listener);
  }

  @Override
  public void setSupportedVersion(long version) {
    mFirestore.collection("globalSettings").document("versions").set(new GlobalSettings(version));
  }
}
