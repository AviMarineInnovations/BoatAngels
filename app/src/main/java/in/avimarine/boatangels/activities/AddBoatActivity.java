package in.avimarine.boatangels.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.FireBase;
import in.avimarine.boatangels.db.iDb;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.Marina;
import in.avimarine.boatangels.db.objects.User;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddBoatActivity extends AppCompatActivity {

  private static final String TAG = "AddBoatActivity";
  private static final int REQUEST_CODE = 2547;
  private final List<Marina> marinas = new ArrayList<>();
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.marina_spinner)
  Spinner marinaSpinner;
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
  @SuppressWarnings("WeakerAccess")
  @BindView(R.id.boat_club_et)
  EditText boatClubEt;
  private iDb db;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_boat);
    ButterKnife.bind(this);
    MarinaSpinnerAdapter adapter = new MarinaSpinnerAdapter(this, marinas);
    db = new FireBase();
    db.getMarinasInCountry("Israel", task -> {
      if (task.isSuccessful()) {
        Log.d(TAG, "Received " + task.getResult().size() + " boats");
        for (DocumentSnapshot document : task.getResult()) {
          marinas.add(document.toObject(Marina.class));
        }
        adapter.notifyDataSetChanged();
      } else {
        Log.d(TAG, "Error getting documents: ", task.getException());
        Toast.makeText(AddBoatActivity.this, "Error connecting to online service!",
            Toast.LENGTH_LONG).show();
      }
    });
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    marinaSpinner.setAdapter(adapter);
  }

  @OnClick(R.id.add_boat_btn)
  public void onAddBtnClick(View v) {

    Boat b = new Boat();
    if (marinaSpinner.getSelectedItem() == null) {
      TextView tv = (TextView) marinaSpinner.getSelectedView();
      if (tv != null) {
        tv.setError("No marina was selected");
      } else {
        Toast.makeText(this, "No marina was selected", Toast.LENGTH_SHORT).show();
      }
      return;
    }
    b.setMarinaUuid(((Marina) marinaSpinner.getSelectedItem()).getUuid());
    b.setMarinaName(((Marina) marinaSpinner.getSelectedItem()).getName());
    b.setName(boatNameEt.getText().toString());
    b.setModel(boatModelEt.getText().toString());
    b.setClubName(boatClubEt.getText().toString());
    Double lat = GeneralUtils.tryParseDouble(boatLatEt.getText().toString());
    Double lon = GeneralUtils.tryParseDouble(boatLonEt.getText().toString());
    cordinateValidity(isValidLat(lat), boatLatEt);
    cordinateValidity(isValidLon(lon), boatLonEt);
    b.setLocation(new GeoPoint(lat, lon));
    b.setLastUpdate(new Date());
    b.setFirstAddedTime(new Date());
    b.users.add(FirebaseAuth.getInstance().getUid());
    b.setCode(Boat.generateAccessCode());
    User u = db.getCurrentUser();
    u.getBoats().add(b.getUuid());
    db.setUser(u);
    db.addBoat(b);
    finish();
  }

  public void cordinateValidity(boolean vaild, EditText boatCordEt){
    if (!vaild){
      boatCordEt.setError("Out of range");
      boatCordEt.requestFocus();
    }
  }

  @OnClick(R.id.select_btn)
  public void onSelectBtnClick(View v) {
    Intent intent = new Intent(this, SearchBoatActivity.class);
    startActivityForResult(intent, REQUEST_CODE);
  }

  private boolean isValidLon(Double val) {
    return val != null && (val >= -180) && (val <= 180);
  }

  private boolean isValidLat(Double val) {
    return val != null && (val >= -90) && (val <= 90);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      addUserToBoat(data.getStringExtra("UUID"));
    }

  }

  private void addUserToBoat(String uuid) {
    db.getBoat(uuid, task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        if (document.exists()) {
          Boat b = document.toObject(Boat.class);
          User u = db.getCurrentUser();
          if (u != null) {
            b.users.add(u.getUid());
            u.getBoats().add(b.getUuid());
            db.setUser(u);
            db.addBoat(b);
          } else {
            Log.e(TAG, "Unable to add boat to user. currentUser is null");
          }
        } else {
          Log.e(TAG, "Unable to add boat to user");
          Toast.makeText(this,"Unable to add the selected boat",Toast.LENGTH_LONG).show();
        }
        finish();
      }
    });
  }

  private class MarinaSpinnerAdapter extends ArrayAdapter<Marina> {

    /**
     * The internal data (the ArrayList with the Objects).
     */
    private final List<Marina> data;
    private final Context context;

    MarinaSpinnerAdapter(Context context,
        List<Marina> values) {
      super(context, android.R.layout.simple_spinner_item, values);
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
      return populateView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView,
        @NonNull ViewGroup parent) {
      return populateView(position, convertView, parent);
    }

    private View populateView(int position, View convertView,
        @NonNull ViewGroup parent) {
      View listItem = convertView;
      if (listItem == null) {
        listItem = LayoutInflater
            .from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
      }

      Marina marina = marinas.get(position);

      TextView name = listItem.findViewById(android.R.id.text1);
      name.setText(marina.getName());

      return listItem;
    }
  }

}
