package in.avimarine.boatangels.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query.Direction;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Inspection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


public class MyInspection extends AppCompatActivity {


  private String uid = FirebaseAuth.getInstance().getUid();
  private final FirebaseFirestore db = FirebaseFirestore.getInstance();
  private List<String> arrayInspe = new ArrayList<>();
  private SparseArray<String> hashMap = new SparseArray<>();
  private SparseArray<String> hashMapInspeUid = new SparseArray<>();
  private static final String TAG = "INSPECTION_LIST";
  private Integer indexList = 0;
  private Context context = this;
  private String inspecUid;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_inspection);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayInspe);
    ListView listView = findViewById(R.id.my_inspection_list);

    String inspecBoat = getString(R.string.inspect_boat);
    String inspecDate = getString(R.string.inspect_date);
    String pointsEarned = getString(R.string.points_earned);

    db.collection("inspections").orderBy("inspectionTime", Direction.DESCENDING)
        .get()
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            for (DocumentSnapshot document : task.getResult()) {
              Inspection inspec = document.toObject(Inspection.class);
              if (inspec.getInspectorUid().equals(uid)) {
                String inspeData = DateFormat.getDateInstance().format(inspec.getInspectionTime());
                arrayInspe.add(inspecBoat + inspec.getBoatName() + "\n" +
                    inspecDate + inspeData + "\n" +
                    pointsEarned + inspec.getPointsEarned());
                Log.d(TAG, document.getId() + " => " + document.getData());

                hashMap.put(indexList, inspec.getBoatUuid());
                hashMapInspeUid.put(indexList, inspec.getUuid());
                indexList++;
              }
            }
            listView.setAdapter(arrayAdapter);
          } else {
            Log.d(TAG, "Cannot find inspection");
          }

        });
    listView.setOnItemClickListener((arg0, v, position, arg3) -> {

      inspecUid = hashMapInspeUid.get(position);
      String uid = hashMap.get(position);
      Log.d(TAG, "Name : " + uid + " " + position);

      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

      // set title
      alertDialogBuilder.setTitle(getString(R.string.what_you_like_to_do));

      // set dialog message
      alertDialogBuilder
          .setCancelable(true)
          .setPositiveButton(getString(R.string.new_inspection),
              (dialog, id) -> {

                Intent intent = new Intent(MyInspection.this, InspectBoatActivity.class);
                intent.putExtra(getString(R.string.intent_extra_boat_uuid), uid);
                startActivity(intent);
              })
          .setNegativeButton(getString(R.string.view_inspection),
              (dialog, id) -> {
                Intent intent1 = new Intent(MyInspection.this, InspectionResultActivity.class);
                intent1.putExtra(getString(R.string.intent_extra_inspection_uuid), inspecUid);
                startActivity(intent1);
              });

      // create alert dialog
      AlertDialog alertDialog = alertDialogBuilder.create();

      // show it
      alertDialog.show();
    });
  }

}








