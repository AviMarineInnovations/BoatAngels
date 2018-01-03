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
  public int yachtiePoint = 100;
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
        "yachtiePoint: " + yachtiePoint + "\n" +
        "lastInspectionDate: " + lastInspectionDate
        ;
  }

  public String getName() {
    return name;
  }

  public String getModel() {
    return model;
  }

  public int getOfferPoint() {
    return offerPoint;
  }

  public GeoPoint getLocation() {
    return location;
  }

  public String getMarinaUuid() {
    return marinaUuid;
  }

  public String getMarinaName() {
    return marinaName;
  }

  public List<String> getUsers() {
    return users;
  }

  public String getClubName() {
    return clubName;
  }

  public String getClubUuid() {
    return clubUuid;
  }

  public int getYachtiePoint() {
    return yachtiePoint;
  }

  public void setYachtiePoint(int yachtiePoint) {
    this.yachtiePoint = yachtiePoint;
  }

  public Long getLastInspectionDate() {
    return lastInspectionDate;
  }
}
