package in.avimarine.boatangels.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import in.avimarine.boatangels.R;
import in.avimarine.boatangels.db.objects.Boat;

public class AskInspection extends AppCompatActivity {

  private String yachtiePoint;
  private FirebaseFirestore db = FirebaseFirestore.getInstance();;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ask_for_inspection);
    EditText pointOffer = (EditText)findViewById(R.id.yachtie_point_editxt);
    yachtiePoint = pointOffer.toString();
    inspectMe(yachtiePoint);
  }


  public void inspectMe(String yachtiePoint){
  CollectionReference boatRef = db.collection("boats");
  Query getBoat = boatRef.whereEqualTo("users", FirebaseAuth.getInstance().getUid());
    DocumentReference docRef = db.collection("boats").document(getBoat.toString());
    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
      @Override
      public void onSuccess(DocumentSnapshot documentSnapshot) {
        Boat boat = documentSnapshot.toObject(Boat.class);
        TextView test = (TextView)findViewById(R.id.textView4);
        test.setText(boat.toString());
      }
    });
}


}
