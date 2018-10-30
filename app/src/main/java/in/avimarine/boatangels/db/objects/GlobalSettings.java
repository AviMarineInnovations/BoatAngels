package in.avimarine.boatangels.db.objects;

import java.util.Date;


/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */


public class GlobalSettings extends BaseDbObject {

  private Long compatibleVersion;

  public GlobalSettings(Long compatibleVersion) {
    this.firstAddedTime = new Date().getTime();
    this.lastUpdateTime = firstAddedTime;
    this.compatibleVersion = compatibleVersion;
  }

  public Long getCompatibleVersion() {
    return compatibleVersion;
  }
  public void setCompatibleVersion(Long compatibleVersion) {
    this.compatibleVersion = compatibleVersion;
  }

  @Override
  public String toString() {
    return "GlobalSettings{" +
        "compatibleVersion=" + compatibleVersion +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        '}';
  }
}
