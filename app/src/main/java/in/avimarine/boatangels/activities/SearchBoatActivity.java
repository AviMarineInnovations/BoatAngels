package in.avimarine.boatangels.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.dialogs.BoatCodeInputDialog;
import in.avimarine.boatangels.dialogs.BoatCodeInputDialog.BoatCodeInputDialogListener;
import in.avimarine.boatangels.general.GeneralUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchBoatActivity extends AppCompatActivity implements BoatCodeInputDialogListener {

  private static final String TAG = "SearchBoatActivity";
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private final List<Boat> boatList = new ArrayList<>();
  private Boat selectedBoat = null;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_boat);
    SearchView mSearchView = findViewById(R.id.search_view);
    ListView mListView = findViewById(R.id.list_view);
    getSupportActionBar().setTitle("Select boat");
    ArrayAdapter<Boat> arrayAdapter = new ArrayAdapter<>(this, android.R.
        layout.simple_list_item_1, boatList);
    mListView.setOnItemClickListener((adapterView, view, i, l) -> {
      selectedBoat = boatList.get(i);
      selectBoat(selectedBoat);
    });
    db.collection("boats").
        get().
        addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
          if (task.isSuccessful()) {
            for (DocumentSnapshot document : task.getResult()) {
              Boat boat = document.toObject(Boat.class);
              boatList.add(boat);
            }
            Collections.sort(boatList,
                (boat, t1) -> boat.getName().compareToIgnoreCase(t1.getName()));
            mListView.setAdapter(arrayAdapter);
            mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String s) {
                return false;
              }

              @Override
              public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
              }
            });
          }
        });
  }

  private void selectBoat(Boat b) {
    if (b==null){
      Toast.makeText(this,"Unable to get selected boat",Toast.LENGTH_LONG).show();
      Log.e(TAG, "Unable to get selected boat");
      return;
    }
    if(b.getCode()==null){
      Toast.makeText(this,"This boat has no access code. Contact the developers for help",Toast.LENGTH_LONG).show();
      Log.e(TAG,"No access code for boat "+ selectedBoat.getName());
      return;
    }
    DialogFragment df = BoatCodeInputDialog.newInstance(this);
    df.show(getFragmentManager(), "Enter_Access_Code");


  }
  @Override
  public void onBoatCodeDialogPositiveClick(DialogFragment dialog) {
    EditText accessCodeText = dialog.getDialog().findViewById(R.id.access_code);
    if (accessCodeText==null)
      return;
    if (accessCodeText.getText()==null)
      return;
    if (selectedBoat==null)
      return;
    if (GeneralUtils.isValid(accessCodeText.getText().toString(),Long.class,0f,999999f)&&selectedBoat.getCode().equals(accessCodeText.getText().toString())) {
      Intent returnIntent = new Intent();
      returnIntent.putExtra("UUID",selectedBoat.getUuid());
      setResult(RESULT_OK,returnIntent);
      finish();
    }
  }
}
