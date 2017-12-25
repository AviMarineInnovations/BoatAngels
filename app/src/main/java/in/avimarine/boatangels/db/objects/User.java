package in.avimarine.boatangels.db.objects;

/**
 * Created by Motim on 12/25/2017.
 */

public class User extends BaseDbObject {

  public String DisplayName;
  public String Mail;
  public String Phone;
  public String Country;
  public String FirstJoinTime;


  @Override
  public String toString() {
    return "Users{" +
        "name='" + DisplayName + '\'' +
        ", Mail=" + Mail +
        ", Phone=" + Phone +
        ", firstAddedTime=" + FirstJoinTime +
        ", lastUpdateTime=" + lastUpdateTime +
        '}';
  }
}
