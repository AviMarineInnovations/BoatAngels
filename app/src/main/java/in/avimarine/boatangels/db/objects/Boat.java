package in.avimarine.boatangels.db.objects;

import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.List;


/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class Boat extends BaseDbObject {

  public String name;
  public String model;
  public GeoPoint location;
  public String marinaUuid;
  public String marinaName;
  public int offerPoint = 0;
  public final List<String> users = new ArrayList<>();
  public String clubName;
  public String clubUuid;
  public Long lastInspectionDate;

  @Override
  public String toString() {
    return
        "uuid: " + getUuid() + "\n"+
        "Name: " + name +  "\n" +

        "lastUpdateTime: " + lastUpdateTime +"\n"+
        "firstAddedTime: " + firstAddedTime +"\n"+
        "Marina: " + marinaName+"\n"+
        "model: " + model+"\n"+
        "location: " + location +"\n"+
        "ClubName: " + clubName+"\n"+
        "OfferPoint: " + offerPoint+"\n"+
        "lastInspectionDate: " + lastInspectionDate
        ;
  }

  public String getName() {
    return name;
  }

  public int getOfferPoint() {
    return offerPoint;
  }

}
