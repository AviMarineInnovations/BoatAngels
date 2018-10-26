package in.avimarine.boatangels.db.objects;

import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class Boat extends BaseDbObject {

  private String name;
  private String model;
  private GeoPoint location;
  private String marinaUuid;
  private String marinaName;
  private int offerPoint = 0;
  private String clubName;
  private String clubUuid;
  private Long lastInspectionDate;
  private String photoName;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  private String code;


  public List<String> users = new ArrayList<>();


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public GeoPoint getLocation() {
    return location;
  }

  public void setLocation(GeoPoint location) {
    this.location = location;
  }

  public String getMarinaUuid() {
    return marinaUuid;
  }

  public void setMarinaUuid(String marinaUuid) {
    this.marinaUuid = marinaUuid;
  }

  public String getMarinaName() {
    return marinaName;
  }

  public void setMarinaName(String marinaName) {
    this.marinaName = marinaName;
  }

  public int getOfferPoint() {
    return offerPoint;
  }

  public void setOfferPoint(int offerPoint) {
    this.offerPoint = offerPoint;
  }

  public List<String> getUsers() {
    return users;
  }
  public void setUsers(List<String> users) {
    this.users = users;
  }

  public String getClubName() {
    return clubName;
  }

  public void setClubName(String clubName) {
    this.clubName = clubName;
  }

  public String getClubUuid() {
    return clubUuid;
  }

  public void setClubUuid(String clubUuid) {
    this.clubUuid = clubUuid;
  }

  public Long getLastInspectionDate() {
    return lastInspectionDate;
  }

  public void setLastInspectionDate(Long lastInspectionDate) {
    this.lastInspectionDate = lastInspectionDate;
  }

  public String getPhotoName() {
    return photoName;
  }

  public void setPhotoName(String photoName) {
    this.photoName = photoName;
  }


  public static String generateAccessCode() {
    Random rand = new Random();
    int n = rand.nextInt(899999) + 100000;
    return String.valueOf(n);
  }


  @Override
  public String toString() {
    return name;
  }
}
