package in.avimarine.boatangels.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Inspection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
  @BindView(R.id.inspection_editText)
  EditText inspection_text;
  private BoatSpinnerAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inspect_boat);
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

  }

  @OnClick(R.id.submit_inspection_btn)
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
    db.addInspection(inspection);
    finish();
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

