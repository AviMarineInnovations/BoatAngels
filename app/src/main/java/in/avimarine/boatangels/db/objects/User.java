package in.avimarine.boatangels.db.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Motim on 12/25/2017.
 */

public class User extends BaseDbObject {

  public String DisplayName;
  public String Mail;
  public String Phone;
  public String Country;
  public Date FirstJoinTime;
  public String uid;
  public Date LastUpdateTime;
  public final List<String> boats = new ArrayList<>();

  @Override
  public String toString() {
    return "Users{" +
        "name='" + DisplayName + '\'' +
        ", uid=" + uid +
        ", Mail=" + Mail +
        ", Phone=" + Phone +
        ", firstAddedTime=" + FirstJoinTime +
        ", firstAddedTime=" + boats +
        ", lastUpdateTime=" + LastUpdateTime +
        '}';
  }
}
