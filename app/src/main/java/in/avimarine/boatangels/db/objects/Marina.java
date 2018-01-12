package in.avimarine.boatangels.db.objects;

import com.google.firebase.firestore.GeoPoint;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 18/12/2017.
 *
 * This Class defines an anchorage in the world
 */

public class Marina extends BaseDbObject{

  public String name;
  public String country;
  public GeoPoint location;

  @Override
  public String toString() {
    return "Marina{" +
        "name='" + name + '\'' +
        ", location=" + location +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        '}';
  }

  public GeoPoint getLocation() {
    return location;
  }
}
