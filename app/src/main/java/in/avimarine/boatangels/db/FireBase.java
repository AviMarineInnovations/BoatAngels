package in.avimarine.boatangels.db;

import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Marina;
import java.util.HashSet;
import java.util.UUID;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class FireBase implements iDb {

  private static final String TAG = "FireBase";
  private final FirebaseFirestore mFirestore;
  private final HashSet<Boat> boats = new HashSet<>();


  public FireBase() {
      mFirestore = FirebaseFirestore.getInstance();
      FirebaseFirestore.setLoggingEnabled(true);
  }

  @Override
  public void setBoatQuery(String marina){
    mFirestore.collection("boats").whereEqualTo("marina", marina).addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot value,
          @Nullable FirebaseFirestoreException e) {
        if (e != null) {
          Log.w(TAG, "Listen failed.", e);
          return;
        }
        if (value==null)
        {
          Log.w(TAG, "Listen failed.");
          return;
        }
        for (DocumentSnapshot doc : value) {
          if (doc.get("name") != null) {
            try {
              doc.toObject(Boat.class);
            }catch(Exception ex){
              Log.e(TAG,"Get boats error.",ex);
            }
            boats.add(doc.toObject(Boat.class));
          }
        }
      }
    });
  }

  @Override
  public void addBoat(Boat b) {
    mFirestore.collection("boats").document(b.getUuid()).set(b);
  }

    @Override
    @Nullable
    public Boat getBoat (UUID uuid){
      for (Boat b: boats){
        if (b.getUuid().equals(uuid.toString())){
          return b;
        }
      }
      return null;
    }


  @Override
  public void getBoatsInMarina(String marina, OnCompleteListener<QuerySnapshot> listener ) {
    mFirestore.collection("boats").whereEqualTo("marinaName", marina).get().addOnCompleteListener(listener);
  }

  @Override
  public void getInspection(String uuid, OnCompleteListener<DocumentSnapshot> listener ) {
    mFirestore.collection("inspections").document(uuid).get().addOnCompleteListener(listener);
  }

  @Override
  public void addInspection(Inspection i) {
    mFirestore.collection("inspections").document(i.getUuid()).set(i);
  }

  @Override
  public void getMarinasInCountry(String country, OnCompleteListener<QuerySnapshot> listener) {
    mFirestore.collection("marinas").whereEqualTo("country", country).get().addOnCompleteListener(listener);
  }

  @Override
  public void addMarina(Marina m) {
    mFirestore.collection("marinas").document(m.getUuid()).set(m);
  }
}
