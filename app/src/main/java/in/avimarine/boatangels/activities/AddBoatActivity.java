package in.avimarine.boatangels.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddBoatActivity extends AppCompatActivity {

  private static final String TAG = "AddBoatActivity";
  private iDb db;
  private final List<Marina> marinas = new ArrayList<>();

  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.marina_spinner)
  Spinner marina_spinner;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boatname_et)
  EditText boatNameEt;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boat_model_et)
  EditText boatModelEt;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.latitude_et)
  EditText boatLatEt;
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.longitude_et)
  EditText boatLonEt;


  private MarinaSpinnerAdapter adapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_boat);
    ButterKnife.bind(this);
    adapter  = new MarinaSpinnerAdapter(this, marinas);
    db = new FireBase();
    db.getMarinasInCountry("Israel", new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
          Log.d(TAG, "Received " + task.getResult().size() + " boats");
          for (DocumentSnapshot document : task.getResult()) {
            marinas.add(document.toObject(Marina.class));
          }
          adapter.notifyDataSetChanged();
        } else {
          Log.d(TAG, "Error getting documents: ", task.getException());
          Toast.makeText(AddBoatActivity.this, "Error connecting to online service!", Toast.LENGTH_LONG).show();
        }
      }
    });
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    marina_spinner.setAdapter(adapter);
  }
  @OnClick(R.id.add_boat_btn)
  public void onClick(View v) {
    Boat b = new Boat();
    if (marina_spinner.getSelectedItem()==null)
    {
      TextView tv = (TextView) marina_spinner.getSelectedView();
      if (tv!=null)
        tv.setError("No marina was selected");
      else
        Toast.makeText(this,"No marina was selected",Toast.LENGTH_SHORT).show();
      return;
    }
    b.marinaUuid = ((Marina)marina_spinner.getSelectedItem()).getUuid();
    b.marinaName = ((Marina)marina_spinner.getSelectedItem()).name;
    b.name = boatNameEt.getText().toString();
    b.model = boatModelEt.getText().toString();
    Double lat = GeneralUtils.tryParseDouble(boatLatEt.getText().toString());
    Double lon = GeneralUtils.tryParseDouble(boatLonEt.getText().toString());
    if (!isValidLat(lat)){
      boatLatEt.setError("Out of range");
      boatLatEt.requestFocus();
      return;
    }
    if (!isValidLon(lon)){
      boatLonEt.setError("Out of range");
      boatLonEt.requestFocus();
      return;
    }
    b.location = new GeoPoint(lat, lon);
    b.setLastUpdate(new Date());
    b.setFirstAddedTime(new Date());
    b.users.add(FirebaseAuth.getInstance().getUid());
    db.addBoat(b);
    finish();
  }

  private boolean isValidLon(Double val) {
    return val != null && (val >= -180) && (val <= 180);
  }

  private boolean isValidLat(Double val) {
    return val != null && (val >= -90) && (val <= 90);
  }


  private class MarinaSpinnerAdapter extends ArrayAdapter<Marina> {

    /**
     * The internal data (the ArrayList with the Objects).
     */
    private final List<Marina> data;
    private final Context context;

    public MarinaSpinnerAdapter(Context context,
        List<Marina> values) {
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
    public Marina getItem(int position) {
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
        listItem = LayoutInflater
            .from(context).inflate(android.R.layout.simple_spinner_item,parent,false);

      Marina marina = marinas.get(position);

      TextView name = listItem.findViewById(android.R.id.text1);
      name.setText(marina.name);

      return listItem;
    }
  }

}
