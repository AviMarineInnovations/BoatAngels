package in.avimarine.boatangels.db.objects;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Date;
import java.util.UUID;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 *
 * This class is a base class for all the DB objects in this application
 */


@IgnoreExtraProperties
public abstract class BaseDbObject {


  public  Long lastUpdateTime;
  public Long firstAddedTime;
  @Exclude
  private UUID _uuid;

  protected BaseDbObject() {
    _uuid = UUID.randomUUID();
  }

  @Override
  public abstract String toString();

  public void setUuid(String uuid) {
    this._uuid = UUID.fromString(uuid);
  }

  public String getUuid() {
    return _uuid.toString();
  }

  @Exclude
  public Date getLastUpdate() {
    if (lastUpdateTime==null)
      return null;
    return new Date(lastUpdateTime);
  }

  @Exclude
  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdateTime = lastUpdate.getTime();
  }

  @Exclude
  public Date getFirstAddedTime() {
    if (firstAddedTime==null)
      return null;
    return new Date(firstAddedTime);
  }

  @Exclude
  public void setFirstAddedTime(Date firstAddedTime) {
    this.firstAddedTime = firstAddedTime.getTime();
  }
}
