package in.avimarine.boatangels.db;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.geographical.Weather;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public interface iDb {

  void addBoat(Boat b);

  void getBoat(String uuid, OnCompleteListener<DocumentSnapshot> listener);

  void getMarina(String uuid, OnCompleteListener<DocumentSnapshot> listener);

  void getBoatsInMarina(String marina, OnCompleteListener<QuerySnapshot> listener);

  /**
   * This method gets all the boats in the DB
   * This can be very intensive on resources!
   * @param listener - listener to be called on completion of query
   */
  void getBoats(OnCompleteListener<QuerySnapshot> listener);

  void getInspection(String uuid, OnCompleteListener<DocumentSnapshot> listener);

  void getLatestInspection(String boatUuid, OnCompleteListener<QuerySnapshot> listener);

  void addInspection(Inspection inspection);

  void setUser(User user);

  void getUser(String uid, OnCompleteListener<DocumentSnapshot> listener);

  void getMarinasInCountry(String country, OnCompleteListener<QuerySnapshot> listener);

  void addMarina(Marina m);

  void setCurrentUser(User u);

  User getCurrentUser();


  void updateWeather(String uuid, Weather w);
}
