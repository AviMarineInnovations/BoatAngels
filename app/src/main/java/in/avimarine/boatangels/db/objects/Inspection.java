package in.avimarine.boatangels.db.objects;

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
  public String inspectorUid;
  public String boatUid;
  public String boatName;
  public String message;
  public Map<String, String> finding;
  public Long inspectionTime;
  public List<Message> discussion;

  @Override
  public String toString() {
    return "Inspection{" +
        "inspectorUid='" + inspectorUid + '\'' +
        ", lastUpdateTime=" + lastUpdateTime +
        ", boatUid='" + boatUid + '\'' +
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
