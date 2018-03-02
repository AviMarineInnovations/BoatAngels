package in.avimarine.boatangels.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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

    db.collection("inspections")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
              for (DocumentSnapshot document : task.getResult()) {
                Inspection inspec = document.toObject(Inspection.class);
                if (inspec.inspectorUid.equals(uid)) {
                  String inspeData = DateFormat.getDateInstance().format(inspec.inspectionTime);
                  arrayInspe.add(inspecBoat + inspec.boatName + "\n" +
                      inspecDate + inspeData + "\n" +
                      pointsEarned + inspec.pointsEarned);
                  Log.d(TAG, document.getId() + " => " + document.getData());

                  hashMap.put(indexList, inspec.boatUuid);
                  hashMapInspeUid.put(indexList, inspec.getUuid());
                  indexList++;
                }
              }
              listView.setAdapter(arrayAdapter);
            } else {
              Log.d(TAG, "Cannot find inspection");
            }

          }
        });
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View v, int position,
          long arg3) {

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
                new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int id) {

                    Intent intent = new Intent(MyInspection.this, InspectBoatActivity.class);
                    intent.putExtra(getString(R.string.intent_extra_boat_uuid), uid);
                    startActivity(intent);
                  }
                })
            .setNegativeButton(getString(R.string.view_inspection),
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                    Intent intent1 = new Intent(MyInspection.this, InspectionResultActivity.class);
                    intent1.putExtra(getString(R.string.intent_extra_inspection_uuid), inspecUid);
                    startActivity(intent1);
                  }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
      }
    });
  }

}








