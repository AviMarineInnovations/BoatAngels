package in.avimarine.boatangels.db.objects;

import static in.avimarine.boatangels.db.objects.Inspection.StatusEnum.GOOD;
import static in.avimarine.boatangels.db.objects.Inspection.StatusEnum.NotInspected;

import java.util.List;
import java.util.Map;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 *
 * Inspection class represent a single inspection made to a boat by an inspector
 */



public class Inspection extends BaseDbObject {

  public enum StatusEnum
  {
    GOOD, BAD, VERY_BAD   //add to list inspectionActivity field of ""
  }

  public String inspectorUid;
  public String inspectorName;
  public String boatUuid;
  public String boatName;
  public String message;
  public Map<String, String> finding;
  public Long inspectionTime;
  public List<Message> discussion;
  public int pointsEarned;
  private StatusEnum status;  

  public StatusEnum getStatus() { return this.status; }

  @Override
  public String toString() {
    return "Inspection{" +
        "inspectorUid='" + inspectorUid + '\'' +
        "getPoint='" + pointsEarned + '\'' +
        ", lastUpdateTime=" + lastUpdateTime +
        ", boatUuid='" + boatUuid + '\'' +
        ", boatName='" + boatName + '\'' +
        ", message='" + message + '\'' +
        ", firstAddedTime=" + firstAddedTime +
        ", finding=" + finding +
        ", _uuid=" + getUuid() +
        ", inspectionTime=" + inspectionTime +
        ", discussion=" + discussion +
        '}';
  }

}
