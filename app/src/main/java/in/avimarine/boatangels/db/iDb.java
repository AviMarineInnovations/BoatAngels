package in.avimarine.boatangels.db;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;
import java.util.UUID;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public interface iDb {

  void setBoatQuery(String marina);
  void addBoat(Boat b);
  Boat getBoat(UUID uuid);

  void getBoatsInMarina(String marina, OnCompleteListener<QuerySnapshot> listener);

  void addInspection(Inspection inspection);

  void addUser(User user);

  void getUser(String uid, OnCompleteListener<DocumentSnapshot> listener);

  void getMarinasInCountry(String country, OnCompleteListener<QuerySnapshot> listener);

  void addMarina(Marina m);
}
