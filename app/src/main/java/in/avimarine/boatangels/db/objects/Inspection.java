package in.avimarine.boatangels.db.objects;

import static in.avimarine.boatangels.db.objects.Inspection.StatusEnum.GOOD;

import android.widget.ImageView;
import in.avimarine.boatangels.R;
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


  private String inspectorUid;
  private String inspectorName;
  private String boatUuid;
  private String boatName;
  private String message;
  private Map<String, String> finding;
  private Long inspectionTime;
  private List<Message> discussion;
  private int pointsEarned;
  private StatusEnum status = GOOD;
  public Inspection(String inspectorUid, String inspectorName, String boatUuid,
      String boatName, String message, Map<String, String> finding, Long inspectionTime,
      List<Message> discussion, int pointsEarned,
      StatusEnum status) {
    this.inspectorUid = inspectorUid;
    this.inspectorName = inspectorName;
    this.boatUuid = boatUuid;
    this.boatName = boatName;
    this.message = message;
    this.finding = finding;
    this.inspectionTime = inspectionTime;
    this.discussion = discussion;
    this.pointsEarned = pointsEarned;
    this.status = status;
  }
  public Inspection(){
    //For Firestore
  }

  public String getInspectorUid() {
    return inspectorUid;
  }

  public void setInspectorUid(String inspectorUid) {
    this.inspectorUid = inspectorUid;
  }

  public String getInspectorName() {
    return inspectorName;
  }

  public void setInspectorName(String inspectorName) {
    this.inspectorName = inspectorName;
  }

  public String getBoatUuid() {
    return boatUuid;
  }

  public void setBoatUuid(String boatUuid) {
    this.boatUuid = boatUuid;
  }

  public String getBoatName() {
    return boatName;
  }

  public void setBoatName(String boatName) {
    this.boatName = boatName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, String> getFinding() {
    return finding;
  }

  public void setFinding(Map<String, String> finding) {
    this.finding = finding;
  }

  public Long getInspectionTime() {
    return inspectionTime;
  }

  public void setInspectionTime(Long inspectionTime) {
    this.inspectionTime = inspectionTime;
  }

  public List<Message> getDiscussion() {
    return discussion;
  }

  public void setDiscussion(List<Message> discussion) {
    this.discussion = discussion;
  }

  public int getPointsEarned() {
    return pointsEarned;
  }

  public void setPointsEarned(int pointsEarned) {
    this.pointsEarned = pointsEarned;
  }





  public void setStatus(StatusEnum status) {this.status = status;}
  public StatusEnum getStatus() { return  status;}
  public void setInspectionIcon(ImageView icon){
    switch (status) {
      case GOOD:
        icon.setImageResource(R.drawable.ic_status_good_unselected);
        break;
      case BAD:
        icon.setImageResource(R.drawable.ic_status_average_unselected);
        break;
      case VERY_BAD:
        icon.setImageResource(R.drawable.ic_status_bad_unselected);
        break;
    }
  }
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
  public enum StatusEnum
  {
    GOOD, BAD, VERY_BAD
  }
}
