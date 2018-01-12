package in.avimarine.boatangels.db;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class FireBase implements iDb {

  private static final String TAG = "FireBase";
  private final FirebaseFirestore mFirestore;
  private static User currentUser;


  public FireBase() {
      mFirestore = FirebaseFirestore.getInstance();
      FirebaseFirestore.setLoggingEnabled(true);
  }

  @Override
  public void addBoat(Boat b) {
    CollectionReference cr = mFirestore.collection("boats");
    DocumentReference dr = cr.document(b.getUuid());
    dr.set(b);
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
  public void getBoatsInMarina(String marina, OnCompleteListener<QuerySnapshot> listener ) {
    mFirestore.collection("boats").whereEqualTo("marinaName", marina).get().addOnCompleteListener(listener);
  }

  @Override
  public void getBoats(OnCompleteListener<QuerySnapshot> listener){
    mFirestore.collection("boats").get().addOnCompleteListener(listener);
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
  public void addUser(User user) {
      mFirestore.collection("users").document(user.getUid()).set(user);
  }
  @Override
  public void getUser(String uid, OnCompleteListener<DocumentSnapshot> listener) {
    mFirestore.collection("users").document(uid).get().addOnCompleteListener(listener);
  }

  @Override
  public void getMarinasInCountry(String country, OnCompleteListener<QuerySnapshot> listener) {
    mFirestore.collection("marinas").whereEqualTo("country", country).get().addOnCompleteListener(listener);
  }
  @Override
  public void addMarina(Marina m) {
    mFirestore.collection("marinas").document(m.getUuid()).set(m);
  }

  @Override
  public void setCurrentUser(User u){
      currentUser = u;
  }
  @Override
  public User getCurrentUser(){
    return currentUser;
  }

}
