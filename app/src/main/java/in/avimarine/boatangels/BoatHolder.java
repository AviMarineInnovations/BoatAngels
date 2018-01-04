package in.avimarine.boatangels;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.Date;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 23/12/2017.
 * This class is used to hold an inspection for a the inspection list recycler view
 */

public class BoatHolder extends RecyclerView.ViewHolder {
  private final TextView mNameField;
  private final TextView mSumOfferPoints;
  private final TextView mClubNameField;
  private final ImageView mStatusImage;


  public BoatHolder(View itemView) {
    super(itemView);
    mSumOfferPoints = itemView.findViewById(R.id.sum_offer_points);
    mNameField = itemView.findViewById(R.id.boat_name_tv);
    mClubNameField = itemView.findViewById(R.id.club_name_tv);
    mStatusImage = itemView.findViewById(R.id.status_iv);
  }

  public void bind(Boat b) {
    setName(b.name);
    //DateUtils.getRelativeTimeSpanString(b.lastInspectionDate)
    String sumOffer = Integer.toString(b.offerPoint);
    setClubName(b.clubName);
    setmSumOfferPoints("offer: " + sumOffer + " points");
    setClubName(b.clubName);
    setStatus(getStatus(b.lastInspectionDate));
  }

  private int getStatus(Long lastInspectionDate) {
    if (lastInspectionDate == null)
      return 2;
    Date d = new Date(lastInspectionDate);
    int diff = GeneralUtils.getDaysDifference(d,new Date());
    if (diff<=1){
      return 0;
    } else if(diff<=5){
      return 1;
    } else
      return 2;
  }

  private void setName(String name) {
    mNameField.setText(name);
  }
  private void setmSumOfferPoints(String offerPoint) {
    mSumOfferPoints.setText(offerPoint);
  }

  private void setClubName(String text) {
    mClubNameField.setText(text);
  }

  private void setStatus(int status) {
    switch (status) {
      case 0:
        mStatusImage.setImageResource(R.drawable.ic_traffic_green_24px);
        break;
      case 1:
        mStatusImage.setImageResource(R.drawable.ic_traffic_yellow_24px);
        break;
      case 2:
        mStatusImage.setImageResource(R.drawable.ic_traffic_red_24px);
        break;
    }
  }



}