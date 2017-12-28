package in.avimarine.boatangels.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import in.avimarine.boatangels.CheckBoxTriState;
import in.avimarine.boatangels.CheckBoxTriState.State;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Inspection;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.Date;
import java.util.Map;

public class InspectionResultActivity extends AppCompatActivity {

  private static final String TAG = "InspectionResultActivit";
  @BindView(R.id.message_linedEditText)
  TextView message;
  @BindView(R.id.inspection_title)
  TextView title;
  @BindView(R.id.inspection_subtitle)
  TextView subtitle;
  @BindView(R.id.checkBox_bow)
  CheckBoxTriState checkbox_bow;
  @BindView(R.id.checkBox_jib)
  CheckBoxTriState checkbox_jib;
  @BindView(R.id.checkBox_mainsail)
  CheckBoxTriState checkbox_main;
  @BindView(R.id.checkBox_stern)
  CheckBoxTriState checkbox_stern;
  @BindView(R.id.moored_boat_body)
  ImageView boatBody;
  @BindView(R.id.moored_boat_bowlines)
  ImageView boatBowLines;
  @BindView(R.id.moored_boat_sternlines)
  ImageView boatSternLines;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspection_result);
    ButterKnife.bind(this);
    Intent i = getIntent();
    String uuid = i.getStringExtra(getString(R.string.intent_extra_inspection_uuid));

    final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
    final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
    boatBody.setImageDrawable(drawable);
    iDb db = new FireBase();
    db.getInspection(uuid, new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult();
          if (document != null) {
            Inspection i = document.toObject(Inspection.class);
            updateUI(i);
          } else {
            Log.d(TAG, "No such document");
          }
        } else {
          Log.d(TAG, "get failed with ", task.getException());
        }
      }
    });

  }

  @OnClick(R.id.checkBox_bow)
  public void onCheckBoxChanged(View v){
    final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.XCheckedRopes);
    final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
    boatBody.setImageDrawable(drawable);
    Log.d(TAG,"Checkbox clicked");
  }

  public void updateUI(Inspection i){
    GeneralUtils.enableAndShowViews(title,subtitle,message,boatBody,boatBowLines,boatSternLines);
    GeneralUtils.showViews(checkbox_bow,checkbox_jib,checkbox_main,checkbox_stern);
    title.setText("Inspection for " + i.boatName);
    subtitle.setText("Made by " + i.inspectorName+" on " + GeneralUtils.toFormatedDateString(this,new Date(i.inspectionTime)));
    message.setText(i.message);
    setCheckBoxes(i);
    colorBoat();

  }

  private void colorBoat() {
    if (checkbox_bow.getState()==State.VCHECKED){
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.VCheckedRopes);
      final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines, wrapper.getTheme());
      boatBowLines.setImageDrawable(drawable);
    }
    else if (checkbox_bow.getState()==State.XCHECKED){
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.XCheckedRopes);
      final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines, wrapper.getTheme());
      boatBowLines.setImageDrawable(drawable);
    }
    else {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
      final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines, wrapper.getTheme());
      boatBowLines.setImageDrawable(drawable);
    }
    if (checkbox_stern.getState()==State.VCHECKED){
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.VCheckedRopes);
      final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines, wrapper.getTheme());
      boatSternLines.setImageDrawable(drawable);
    }
    else if (checkbox_stern.getState()==State.XCHECKED){
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.XCheckedRopes);
      final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines, wrapper.getTheme());
      boatSternLines.setImageDrawable(drawable);
    }
    else {
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
      final Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_sternlines, wrapper.getTheme());
      boatSternLines.setImageDrawable(drawable);
    }
    ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.UncheckedBoat);
    Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
    boatBody.setImageDrawable(drawable);
    if (checkbox_jib.getState()==State.VCHECKED&&checkbox_main.getState()==State.VCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.VCheckedJibVCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.VCHECKED&&checkbox_main.getState()==State.XCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.VCheckedJibXCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.XCHECKED&&checkbox_main.getState()==State.VCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.XCheckedJibVCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.XCHECKED&&checkbox_main.getState()==State.XCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.XCheckedJibXCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.UNCHECKED&&checkbox_main.getState()==State.XCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.UnCheckedJibXCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.UNCHECKED&&checkbox_main.getState()==State.VCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.UnCheckedJibVCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.XCHECKED&&checkbox_main.getState()==State.UNCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.XCheckedJibUnCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
    else if (checkbox_jib.getState()==State.VCHECKED&&checkbox_main.getState()==State.UNCHECKED){
      wrapper = new ContextThemeWrapper(this, R.style.VCheckedJibUnCheckedMain);
      drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_body_sails, wrapper.getTheme());
      boatBody.setImageDrawable(drawable);
    }
  }

  private void setCheckBoxes(Inspection i) {
    if (i.finding==null){
      GeneralUtils.disableAndHideViews(true,checkbox_bow,checkbox_jib,checkbox_main,checkbox_stern,boatBody,boatBowLines,boatSternLines);
      return;
    }
    setCheckBox(i.finding,"BOWLINES", checkbox_bow);
    setCheckBox(i.finding,"JIB", checkbox_jib);
    setCheckBox(i.finding,"MAIN", checkbox_main);
    setCheckBox(i.finding,"STERNLINES", checkbox_stern);
  }

  private void setCheckBox(Map<String,String> m, String key, CheckBoxTriState cb) {
    if (m.containsKey(key)){
      if (m.get(key).equals(State.UNCHECKED.name())){
        cb.setState(State.UNCHECKED);
      }
      else if (m.get(key).equals(State.VCHECKED.name())){
        cb.setState(State.VCHECKED);
      }
      else if (m.get(key).equals(State.XCHECKED.name())){
        cb.setState(State.XCHECKED);
      }
    }
  }

}
