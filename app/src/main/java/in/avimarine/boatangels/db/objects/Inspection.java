package in.avimarine.boatangels.db.objects;

import static in.avimarine.boatangels.db.objects.Inspection.StatusEnum.GOOD;
import static in.avimarine.boatangels.db.objects.Inspection.StatusEnum.BAD;
import static in.avimarine.boatangels.db.objects.Inspection.StatusEnum.VERY_BAD;

import android.view.View;
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

  public enum StatusEnum
  {
    GOOD, BAD, VERY_BAD
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
  private StatusEnum status = GOOD;
  private boolean liked = false;



  public void setStatus(StatusEnum status) {this.status = status;}
  public StatusEnum getStatus() { return  status;}
  public void setInspectionSavirityIcon(ImageView icon){
    switch (status) {
      case GOOD:
        icon.setImageResource(R.drawable.ic_unpressed_good_inspection_icon);
        break;
      case BAD:
        icon.setImageResource(R.drawable.ic_unpressed_bad_inspection_icon);
        break;
      case VERY_BAD:
        icon.setImageResource(R.drawable.ic_unpressed_very_bad_inspection_icon);
        break;
    }
  }

  //-------------------------
  public boolean getLiked(){ return this.liked; }
  public void setLikeStatus(boolean liked){
    this.liked = liked;
  }

//  public void displayLikeIcon(View view, ImageView likeIcon)
//  {
//    if(this.liked)
//      likeIcon.setVisibility(view.VISIBLE);
//    else
//      likeIcon.setVisibility(view.INVISIBLE);
//  }

  //-------------------------

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
