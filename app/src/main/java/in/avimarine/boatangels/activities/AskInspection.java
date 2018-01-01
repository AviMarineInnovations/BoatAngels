package in.avimarine.boatangels.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;
import in.avimarine.boatangels.db.objects.User;

public class AskInspection extends AppCompatActivity {

  private Boat inspectBoat;
  private User user;
  private String getBoatUid ;
  private String yachtiePoint;
  private FirebaseFirestore db = FirebaseFirestore.getInstance();



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ask_for_inspection);
    EditText pointOffer = (EditText) findViewById(R.id.yachtie_point_editxt);
    yachtiePoint = pointOffer.getText().toString();
    inspectMe(yachtiePoint);
  }


  public void inspectMe(String yachtiePoint) {
    DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        user = documentSnapshot.toObject(User.class);
        getBoatUid = user.getBoats().toString();
        String Uid = getBoatUid.replaceAll("[]\\[]","");
        getBoat(Uid);
      }
    });


  }

  public void getBoat(final String Uid) {
    DocumentReference doc = db.collection("boats").document(Uid);
    doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        inspectBoat = documentSnapshot.toObject(Boat.class);

        TextView test = (TextView) findViewById(R.id.textView4);
        test.setText(inspectBoat.toString());
      }
    });

  }
}