package in.avimarine.boatangels.db.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by Motim on 25/12/2017.
 *
 * This Class defines a registered user in the system
 */

public class User extends BaseDbObject {

  private String displayName;
  private String mail;
  private String phone;
  private String country;
  private int yachtiePoint = 100;
  private Date firstJoinTime;
  private String uid;



  private List<String> boats = new ArrayList<>();

  @Override
  public String toString() {
    return
        "Name =" + displayName + '\n' +
        "uid =" + uid + '\n' +
        "mail=" + mail +'\n' +
        "Phone=" + phone +'\n' +
        "yachtiePoint=" + yachtiePoint +'\n' +
        "firstAddedTime=" + firstJoinTime +'\n' +
        "boats=" + boats +'\n'+
        "lastUpdateTime=" + lastUpdateTime
        ;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getMail() {
    return mail;
  }

  public String getPhone() {
    return phone;
  }

  public int getYachtiePoint() {
    return yachtiePoint;
  }

  public String getCountry() {
    return country;
  }

  public Date getFirstJoinTime() {
    return firstJoinTime;
  }

  public String getUid() {
    return uid;
  }

  public List<String> getBoats() {
    return boats;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setYachtiePoint(int yachtiePoint) {
    this.yachtiePoint = yachtiePoint;
  }

  public void setFirstJoinTime(Date firstJoinTime) {
    this.firstJoinTime = firstJoinTime;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
  public void setBoats(List<String> boats) {
    this.boats = boats;
  }

}
