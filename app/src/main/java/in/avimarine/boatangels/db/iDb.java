package in.avimarine.boatangels.db;

import in.avimarine.boatangels.db.objects.Boat;
import java.util.UUID;

/**
 * Created by aayaffe on 16/12/2017.
 */

public interface iDb {

  void setBoatQuery(String marina);
  public void addBoat(Boat b);
  public Boat getBoat(UUID uuid);
}
