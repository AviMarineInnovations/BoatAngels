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

  public String displayName;
  public String mail;
  public String phone;
  public String country;
  public Date firstJoinTime;
  public String uid;
  public Date lastUpdateTime;
  public final List<String> boats = new ArrayList<>();

  @Override
  public String toString() {
    return
        "Name =" + displayName + '\n' +
        "uid =" + uid + '\n' +
        "mail=" + mail +'\n' +
        "Phone=" + phone +'\n' +
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


}
