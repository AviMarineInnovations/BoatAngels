package in.avimarine.boatangels.db.objects;

import com.google.firebase.database.Exclude;
import in.avimarine.boatangels.geographical.AviLocation;
import java.util.Date;
import java.util.UUID;

/**
 * Created by aayaffe on 16/12/2017.
 */

public class Boat {
  @Exclude
  private UUID _uuid;
  public String name;
  public Long lastUpdateTime;
  public Long firstAddedTime;
  public AviLocation aviLocation;
  public String marina;

  @Override
  public String toString() {
    return "Boat{" +
        "_uuid=" + _uuid +
        ", name='" + name + '\'' +
        ", lastUpdateTime=" + lastUpdateTime +
        ", firstAddedTime=" + firstAddedTime +
        ", aviLocation=" + aviLocation +
        ", marina=" + marina +
        '}';
  }

  public Boat(){
    _uuid = UUID.randomUUID();
  }
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
