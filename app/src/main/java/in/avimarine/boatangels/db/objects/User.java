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
  public final List<String> boats = new ArrayList<>();

  @Override
  public String toString() {
    return "Users{" +
        "name='" + displayName + '\'' +
        ", uid=" + uid +
        ", mail=" + mail +
        ", Phone=" + phone +
        ", firstAddedTime=" + firstJoinTime +
        ", firstAddedTime=" + boats +
        ", lastUpdateTime=" + lastUpdateTime +
        '}';
  }
}
