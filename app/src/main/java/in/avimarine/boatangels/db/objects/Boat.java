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


  public final List<String> users = new ArrayList<>();

  @Override
  public String toString() {
    return "Boat{" +
        "_uuid=" + getUuid() +
        ", name='" + name + '\'' +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        ", marina=" + marinaName +
        '}';
  }

}
