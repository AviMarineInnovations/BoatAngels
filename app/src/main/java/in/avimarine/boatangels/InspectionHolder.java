package in.avimarine.boatangels;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView; //pazit
import android.widget.TextView;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.db.objects.Inspection.StatusEnum;
import java.util.Date;
/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 23/12/2017.
 * This class is used to hold an inspection for a the inspection list recycler view
 */

public class InspectionHolder extends RecyclerView.ViewHolder {
  private final TextView mDateField;
  private final TextView mTextField;
  private final ImageView mstatusIcon;


  public InspectionHolder(View itemView) {
    super(itemView);
    mDateField = itemView.findViewById(R.id.date_time);
    mTextField = itemView.findViewById(R.id.inspection_text);
    mstatusIcon = itemView.findViewById(R.id.status_iv); //TODO: find how to define the R.id.inspection_icon
  }

  public void bind(Inspection i) {
    Date date = new Date(i.inspectionTime);
    setDate(DateFormat.format("dd.MM.yyyy HH:mm", date).toString());
    setText(i.message);
    setIconColour(i.getStatus()); //TODO
  }

  private void setDate(String name) {
    mDateField.setText(name);
  }

  private void setText(String text) {
    mTextField.setText(text);
  }

  private void setIconColour(StatusEnum inspectionStatus) //TODO:set Icon Image in different colours
  {
    switch (inspectionStatus) {
      case NotInspected:
        mstatusIcon.setImageResource(R.drawable.ic_traffic_red_24px); //green Icon
        break;
      case Good:
       mstatusIcon.setImageResource(R.drawable.ic_traffic_green_24px); //green Icon
        break;
      case Bad:
       mstatusIcon.setImageResource(R.drawable.ic_traffic_yellow_24px); //orange Icon
        break;
      case VeryBad:
       mstatusIcon.setImageResource(R.drawable.ic_traffic_red_24px); //red Icon
        break;
    }

  }


}


/*
TODO:
  0.Add to Inspection obj inspection status field and getCurrentInspectionStatus func
  1.Add Icon field
  2.Initialize Icon from itemView - find how to define its R.id, how to connect Icon Image to this field
  3.Write setIconColour function + set it by that func in Bind
  4.Write getInspectionStatus function - check the inspection current status of a boat and compare it to the prev one
      if there's a change - change colour accordingly
  5.Integrate this with BoatForInspectionActivity somehow (?)

 */