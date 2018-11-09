package in.avimarine.boatangels;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import in.avimarine.boatangels.db.objects.Inspection;
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
  private final ImageView statusIcon;

  public InspectionHolder(View itemView) {
    super(itemView);
    mDateField = itemView.findViewById(R.id.date_time);
    mTextField = itemView.findViewById(R.id.inspection_text);
    statusIcon = itemView.findViewById(R.id.inspection_status_icon);
  }

  public void bind(Inspection i) {
    Date date = new Date(i.getInspectionTime());
    setDate(DateFormat.format("dd.MM.yyyy HH:mm", date).toString());
    setText(i.getMessage());
    i.setInspectionIcon(statusIcon);
  }

  private void setDate(String name) {
    mDateField.setText(name);
  }
  private void setText(String text) {
    mTextField.setText(text);
  }

}