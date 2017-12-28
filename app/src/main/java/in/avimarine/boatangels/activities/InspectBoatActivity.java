package in.avimarine.boatangels.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.CheckBoxTriState;
import in.avimarine.boatangels.CheckBoxTriState.State;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of an
 * Avi Marine Innovations project: BoatAngels
 * first created by aayaffe on 17/12/2017.
 */

public class InspectBoatActivity extends AppCompatActivity {

  private static final String TAG = "InspectBoatActivity";
  private iDb db;
  private final List<Boat> boats = new ArrayList<>();
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boat_spinner)
  Spinner boats_spinner;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.message_linedEditText)
  EditText inspection_text;
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
  private BoatSpinnerAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspect_boat_graphic);
    ButterKnife.bind(this);
    adapter  = new BoatSpinnerAdapter(this, boats);
    db = new FireBase();
    db.getBoatsInMarina("Shavit, Haifa", new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
          Log.d(TAG, "Received " + task.getResult().size() + " boats");
          for (DocumentSnapshot document : task.getResult()) {
            boats.add(document.toObject(Boat.class));
          }
          adapter.notifyDataSetChanged();
        } else {
          Log.d(TAG, "Error getting documents: ", task.getException());
          Toast.makeText(InspectBoatActivity.this, "Error connecting to online service!", Toast.LENGTH_LONG).show();
        }
      }
    });
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    boats_spinner.setAdapter(adapter);
    OnClickListener ocl = new OnClickListener() {
      @Override
      public void onClick(View view) {
        colorBoat();
      }
    };
    checkbox_stern.setOnClickListener(ocl);
    checkbox_bow.setOnClickListener(ocl);
    checkbox_jib.setOnClickListener(ocl);
    checkbox_main.setOnClickListener(ocl);
    colorBoat();

  }

  @OnClick(R.id.send_inspection_btn)
  public void onClick(View v) {
    Inspection inspection = new Inspection();
    if (boats_spinner.getSelectedItem()==null)
    {
      Toast.makeText(this,"No boat was selected",Toast.LENGTH_SHORT).show();
      return;
    }
    inspection.boatUid = ((Boat)boats_spinner.getSelectedItem()).getUuid();
    inspection.boatName = ((Boat)boats_spinner.getSelectedItem()).name;
    inspection.message = inspection_text.getText().toString();
    inspection.inspectionTime = new Date().getTime();
    inspection.inspectorUid = FirebaseAuth.getInstance().getUid();
    if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
      inspection.inspectorName = FirebaseAuth.getInstance().getCurrentUser()
          .getDisplayName(); //TODO: Switch to using name from User object
    }
    inspection.finding = getCheckBoxes();
    db.addInspection(inspection);
    finish();
  }



  private void colorBoat() {
    if (checkbox_bow.getState()==State.VCHECKED){
      final ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.VCheckedRopes);
      final Drawable drawable = ResourcesCompat
          .getDrawable(getResources(), R.drawable.ic_moored_sailing_boat_bowlines, wrapper.getTheme());
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

  private Map<String,String> getCheckBoxes() {
    Map<String,String> ret =new HashMap<>();
    ret.put("BOWLINES",checkbox_bow.getState().name());
    ret.put("JIB",checkbox_jib.getState().name());
    ret.put("MAIN",checkbox_main.getState().name());
    ret.put("STERNLINES",checkbox_stern.getState().name());
    return ret;
  }





  private class BoatSpinnerAdapter extends ArrayAdapter<Boat> {

    /**
     * The internal data (the ArrayList with the Objects).
     */
    private final List<Boat> data;
    private final Context context;

    public BoatSpinnerAdapter(Context context,
        List<Boat> values) {
      super(context, android.R.layout.simple_spinner_item,values);
      this.context = context;
      this.data = values;
    }

    /**
     * Returns the Size of the ArrayList
     */
    @Override
    public int getCount() {
      return data.size();
    }

    /**
     * Returns one Element of the ArrayList
     * at the specified position.
     */
    @Override
    public Boat getItem(int position) {
      return data.get(position);
    }

    @Override
    public long getItemId(int i) {
      return i;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
      return populateView(position,convertView,parent);
    }
    @Override
    public View getDropDownView(int position, View convertView,
        @NonNull ViewGroup parent) {
      return populateView(position,convertView,parent);
    }
    private View populateView(int position, View convertView,
        @NonNull ViewGroup parent){
      View listItem = convertView;
      if(listItem == null)
        listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item,parent,false);

      Boat boat = boats.get(position);

      TextView name = listItem.findViewById(android.R.id.text1);
      name.setText(boat.name);

      return listItem;
    }
  }
}

