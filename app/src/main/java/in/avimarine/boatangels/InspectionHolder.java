package in.avimarine.boatangels;


/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 23/12/2017.
 */
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import in.avimarine.boatangels.db.objects.Inspection;
import java.util.Date;


public class InspectionHolder extends RecyclerView.ViewHolder {
  private final TextView mDateField;
  private final TextView mTextField;
  private final RelativeLayout mMessageContainer;
  private final LinearLayout mMessage;


  public InspectionHolder(View itemView) {
    super(itemView);
    mDateField = itemView.findViewById(R.id.date_time);
    mTextField = itemView.findViewById(R.id.inspection_text);
    mMessageContainer = itemView.findViewById(R.id.message_container);
    mMessage = itemView.findViewById(R.id.message);
  }

  public void bind(Inspection i) {
    setDate(new Date(i.inspectionTime).toString());
    setText(i.message);
  }

  private void setDate(String name) {
    mDateField.setText(name);
  }

  private void setText(String text) {
    mTextField.setText(text);
  }

}