package in.avimarine.boatangels.db.objects;

import com.google.firebase.firestore.GeoPoint;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class Boat extends BaseDbObject {

  public String name;
  public GeoPoint location;
  public String marina;

  @Override
  public String toString() {
    return "Boat{" +
        "_uuid=" + getUuid() +
        ", name='" + name + '\'' +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        /*", aviLocation=" + aviLocation +*/
        ", marina=" + marina +
        '}';
  }

}
